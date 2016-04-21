package ru.cdecl.pub.iota.servlets;

import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.game.NoCardsInDeckException;
import ru.cdecl.pub.iota.mechanics.GamePlayer;
import ru.cdecl.pub.iota.mechanics.GameSession;
import ru.cdecl.pub.iota.models.UserProfile;
import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.CardDeckItem;
import ru.cdecl.pub.iota.models.game.Wildcard;
import ru.cdecl.pub.iota.services.AccountService;
import ru.cdecl.pub.iota.services.game.CardDeckService;
import ru.cdecl.pub.iota.services.game.GameSessionService;
import ru.cdecl.pub.iota.services.game.PlayingFieldService;
import ru.cdecl.pub.iota.servlets.base.JsonApiServlet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Service
@Singleton
@WebServlet
public class GameServlet extends JsonApiServlet {

    @Inject
    private GameSessionService gameSessionService;

    @Inject
    private AccountService accountService;

    private final Object waitingLock = new Object();
    private final Queue<UserProfile> waitingPlayers = new ConcurrentLinkedQueue<>();
    private final ConcurrentMap<UserProfile, Long> playerGameSessionIds = new ConcurrentHashMap<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            final UserProfile userProfile = accountService.getUserProfile((Long) httpSession.getAttribute("user_id"));

