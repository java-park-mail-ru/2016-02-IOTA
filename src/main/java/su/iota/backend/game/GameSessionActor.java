package su.iota.backend.game;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.actors.behaviors.RequestReplyHelper;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.messages.game.GameResultMessage;
import su.iota.backend.messages.game.PlayerActionMessage;
import su.iota.backend.messages.game.PlayerActionResultMessage;
import su.iota.backend.messages.internal.GameSessionInitMessage;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@PerLookup
public final class GameSessionActor extends BasicActor<IncomingMessage, GameResultMessage> {

    @Inject
    GameMechanics gameMechanics;

    private Map<ActorRef<Object>, UserProfile> players;

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
            } else if (message instanceof PlayerActionMessage) {
                handlePlayerActionMessage((PlayerActionMessage) message);
            }
            checkCodeSwap();
        }
    }

    private void handlePlayerActionMessage(PlayerActionMessage message) throws SuspendExecution {
        final PlayerActionMessage actionMessage = (PlayerActionMessage) message;
        //noinspection unchecked
        final ActorRef<Object> sender = (ActorRef<Object>) actionMessage.getFrom();
        Log.info("Player " + actionMessage.getFrom() + " is ready: " + actionMessage.getReady());
        final PlayerActionResultMessage actionResultMessage = new PlayerActionResultMessage();
        actionResultMessage.setOk(true);
        actionResultMessage.setPayload(self().toString());
        sender.send(actionResultMessage);
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
