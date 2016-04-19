package ru.cdecl.pub.iota.servlets;

import org.eclipse.jetty.util.ArrayQueue;
import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private GameSessionService gameSessionService;

    @Inject
    private AccountService accountService;

    private final Object waitingLock = new Object();
    private final Queue<Player> waitingPlayers = new ConcurrentArrayQueue<>();

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
            jsonWriter.key("id").value(gameSessionId);
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
                                jsonWriter.key("err").value("Such state, much invalid.");
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
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
        JSONArray userCardsOld = null;
        final JSONArray playersForCards = jsonRequest.getJSONArray("players");
        for (Object playerForCards : playersForCards) {
            final JSONObject playerObject = (JSONObject) playerForCards;
            if (Objects.equals((Long) playerObject.getLong("id"), player.getUserProfile().getId())) {
                userCardsOld = playerObject.getJSONArray("cards");
                break;
            }
        }
        if (userCardsOld == null) {
            return false;
        }
        int userCardsCount = userCardsOld.length();
        final Collection<Card> handCards = gameSession.getCurrentGamePlayer().handCards;
        handCards.clear();
        userCardsOld.forEach(o -> {
            final JSONObject jsonObject = (JSONObject) o;
            handCards.add(new Card(
                    Card.Color.fromString(jsonObject.getString("color")),
                    Card.Shape.fromString(jsonObject.getString("shape")),
                    jsonObject.getInt("value")));
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

    @Nullable
    private JSONObject cardToJsonObject(Card card) {
        if (card == null) {
            return null;
        }
        final JSONObject cardJsonObject = new JSONObject();
        cardJsonObject.put("color", card.getColor());
        cardJsonObject.put("shape", card.getShape());
        cardJsonObject.put("value", card.getValue());
        return cardJsonObject;
    }

    private Card[][] toCardArray(JSONObject json) {
        final JSONArray arr = json.getJSONArray("table");
        final Card[][] cards = new Card[GameSession.PlayingField.MAX_CARDS_IN_DIMENSION][GameSession.PlayingField.MAX_CARDS_IN_DIMENSION];
        arr.forEach(o -> {
            final JSONObject tableObject = (JSONObject) o;
            final int x = tableObject.getInt("x");
            final int y = tableObject.getInt("y");
            final JSONObject cardObject = tableObject.getJSONObject("card");
            final String shape = cardObject.getString("shape");
            final String color = cardObject.getString("color");
            final int value = cardObject.getInt("value");
            if (shape != null && color != null && value > 0) {
                cards[x][y] = new Card(Card.Color.fromString(color), Card.Shape.fromString(shape), value);
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

}
