package ru.cdecl.pub.iota.servlets;

import org.eclipse.jetty.util.ArrayQueue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.mechanics.GamePlayer;
import ru.cdecl.pub.iota.mechanics.GameSession;
import ru.cdecl.pub.iota.models.UserProfile;
import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.Player;
import ru.cdecl.pub.iota.services.AccountService;
import ru.cdecl.pub.iota.services.game.GameSessionService;
import ru.cdecl.pub.iota.servlets.base.JsonApiServlet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

@Service
@Singleton
@WebServlet
public class GameServlet extends JsonApiServlet {

    @Inject
    GameSessionService gameSessionService;

    @Inject
    AccountService accountService;

    final Object waitingLock = new Object();
    final Queue<Player> waitingPlayers = new ArrayQueue<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            final UserProfile userProfile = accountService.getUserProfile((Long) httpSession.getAttribute("user_id"));
            final Player player = new Player(userProfile);

            waitingPlayers.add(player);
            int nPlayers = 2;
            Long gameSessionId = null;
            if (waitingPlayers.size() < nPlayers) {
                while (true) {
                    try {
                        synchronized (waitingLock) {
                            waitingLock.wait();
                        }
                    } catch (InterruptedException ignored) {
                    }
                    if (player.sessionId != null) {
                        gameSessionId = player.sessionId;
                        break;
                    }
                }
            } else {
                final List<Player> players = new LinkedList<>();
                while (nPlayers > 0) {
                    players.add(waitingPlayers.remove());
                    nPlayers--;
                }
                gameSessionId = gameSessionService.spawnGameSessionForPlayers(players);
                for (Player otherPlayer : players) {
                    otherPlayer.sessionId = gameSessionId;
                }
                synchronized (waitingLock) {
                    waitingLock.notifyAll();
                }
            }
            httpSession.setAttribute("game_session_id", gameSessionId);
            jsonWriter.key("game_session_id").value(gameSessionId);
        }

        jsonWriter.endObject();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gameSessionService.cleanUpDeadSessions();

        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            final GameSession gameSession = gameSessionService.getGameSessionById((Long) httpSession.getAttribute("game_session_id"));
            if (gameSession != null) {
                jsonWriter.key("table").value(toJsonArray(gameSession.playingField.cards));
                jsonWriter.key("players");
                jsonWriter.array();
                for (GamePlayer gamePlayer : gameSession.gamePlayers) {
                    jsonWriter.object();
                    jsonWriter.key("id").value(gamePlayer.getPlayer().getUserProfile().getId());
                    jsonWriter.key("name").value(gamePlayer.getPlayer().getUserProfile().getLogin());
                    jsonWriter.key("cards").array();
                    for (Card card : gamePlayer.handCards) {
                        jsonWriter.value(cardToJsonObject(card));
                    }
                    jsonWriter.endArray();
                    jsonWriter.endObject();
                }
                jsonWriter.endArray();
                jsonWriter.key("turn_player_id").value(gameSession.getCurrentGamePlayer().getPlayer().getUserProfile().getId());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonWriter.key("err").value("Game session not found.");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonWriter.key("err").value("No http session.");
        }

        jsonWriter.endObject();
    }

    // todo: переписать
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gameSessionService.cleanUpDeadSessions();

        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            final GameSession gameSession = gameSessionService.getGameSessionById((Long) httpSession.getAttribute("game_session_id"));
            if (gameSession != null) {
                final Long userIdFromGameSession = gameSession.getCurrentGamePlayer().getPlayer().getUserProfile().getId(); // fixme: unsee it
                final Long userIdFromHttpSession = (Long) httpSession.getAttribute("user_id");
                if (userIdFromGameSession != null && userIdFromGameSession.equals(userIdFromHttpSession)) {
                    final UserProfile userProfile = accountService.getUserProfile(userIdFromHttpSession);
                    if (userProfile != null) {
                        final JSONObject jsonRequest = getJsonObjectFromHttpRequest(req);
                        //noinspection OverlyBroadCatchBlock
                        try {
                            if (!gameTurn(new Player(userProfile), gameSession, jsonRequest, jsonWriter)) {
                                resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                                jsonWriter.key("err").value("Unknown error.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            jsonWriter.key("err").value("Unknown exception.");
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        jsonWriter.key("err").value("User not found.");
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    jsonWriter.key("err").value("Liar, liar, pants on fire.");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonWriter.key("err").value("Game session not found.");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonWriter.key("err").value("No http session.");
        }

        jsonWriter.endObject();
    }

    // todo: вынести в отдельный сервис гейм-механики
    private boolean gameTurn(Player player, GameSession gameSession, JSONObject jsonRequest, JSONWriter jsonWriter) {
        //noinspection ConstantConditions
//        if (gameSession.getCurrentGamePlayer().getPlayer().getUserProfile().getId().equals(player.getUserProfile().getId())) {
//            return false;
//        }
        final Card[][] newState = toCardArray(jsonRequest);
        if (!gameSession.updatePlayingField(newState)) {
            return false;
        }
        jsonWriter.key("table").value(toJsonArray(gameSession.playingField.cards));
        final JSONArray userCardsOld = jsonRequest.getJSONArray("cards");
        int userCardsCount = userCardsOld.length();
        Collection<Card> handCards = gameSession.getCurrentGamePlayer().handCards;
        handCards.clear();
        //noinspection Convert2Lambda,Convert2Lambda
        userCardsOld.forEach(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                JSONObject jsonObject = (JSONObject)o;
                handCards.add(new Card(
                        getColorFromString(jsonObject.getString("color")),
                        getShapeFromString(jsonObject.getString("shape")),
                        jsonObject.getInt("value")));
            }
        });
        final JSONArray newCards = new JSONArray();
        for (; userCardsCount < 4; ++userCardsCount) {
            final Card card = gameSession.drawCard();
            newCards.put(cardToJsonObject(card));
            userCardsOld.put(cardToJsonObject(card));
            handCards.add(card);
        }
        jsonWriter.key("cards").value(new JSONArray(handCards));
        jsonWriter.key("new_cards").value(newCards);
        gameSession.endTurn();
        jsonWriter.key("players");
        jsonWriter.array();
        for (GamePlayer gamePlayer : gameSession.gamePlayers) {
            final UserProfile userProfile = gamePlayer.getPlayer().getUserProfile();
            jsonWriter.object();
            jsonWriter.key("id").value(userProfile.getId());
            jsonWriter.key("name").value(userProfile.getLogin());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.key("turn_player_id").value(gameSession.getCurrentGamePlayer().getPlayer().getUserProfile().getId());
        return true;
    }

    private JSONObject cardToJsonObject(Card card) {
        if (card == null) {
            return null;
        }
        final JSONObject cardJsonObject = new JSONObject();
        cardJsonObject.put("color", getStringFromColor(card.getColor()));
        cardJsonObject.put("shape", getStringFromShape(card.getShape()));
        cardJsonObject.put("value", card.getValue());
        return cardJsonObject;
    }

    private Card[][] toCardArray(JSONObject json) {
        final JSONArray arr = json.getJSONArray("table");
        final Card[][] cards = new Card[GameSession.PlayingField.MAX_CARDS_IN_DIMENSION][GameSession.PlayingField.MAX_CARDS_IN_DIMENSION];
        //noinspection Convert2Lambda,AnonymousInnerClassMayBeStatic
        arr.forEach(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                final JSONObject tableObject = (JSONObject) o;
                final int x = tableObject.getInt("x");
                final int y = tableObject.getInt("y");
                final JSONObject cardObjec = tableObject.getJSONObject("card");
                final String shape = cardObjec.getString("shape");
                final String color = cardObjec.getString("color");
                final int value = cardObjec.getInt("value");
                if (shape != null && color != null && value > 0) {
                    cards[x][y] = new Card(getColorFromString(color), getShapeFromString(shape), value);
                }
            }
        });
        return cards;
    }

    private JSONArray toJsonArray(Card[][] cards) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < cards.length; ++i) {
            for (int j = 0; j < cards[i].length; ++j) {
                if (cards[i][j] != null) {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("x", i);
                    jsonObject.put("y", j);
                    jsonObject.put("card", cardToJsonObject(cards[i][j]));
                    jsonArray.put(jsonObject);
                }
            }
        }
        return jsonArray;
    }

    private Card.Color getColorFromString(String s) {
        switch (s) {
            case "r":
                return Card.Color.RED;
            case "g":
                return Card.Color.GREEN;
            case "y":
                return Card.Color.YELLOW;
            default:
                return Card.Color.BLUE;
        }
    }

    private String getStringFromColor(Card.Color color) {
        switch (color) {
            case RED:
                return "r";
            case GREEN:
                return "g";
            case YELLOW:
                return "y";
            default:
                return "b";
        }
    }

    private String getStringFromShape(Card.Shape shape) {
        switch (shape) {
            case CIRCLE:
                return "c";
            case SQUARE:
                return "r";
            case TRIANGLE:
                return "t";
            default:
                return "x";
        }
    }

    private Card.Shape getShapeFromString(String s) {
        switch (s) {
            case "c":
                return Card.Shape.CIRCLE;
            case "r":
                return Card.Shape.SQUARE;
            case "t":
                return Card.Shape.TRIANGLE;
            default:
                return Card.Shape.CROSS;
        }
    }

}
