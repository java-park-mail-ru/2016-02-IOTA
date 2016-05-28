package su.iota.backend.game.impl;

import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import org.eclipse.jetty.util.ArrayQueue;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.game.GameMechanics;
import su.iota.backend.models.game.*;

import java.util.*;

@Service
@PerLookup
public class GameMechanicsImpl extends ProxyServerActor implements GameMechanics {

    private Deque<Integer> players = new ArrayDeque<>();

    @NotNull
    private UUID currentGameStateUuid = UUID.randomUUID();

    public GameMechanicsImpl() {
        super(true);
    }

    private Field field = new Field();
    private Queue<FieldItem> cardDeck = new ArrayQueue<>();
    private Map<UUID, FieldItem> cardsDrawn = new HashMap<>();
    private Set<UUID> cardsPlayed = new HashSet<>();
    private Map<Integer, Set<UUID>> playerHands = new HashMap<>();

    {
        initCardDeck();
        final FieldItem card = drawCard();
        field.placeCard(Field.CENTER_COORDINATE, card);
    }

    @NotNull
    @Override
    public UUID getCurrentGameStateUuid() {
        return currentGameStateUuid;
    }

    public boolean canPlayCard(int player, @NotNull FieldItem card) {
        if (card.isEphemeral()) {
            return false;
        }
        final UUID uuid = card.getUuid();
        final Set<UUID> playerHand = playerHands.get(player);
        //noinspection OverlyComplexBooleanExpression
        return playerHand != null
                && playerHand.contains(uuid)
                && cardsDrawn.containsKey(uuid)
                && !cardsPlayed.contains(uuid);
    }

    public boolean tryPlayCard(int player, @NotNull Coordinate coordinate, @NotNull FieldItem card) {
        return tryPlayCardInternal(player, coordinate, card, false);
    }

    public boolean tryEphemeralPlayCard(int player, @NotNull Coordinate coordinate, @NotNull FieldItem card) {
        return tryPlayCardInternal(player, coordinate, card, true);
    }

    private boolean tryPlayCardInternal(int player, @NotNull Coordinate coordinate, @NotNull FieldItem card, boolean isEphemeral) {
        boolean isOk = true;
        //noinspection ConstantConditions
        isOk = isOk && canPlayCard(player, card);
        isOk = isOk && field.isPlacementCorrect(coordinate, card);
        if (!isEphemeral) {
            playCardInternal(player, coordinate, card);
        }
        return isOk;
    }

    private void playCardInternal(int player, @NotNull Coordinate coordinate, @NotNull FieldItem card) {
        final Set<UUID> playerHand = playerHands.get(player);
        final UUID uuid = card.getUuid();
        playerHand.remove(uuid);
        cardsPlayed.add(uuid);
        field.placeCard(coordinate, card);
    }

    @NotNull
    public FieldItem drawCard() {
        final FieldItem drawnCard = cardDeck.remove();
        final UUID uuid = drawnCard.getUuid();
        if (cardsDrawn.containsKey(uuid)) {
            throw new AssertionError();
        }
        cardsDrawn.put(uuid, drawnCard);
        return drawnCard;
    }

    public void endTurn() {
        if (players.isEmpty()) {
            throw new AssertionError();
        }
        final int headPlayer = players.removeFirst();
        giveCards(headPlayer);
        players.addLast(headPlayer);
    }

    private void giveCards(int headPlayer) {
        final Set<UUID> hand = playerHands.get(headPlayer);
        if (hand == null) {
            throw new AssertionError();
        }
        while (hand.size() < 4) {
            hand.add(drawCard().getUuid());
        }
    }

    private void initCardDeck() {
        final List<FieldItem> cards = new ArrayList<>();
        for (FieldItem.Color color : FieldItem.Color.values()) {
            for (FieldItem.Shape shape : FieldItem.Shape.values()) {
                for (FieldItem.Number number : FieldItem.Number.values()) {
                    final Card card = new Card(color, shape, number);
                    cards.add(card);
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            final Wildcard wildcard = new Wildcard();
            cards.add(wildcard);
        }
        Collections.shuffle(cards);
        cardDeck.addAll(cards);
    }

}
