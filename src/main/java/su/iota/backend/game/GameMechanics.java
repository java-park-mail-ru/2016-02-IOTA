package su.iota.backend.game;

import co.paralleluniverse.fibers.SuspendExecution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import su.iota.backend.models.game.Coordinate;
import su.iota.backend.models.game.FieldItem;

import java.util.UUID;

@Contract
public interface GameMechanics {

    @NotNull
    UUID getCurrentGameStateUuid() throws SuspendExecution;

    boolean addPlayer(int player) throws SuspendExecution;

    void dropPlayer(int player) throws SuspendExecution;

    @Nullable
    Integer getPlayerScores(int player) throws SuspendExecution;

    boolean isConcluded() throws SuspendExecution;

    void setConcluded(boolean concluded) throws SuspendExecution;

    boolean tryPlayCard(int player, @NotNull Coordinate coordinate, @NotNull UUID uuid) throws SuspendExecution;

    boolean tryEphemeralPlayCard(int player, @NotNull Coordinate coordinate, @NotNull UUID uuid) throws SuspendExecution;

    boolean endTurn(int player) throws SuspendExecution;

    @Nullable
    FieldItem getDrawnCardByUuid(@NotNull UUID uuid) throws SuspendExecution;

}