            waitingPlayers.add(userProfile);
            int nPlayers = PLAYERS_IN_SESSION;
            Long gameSessionId = null;
            if (waitingPlayers.size() < nPlayers) {
                while (true) {
                    try {
                        synchronized (waitingLock) {
                            waitingLock.wait();
                        }
                    } catch (InterruptedException ignored) {
                    }
                    if (playerGameSessionIds.containsKey(userProfile)) {
                        gameSessionId = playerGameSessionIds.get(userProfile);
                        break;
                    }
                }
            } else {
                final List<UserProfile> players = new LinkedList<>();
                while (nPlayers > 0) {
                    players.add(waitingPlayers.remove());
                    nPlayers--;
                }
                gameSessionId = gameSessionService.spawnGameSessionForPlayers(players);
                for (UserProfile otherPlayer : players) {
                    playerGameSessionIds.put(otherPlayer, gameSessionId);
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
        gameSessionService.cleanupDeadSessions();

        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            final GameSession gameSession = gameSessionService.getGameSessionById((Long) httpSession.getAttribute("game_session_id"));
            if (gameSession != null) {
                jsonWriter.key("table").value(toJsonArray(gameSession.getPlayingFieldService().getCards()));
                jsonWriter.key("players");
                jsonWriter.array();
                for (GamePlayer gamePlayer : gameSession.getGamePlayers()) {
                    jsonWriter.object();
                    jsonWriter.key("id").value(gamePlayer.getUserProfile().getId());
                    jsonWriter.key("name").value(gamePlayer.getUserProfile().getLogin());
                    jsonWriter.key("cards").array();
                    for (CardDeckItem card : gamePlayer.getHandCards()) {
                        jsonWriter.value(cardDeckItemToJsonObject(card));
                    }
                    jsonWriter.endArray();
                    jsonWriter.endObject();
                }
                jsonWriter.endArray();
                jsonWriter.key("turn_player_id").value(gameSession.getCurrentGamePlayer().getUserProfile().getId());
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gameSessionService.cleanupDeadSessions();

        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            final GameSession gameSession = gameSessionService.getGameSessionById((Long) httpSession.getAttribute("game_session_id"));
            if (gameSession != null) {
                final Long userIdFromGameSession = gameSession.getCurrentGamePlayer().getUserProfile().getId();
                final Long userIdFromHttpSession = (Long) httpSession.getAttribute("user_id");
                if (userIdFromGameSession != null && userIdFromGameSession.equals(userIdFromHttpSession)) {
                    final UserProfile userProfile = accountService.getUserProfile(userIdFromHttpSession);
                    if (userProfile != null) {
                        final JSONObject jsonRequest = getJsonObjectFromHttpRequest(req);
                        //noinspection OverlyBroadCatchBlock
                        try {
                            if (!gameTurn(userProfile, gameSession, jsonRequest, jsonWriter)) {
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
    private boolean gameTurn(UserProfile player, GameSession gameSession, JSONObject jsonRequest, JSONWriter jsonWriter) {
        //noinspection ConstantConditions
//        if (gameSession.getCurrentGamePlayer().getPlayer().getUserProfile().getId().equals(player.getUserProfile().getId())) {
//            return false;
//        }
        final Card[][] newState = toCardArray(jsonRequest);
        final PlayingFieldService playingFieldService = gameSession.getPlayingFieldService();
        if (!playingFieldService.setCards(newState)) {
            return false;
        }
        jsonWriter.key("table").value(toJsonArray(playingFieldService.getCards()));
        JSONArray userCardsOld = null;
        final JSONArray playersForCards = jsonRequest.getJSONArray("players");
        for (Object playerForCards : playersForCards) {
            final JSONObject playerObject = (JSONObject) playerForCards;
            if (Objects.equals(playerObject.getLong("id"), player.getId())) {
                userCardsOld = playerObject.getJSONArray("cards");
                break;
            }
        }
        if (userCardsOld == null) {
            return false;
        }
        int userCardsCount = userCardsOld.length();
        final Collection<CardDeckItem> handCards = gameSession.getCurrentGamePlayer().getHandCards();
        handCards.clear();
        // todo: wildcard !
        userCardsOld.forEach(o -> {
            final JSONObject jsonObject = (JSONObject) o;
            handCards.add(new Card(
                    Card.Color.fromString(jsonObject.getString("color")),
                    Card.Shape.fromString(jsonObject.getString("shape")),
                    jsonObject.getInt("value")));
        });
        final CardDeckService cardDeckService = gameSession.getCardDeckService();
        final JSONArray newCards = new JSONArray();
        for (; userCardsCount < 4; ++userCardsCount) {
            try {
                final CardDeckItem card = cardDeckService.drawCard();
                newCards.put(cardDeckItemToJsonObject(card));
                userCardsOld.put(cardDeckItemToJsonObject(card));
                handCards.add(card);
            } catch (NoCardsInDeckException ignored) {
            }
        }
        jsonWriter.key("cards").value(new JSONArray(handCards));
        jsonWriter.key("new_cards").value(newCards);
        gameSession.endTurn();
        jsonWriter.key("players");
        jsonWriter.array();
        for (GamePlayer gamePlayer : gameSession.getGamePlayers()) {
            final UserProfile userProfile = gamePlayer.getUserProfile();
            jsonWriter.object();
            jsonWriter.key("id").value(userProfile.getId());
            jsonWriter.key("name").value(userProfile.getLogin());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.key("turn_player_id").value(gameSession.getCurrentGamePlayer().getUserProfile().getId());
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

    @Nullable
    private JSONObject cardDeckItemToJsonObject(CardDeckItem cardDeckItem) {
        if (cardDeckItem == null) {
            return null;
        }
        if (cardDeckItem instanceof Card) {
            return cardToJsonObject((Card) cardDeckItem);
        } else if (cardDeckItem instanceof Wildcard) {
            final JSONArray jsonArray = new JSONArray();
            final Set<Card> substituteCards = ((Wildcard) cardDeckItem).getSubstituteCards();
            if (substituteCards == null) {
                throw new AssertionError();
            } else {
                substituteCards.forEach(card -> jsonArray.put(cardToJsonObject(card)));
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("wildcard", jsonArray);
            return jsonObject;
        } else {
            throw new AssertionError();
        }
    }

    private Card[][] toCardArray(JSONObject json) {
        final JSONArray arr = json.getJSONArray("table");
        final Card[][] cards = new Card[PlayingFieldService.MAX_CARDS_IN_DIMENSION][PlayingFieldService.MAX_CARDS_IN_DIMENSION];
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

    private JSONArray toJsonArray(CardDeckItem[][] cards) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < cards.length; ++i) {
            for (int j = 0; j < cards[i].length; ++j) {
                if (cards[i][j] != null) {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("x", i);
                    jsonObject.put("y", j);
                    jsonObject.put("card", cardDeckItemToJsonObject(cards[i][j]));
                    jsonArray.put(jsonObject);
                }
            }
        }
        return jsonArray;
    }

    private static final int PLAYERS_IN_SESSION = 2;

}
