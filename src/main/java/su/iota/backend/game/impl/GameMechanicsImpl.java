package su.iota.backend.game.impl;

import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.game.GameMechanics;

@Service
@PerLookup
public class GameMechanicsImpl extends ProxyServerActor implements GameMechanics {


    public GameMechanicsImpl() {
        super(true);
    }

    //

}
