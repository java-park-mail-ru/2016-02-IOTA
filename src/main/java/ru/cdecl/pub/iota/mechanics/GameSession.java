package ru.cdecl.pub.iota.mechanics;

import org.eclipse.jetty.util.ArrayQueue;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.base.GameException;
import ru.cdecl.pub.iota.exceptions.game.NoCardsInDeckException;
import ru.cdecl.pub.iota.models.UserProfile;
import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.CardDeckItem;
import ru.cdecl.pub.iota.models.game.Wildcard;
import ru.cdecl.pub.iota.services.game.CardDeckService;
import ru.cdecl.pub.iota.services.game.PlayingFieldService;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;

@Service
@PerLookup
public class GameSession {

    @Inject
    private CardDeckService cardDeckService;

    @Inject
    private PlayingFieldService playingFieldService;

    private Queue<GamePlayer> gamePlayers = new ArrayQueue<>();

    public void setPlayers(Iterable<UserProfile> players) {
        try {
            setPlayersInternal(players);
        } catch (NoCardsInDeckException e) {
            throw new AssertionError(e);
        }
    }

    private void setPlayersInternal(Iterable<UserProfile> players) throws NoCardsInDeckException {
        for (UserProfile player : players) {
            final List<CardDeckItem> handCards = new LinkedList<>();
            for (int i = 0; i < 4; ++i) {
                handCards.add(cardDeckService.drawCard());
            }
            gamePlayers.add(new GamePlayer(player, handCards));
        }
        playingFieldService.setInitialCard(cardDeckService.drawCard());
    }

    public void endTurn() {
        final GamePlayer gamePlayer = gamePlayers.remove();
        gamePlayers.add(gamePlayer);
    }

    public GamePlayer getCurrentGamePlayer() {
        return gamePlayers.peek();
    }

    public Collection<GamePlayer> getGamePlayers() {
        return Collections.unmodifiableCollection(gamePlayers);
    }

    public CardDeckService getCardDeckService() {
        return cardDeckService;
    }

    public PlayingFieldService getPlayingFieldService() {
        return playingFieldService;
    }

}
