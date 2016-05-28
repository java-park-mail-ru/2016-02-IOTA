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
    private Set<UUID> cardsInDeck = new HashSet<>();
    private Set<UUID> cardsInPlay = new HashSet<>();
    private Set<UUID> cardsPlayed = new HashSet<>();
    private Map<Integer, Set<UUID>> cardsInHand = new HashMap<>();

    {
        initCardDeck();
        final FieldItem card = drawCard();
        if (!tryPlaceCardInternal(Field.CENTER_COORDINATE, card)) {
            throw new AssertionError();
        }
    }

    @NotNull
    @Override
    public UUID getCurrentGameStateUuid() {
        return currentGameStateUuid;
    }

    @NotNull
    public FieldItem drawCard() {
        final FieldItem drawnCard = cardDeck.remove();
        final UUID cardUuid = drawnCard.getUuid();
        cardsInDeck.remove(cardUuid);
        cardsInPlay.add(cardUuid);
        return drawnCard;
    }

    public boolean tryPlaceCard(int player, boolean isEphemeral, @NotNull Coordinate coordinate, @NotNull FieldItem card) {
        if (card.isEphemeral()) {
            return false;
        }
        final UUID uuid = card.getUuid();
        if (cardsInDeck.contains(uuid) || !cardsInPlay.contains(uuid) || cardsPlayed.contains(uuid)) {
            return false;
        }
        final Set<UUID> playerHand = cardsInHand.get(player);
        if (playerHand == null || !playerHand.contains(uuid)) {
            return false;
        }
        if (!tryPlaceCardInternal(isEphemeral, coordinate, card)) {
            return false;
        }
        if (!isEphemeral) {
            playerHand.remove(uuid);
            cardsInPlay.remove(uuid);
            cardsPlayed.add(uuid);
        }
        return true;
    }

    private boolean tryPlaceCardInternal(@NotNull Coordinate coordinate, @NotNull FieldItem card) {
        return tryPlaceCardInternal(false, coordinate, card);
    }

    private boolean tryPlaceCardInternal(boolean isEphemeral, @NotNull Coordinate coordinate, @NotNull FieldItem card) {
        final boolean isOk = field.isPlacementCorrect(coordinate, card);
        if (isOk && !isEphemeral) {
            field.placeCard(coordinate, card);
        }
        return isOk;
    }

    public void endTurn() {
        if (players.isEmpty()) {
            throw new AssertionError();
        }
        final Integer headPlayer = players.removeFirst();
        players.addLast(headPlayer);
    }

    private void initCardDeck() {
        final List<FieldItem> cards = new ArrayList<>();
        for (FieldItem.Color color : FieldItem.Color.values()) {
            for (FieldItem.Shape shape : FieldItem.Shape.values()) {
                for (FieldItem.Number number : FieldItem.Number.values()) {
                    final Card card = new Card(color, shape, number);
                    cardsInDeck.add(card.getUuid());
                    cards.add(card);
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            final Wildcard wildcard = new Wildcard();
            cardsInDeck.add(wildcard.getUuid());
            cards.add(wildcard);
        }
        Collections.shuffle(cards);
        cardDeck.addAll(cards);
    }

}
