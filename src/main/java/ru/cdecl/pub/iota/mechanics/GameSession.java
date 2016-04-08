package ru.cdecl.pub.iota.mechanics;

import org.eclipse.jetty.util.ArrayQueue;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.Player;
import ru.cdecl.pub.iota.services.game.CardDeckService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

@Service
@PerLookup
public class GameSession {

    @Inject
    CardDeckService cardDeckService;

    public Queue<GamePlayer> gamePlayers = new ArrayQueue<>();

    public PlayingField playingField;

    public void setPlayers(Iterable<Player> players) {
        players.forEach(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                final List<Card> handCards = new LinkedList<Card>();
                for (int i = 0; i < 4; ++i) {
                    handCards.add(cardDeckService.drawCard());
                }
                final GamePlayer gamePlayer = new GamePlayer(player, handCards);
                gamePlayers.add(gamePlayer);
            }
        });
        playingField = new PlayingField(cardDeckService.drawCard());
    }

    public GamePlayer getCurrentGamePlayer() {
        return gamePlayers.peek();
    }

    public boolean updatePlayingField(Card[][] cargs) {
        return playingField.updateCards(cargs);
    }

    public Card drawCard() {
        return cardDeckService.drawCard();
    }

    public void endTurn() {
        final GamePlayer gamePlayer = gamePlayers.remove();
        gamePlayers.add(gamePlayer);
    }

    public static class PlayingField {

        public static final int MAX_CARDS_IN_DIMENSION = 34;

        public Card[][] cards = new Card[MAX_CARDS_IN_DIMENSION][MAX_CARDS_IN_DIMENSION];

        public PlayingField(Card initialCard) {
            cards[CENTER_X][CENTER_Y] = initialCard;
        }

        public boolean updateCards(Card[][] cards) {
            if (!isValid(cards)) {
                return false;
            }
            this.cards = cards;
            return true;
        }

        private static boolean isValid(Card[][] field) {
            return true; // todo
        }

        public static final int CENTER_X = MAX_CARDS_IN_DIMENSION / 2;
        public static final int CENTER_Y = MAX_CARDS_IN_DIMENSION / 2;
    }

}
