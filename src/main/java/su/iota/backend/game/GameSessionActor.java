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
import su.iota.backend.messages.game.AbstractPlayerActionMessage;
import su.iota.backend.messages.game.impl.GameStateMessage;
import su.iota.backend.messages.game.impl.IllegalPlayerActionResultMessage;
import su.iota.backend.messages.game.impl.PlayerPassCardMessage;
import su.iota.backend.messages.game.impl.PlayerPlaceCardMessage;
import su.iota.backend.messages.internal.GameSessionDropPlayerMessage;
import su.iota.backend.messages.internal.GameSessionInitMessage;
import su.iota.backend.models.UserProfile;
import su.iota.backend.models.game.Coordinate;
import su.iota.backend.models.game.FieldItem;

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
            if (players != null) {
                return new GameSessionInitMessage.Result(false);
            } else {
                players = initMessage.getPlayers();
                for (final ActorRef<Object> frontend : players.keySet()) {
                    watch(frontend);
                    gameMechanics.addPlayer(getGameKeyForPlayer(frontend));
                }
                return new GameSessionInitMessage.Result(true);
            }
        } else if (message instanceof AbstractPlayerActionMessage) {
            final AbstractPlayerActionMessage.AbstractResultMessage resultMessage
                    = handlePlayerActionMessage((AbstractPlayerActionMessage) message);
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
                players.remove(player);
                gameMechanics.dropPlayer(getGameKeyForPlayer(player));
                Log.info("Dropping player from game! " + player.toString());
            }
            if (players.isEmpty()) {
                Log.info("Last player disconnected, shutting down! " + self().toString());
                shutdown();
            }
        } else if (message instanceof ActorRef<?>) {
            //noinspection unchecked
            final ActorRef<Object> frontend = (ActorRef<Object>) message;
            frontend.send(getGameStateMessageForFrontend(frontend));
        } else {
            super.handleCast(from, id, message);
        }
    }

    @NotNull
    private AbstractPlayerActionMessage.AbstractResultMessage handlePlayerActionMessage(AbstractPlayerActionMessage message) throws SuspendExecution {
        //noinspection unchecked
        final ActorRef<Object> frontend = (ActorRef<Object>) message.getFrom();
        if (frontend == null || !players.containsKey(frontend)) {
            throw new AssertionError();
        }
        if (message instanceof PlayerPlaceCardMessage) {
            return handlePlaceCardMessage((PlayerPlaceCardMessage) message, frontend);
        } /* else if (message instanceof PlayerPassCardMessage) {
            return handlePassCardMessage((PlayerPassCardMessage) message, frontend);
        } */
        return new IllegalPlayerActionResultMessage();
    }

//    private PlayerPassCardMessage.ResultMessage handlePassCardMessage(@NotNull PlayerPassCardMessage message, @NotNull ActorRef<Object> frontend) throws SuspendExecution {
//        return null;
//    }

    @NotNull
    private PlayerPlaceCardMessage.ResultMessage handlePlaceCardMessage(@NotNull PlayerPlaceCardMessage message, @NotNull ActorRef<Object> frontend) throws SuspendExecution {
        final UUID uuid = message.getUuid();
        final Coordinate coordinate = message.getCoordinate();
        if (uuid == null || coordinate == null) {
            return new PlayerPlaceCardMessage.ResultMessage(false);
        }
        final int playerKey = getGameKeyForPlayer(frontend);
        final boolean isOk = message.isEphemeral()
                ? gameMechanics.tryEphemeralPlayCard(playerKey, coordinate, uuid)
                : gameMechanics.tryPlayCard(playerKey, coordinate, uuid);
        if (message.isEndSequenceTrigger()) {
            gameMechanics.endTurn(playerKey);
        }
        return new PlayerPlaceCardMessage.ResultMessage(true);
    }

    private void broadcastGameState() throws SuspendExecution {
        for (ActorRef<Object> playerFrontend : players.keySet()) {
            playerFrontend.send(getGameStateMessageForFrontend(playerFrontend));
        }
    }

    private int getGameKeyForPlayer(@NotNull ActorRef<Object> frontend) throws SuspendExecution {
        return System.identityHashCode(frontend);
    }

    private GameStateMessage getGameStateMessageForFrontend(ActorRef<Object> frontend) throws SuspendExecution {
        return new GameStateMessage(gameMechanics.getCurrentGameStateUuid()); // todo
    }

}
