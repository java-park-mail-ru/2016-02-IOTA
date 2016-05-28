package su.iota.backend.game.impl;

import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.game.GameMechanics;
import su.iota.backend.models.game.Coordinate;
import su.iota.backend.models.game.Field;
import su.iota.backend.models.game.FieldCell;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@PerLookup
public class GameMechanicsImpl extends ProxyServerActor implements GameMechanics {

    @NotNull
    private UUID currentGameStateUuid = UUID.randomUUID();

    public GameMechanicsImpl() {
        super(true);
    }

    private Field field = new Field();

    @NotNull
    @Override
    public UUID getCurrentGameStateUuid() {
        return currentGameStateUuid;
    }

    //

}
