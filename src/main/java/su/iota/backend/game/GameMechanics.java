package su.iota.backend.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import su.iota.backend.models.game.Coordinate;
import su.iota.backend.models.game.FieldItem;

import java.util.UUID;

@Contract
public interface GameMechanics {

    @NotNull
    UUID getCurrentGameStateUuid();

    boolean addPlayer(int player);

    void dropPlayer(int player);

    @Nullable
    Integer getPlayerScores(int player);

    boolean isConcluded();

    void setConcluded(boolean concluded);

    boolean canPlayCard(int player, @NotNull FieldItem card);

    boolean tryPlayCard(int player, @NotNull Coordinate coordinate, @NotNull FieldItem card);

    boolean tryEphemeralPlayCard(int player, @NotNull Coordinate coordinate, @NotNull FieldItem card);

    boolean endTurn(int player);
    
}
