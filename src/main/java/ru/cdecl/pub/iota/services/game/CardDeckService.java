package ru.cdecl.pub.iota.services.game;

import org.eclipse.jetty.util.ArrayQueue;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.game.NoCardsInDeckException;
import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.CardDeckItem;
import ru.cdecl.pub.iota.models.game.Wildcard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@PerLookup
public class CardDeckService {

    private Queue<CardDeckItem> deck = new ArrayQueue<>(MAX_ITEMS_IN_DECK);

    public CardDeckService() {
        fillDeck();
    }

    public void passCard(CardDeckItem card) {
        deck.add(card);
    }

    public CardDeckItem drawCard() throws NoCardsInDeckException {
        try {
            return deck.remove();
        } catch (NoSuchElementException e) {
            //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
            throw new NoCardsInDeckException();
        }
    }

    public int getCardsInDeck() {
        return deck.size();
    }

    private void fillDeck() {
        final List<CardDeckItem> cards = new ArrayList<>();
        cards.addAll(REFERENCE_CARD_SET);
        //noinspection ConstantConditions
        if (isWildcardsEnabled()) {
            for (int i = 0; i < WILDCARDS_IN_DECK; i++) {
                final Wildcard wildcard = new Wildcard();
                cards.add(wildcard);
            }
        }
        Collections.shuffle(cards);
        deck.addAll(cards);
    }

    private boolean isWildcardsEnabled() {
        return false;
    }

    public static final Set<Card> REFERENCE_CARD_SET = Collections.unmodifiableSet(
            Arrays.stream(Card.Color.values()).flatMap(color ->
                    Arrays.stream(Card.Shape.values()).flatMap(shape ->
                            IntStream.rangeClosed(1, 4).mapToObj(value ->
                                    new Card(color, shape, value)
                            ))).collect(Collectors.toSet()));

    public static final int WILDCARDS_IN_DECK = 2;
    public static final int MAX_ITEMS_IN_DECK = REFERENCE_CARD_SET.size() + WILDCARDS_IN_DECK;

}
