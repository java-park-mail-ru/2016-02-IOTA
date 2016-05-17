package su.iota.backend.game;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.actors.behaviors.RequestReplyHelper;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.game.GameMechanics;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.messages.game.GameResultMessage;
import su.iota.backend.messages.game.GameSessionInitMessage;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@PerLookup
public final class GameSessionActor extends BasicActor<IncomingMessage, GameResultMessage> {

    @Inject
    GameMechanics gameMechanics;

    private Map<UserProfile, ActorRef<OutgoingMessage>> players;

    @Override
    protected GameResultMessage doRun() throws InterruptedException, SuspendExecution {
        //noinspection InfiniteLoopStatement
        while (true) {
            final IncomingMessage message = receive(3, TimeUnit.SECONDS);
            if (message instanceof GameSessionInitMessage) {
                final GameSessionInitMessage initMessage = ((GameSessionInitMessage) message);
                if (this.players != null) {
                    RequestReplyHelper.reply(initMessage, false);
                } else {
                    this.players = initMessage.getPlayers();
                    RequestReplyHelper.reply(initMessage, true);
                }
            } else {
                if (message != null) {
                    Log.info(message.toString()); // todo
                }
            }
            checkCodeSwap();
        }
    }

    @Override
    protected @Nullable IncomingMessage filterMessage(Object m) {
        if (players == null && !(m instanceof GameSessionInitMessage)) {
            return null;
        }
        return super.filterMessage(m);
    }

    //

}
