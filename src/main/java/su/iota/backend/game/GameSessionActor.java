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
import su.iota.backend.messages.game.GameResultMessage;
import su.iota.backend.messages.game.PlayerActionMessage;
import su.iota.backend.messages.game.PlayerActionResultMessage;
import su.iota.backend.messages.internal.GameSessionDropPlayerMessage;
import su.iota.backend.messages.internal.GameSessionInitMessage;
import su.iota.backend.messages.internal.GameSessionTerminateMessage;
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
        while (true) {
            final IncomingMessage message = receive(3, TimeUnit.SECONDS);
            if (message instanceof GameSessionInitMessage) {
                final GameSessionInitMessage initMessage = ((GameSessionInitMessage) message);
                if (this.players != null) {
                    RequestReplyHelper.reply(initMessage, false);
                } else {
                    this.players = initMessage.getPlayers();
                    this.players.keySet().forEach(this::watch);
                    RequestReplyHelper.reply(initMessage, true);
                }
            } else if (message instanceof GameSessionTerminateMessage) {
                return new GameResultMessage(); // todo
            } else if (message instanceof GameSessionDropPlayerMessage) {
                // todo
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
        for (ActorRef<Object> playerFrontend : players.keySet()) {
            final PlayerActionResultMessage resultMessage = new PlayerActionResultMessage();
            resultMessage.setOk(true);
            resultMessage.setPayload(userProfile.getId() + " :: " + userProfile.getLogin() + " :: ready: " + message.getReady());
            playerFrontend.send(resultMessage);
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
