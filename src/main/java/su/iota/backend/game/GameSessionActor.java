package su.iota.backend.game;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.ServerActor;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.messages.game.PlayerActionMessage;
import su.iota.backend.messages.game.GameStateMessage;
import su.iota.backend.messages.internal.GameSessionDropPlayerMessage;
import su.iota.backend.messages.internal.GameSessionInitMessage;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import java.util.*;

@Service
@PerLookup
public final class GameSessionActor extends ServerActor<IncomingMessage, OutgoingMessage, Object> {

    @Inject
    GameMechanics gameMechanics;

    private Map<ActorRef<Object>, UserProfile> players;

    @Nullable
    @Override
    protected OutgoingMessage handleCall(ActorRef<?> from, Object id, IncomingMessage message) throws Exception, SuspendExecution {
        if (message instanceof GameSessionInitMessage) {
            final GameSessionInitMessage initMessage = ((GameSessionInitMessage) message);
            if (this.players != null) {
                return new GameSessionInitMessage.Result(false);
            } else {
                this.players = initMessage.getPlayers();
                this.players.keySet().forEach(this::watch);
                return new GameSessionInitMessage.Result(true);
            }
        } else if (message instanceof PlayerActionMessage) {
            final PlayerActionMessage.ResultMessage resultMessage = handlePlayerActionMessage((PlayerActionMessage) message);
            if (resultMessage.isBroadcastTrigger()) {
                broadcastGameState();
            }
            return resultMessage;
        } else {
            return super.handleCall(from, id, message);
        }
    }

    @Override
    protected void handleCast(ActorRef<?> from, Object id, Object message) throws SuspendExecution {
        if (message instanceof GameSessionDropPlayerMessage) {
            final GameSessionDropPlayerMessage dropMessage = (GameSessionDropPlayerMessage) message;
            final ActorRef<Object> player = dropMessage.getPlayer();
            if (players.containsKey(player)) {
                players.remove(player); // todo: tell game mechanics!
            }
            if (players.isEmpty()) {
                Log.info("Last player disconnected, shutting down! " + self().toString());
                shutdown();
            }
        } else if (message instanceof ActorRef<?>) {
            //noinspection unchecked
            final ActorRef<Object> frontend = (ActorRef<Object>) message;
            frontend.send(getGameStateMessageForFrontend(frontend));
        }
    }

    @NotNull
    private PlayerActionMessage.ResultMessage handlePlayerActionMessage(PlayerActionMessage message) throws SuspendExecution {
        //noinspection unchecked
        final ActorRef<Object> frontend = (ActorRef<Object>) message.getFrom();
        if (frontend == null) {
            throw new AssertionError();
        }
        final PlayerActionMessage.ResultMessage resultMessage = new PlayerActionMessage.ResultMessage();
        final UserProfile userProfile = players.get(frontend);
        if (userProfile == null) {
            resultMessage.setOk(false);
            Log.info("No such player in this session! Session: " + self().toString() + ", Frontend: " + frontend.toString());
        } else {
            resultMessage.setOk(true);
            Log.info("Player " + userProfile.getLogin() + " is ready: " + message.getReady());
        }
        return resultMessage;
    }

    private void broadcastGameState() throws SuspendExecution {
        for (ActorRef<Object> playerFrontend : players.keySet()) {
            playerFrontend.send(getGameStateMessageForFrontend(playerFrontend));
        }
    }

    private GameStateMessage getGameStateMessageForFrontend(ActorRef<Object> frontend) {
        final GameStateMessage gameStateMessage = new GameStateMessage();
        if (players.containsKey(frontend)) {
            gameStateMessage.setOk(true);
            gameStateMessage.setPayload(self().toString());
        } else {
            gameStateMessage.setOk(false);
        }
        return gameStateMessage;
    }

}
