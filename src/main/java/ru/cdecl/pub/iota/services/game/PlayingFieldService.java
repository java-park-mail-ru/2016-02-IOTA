package ru.cdecl.pub.iota.services.game;

import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.game.CardPlacementException;
import ru.cdecl.pub.iota.models.game.Card;
import ru.cdecl.pub.iota.models.game.CardDeckItem;
import ru.cdecl.pub.iota.models.game.Wildcard;

import java.util.HashSet;
import java.util.Set;

@Service
@PerLookup
public class PlayingFieldService {

    private CardDeckItem[][] cards = new Card[MAX_CARDS_IN_DIMENSION][MAX_CARDS_IN_DIMENSION];

    public void setInitialCard(CardDeckItem initialCard) {
        cards[CENTER_X][CENTER_Y] = initialCard;
        if (initialCard instanceof Wildcard) {
            updateWildcardSubstitutes((Wildcard) initialCard);
        }
    }

    public CardDeckItem[][] getCards() {
        return cards;
    }

    @Deprecated
    public boolean setCards(CardDeckItem[][] newCards) {
        this.cards = newCards;
        for (CardDeckItem[] row : newCards) {
            for (CardDeckItem card : row) {
                if (card instanceof Wildcard) {
                    updateWildcardSubstitutes((Wildcard) card);
                }
            }
        }
        return true;
    }

    @Nullable
    public CardDeckItem tryPlaceCard(CardDeckItem card, int x, int y) throws CardPlacementException {
        final CardDeckItem previousCard = tryPlaceCardInternal(card, x, y);
        cards[x][y] = card;
        return previousCard;
    }

    @Nullable
    private CardDeckItem tryPlaceCardInternal(CardDeckItem card, int x, int y) throws CardPlacementException {
        if (!isInBounds(x, y)) {
            throw new CardPlacementException();
        }
        final CardDeckItem previousCard = cards[x][y];
        if (previousCard != null) {
            return tryDoWildcardSwapInternal(card, previousCard);
        }
        //noinspection ConstantConditions
        if (!isPlacementValid(card, x, y)) {
            throw new CardPlacementException();
        }
        return null;
    }

    private CardDeckItem tryDoWildcardSwapInternal(CardDeckItem card, CardDeckItem previousCard) throws CardPlacementException {
        if (!(previousCard instanceof Wildcard) || card instanceof Wildcard) {
            throw new CardPlacementException();
        }
        final Wildcard previousWildcard = (Wildcard) previousCard;
        if (!(card instanceof Card)) {
            throw new AssertionError();
        }
        final Set<Card> substituteCards = previousWildcard.getSubstituteCards();
        if (substituteCards == null) {
            throw new AssertionError();
        }
        if (!substituteCards.contains(card)) {
            throw new CardPlacementException();
        }
        previousWildcard.setSubstituteCards(null);
        return previousWildcard;
    }

    private boolean isInBounds(int x, int y) {
        boolean result = (x >= 0 && x < MAX_CARDS_IN_DIMENSION);
        result = result && (y >= 0 && y < MAX_CARDS_IN_DIMENSION);
        return result;
    }

    private void updateWildcardSubstitutes(Wildcard wildcard) {
        final Set<Card> wildcardSubstituteCardSet = new HashSet<>();
        wildcardSubstituteCardSet.addAll(CardDeckService.REFERENCE_CARD_SET);
        wildcard.setSubstituteCards(wildcardSubstituteCardSet);
    }

    private boolean isPlacementValid(CardDeckItem card, int x, int y) {
        return true;
    }

    public static final int MAX_CARDS_IN_DIMENSION = 34;

    private static final int CENTER_X = MAX_CARDS_IN_DIMENSION / 2;
    private static final int CENTER_Y = MAX_CARDS_IN_DIMENSION / 2;

}
