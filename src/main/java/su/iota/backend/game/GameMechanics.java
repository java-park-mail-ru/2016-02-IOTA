package su.iota.backend.game;

import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

import java.util.UUID;

@Contract
public interface GameMechanics {

    @NotNull
    UUID getCurrentGameStateUuid();

}
