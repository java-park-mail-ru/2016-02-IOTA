package ru.cdecl.pub.iota.services.game;

import org.eclipse.jetty.util.ArrayQueue;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.models.game.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

@Service
@PerLookup
public class CardDeckService {

    private Queue<Card> deck = new ArrayQueue<>(MAX_CARDS_IN_DECK);

    public CardDeckService() {
        fillDeck();
    }

    public void passCard(Card card) {
        deck.add(card);
    }

    public Card drawCard() {
        return deck.remove();
    }

    public int getCardsInDeck() {
        return deck.size();
    }

    private void fillDeck() {
        //noinspection MismatchedQueryAndUpdateOfCollection
        final List<Card> cards = new ArrayList<>();
        for (Card.Color color : Card.Color.values()) {
            for (Card.Shape shape : Card.Shape.values()) {
                for (int value : new int[]{1, 2, 3, 4}) {
                    final Card card = new Card(color, shape, value);
                    deck.add(card);
                }
            }
        }
        // todo: add wildcards
        Collections.shuffle(cards);
        deck.addAll(cards);
    }

    public static final int MAX_CARDS_IN_DECK = 64;

}
