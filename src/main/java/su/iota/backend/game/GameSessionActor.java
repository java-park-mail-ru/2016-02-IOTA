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

import static su.iota.backend.misc.SuspendableUtils.rethrowConsumer;

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
        //noinspection unchecked
        final ActorRef<Object> frontend = (ActorRef<Object>) message.getFrom();
        if (frontend == null) {
            throw new AssertionError();
        }
        final UserProfile userProfile = players.get(frontend);
        if (userProfile == null) {
            throw new AssertionError();
        }
        Log.info("Player " + userProfile.getLogin() + " is ready: " + message.getReady());
        players.entrySet().stream().forEach(rethrowConsumer(e -> {
            final PlayerActionResultMessage resultMessage = new PlayerActionResultMessage();
            resultMessage.setOk(true);
            resultMessage.setPayload(userProfile.getId() + " :: " + userProfile.getLogin() + " :: ready: " + message.getReady());
            e.getKey().send(resultMessage);
        }));
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
