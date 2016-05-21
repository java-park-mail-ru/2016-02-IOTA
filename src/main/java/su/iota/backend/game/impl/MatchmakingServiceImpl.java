package su.iota.backend.game.impl;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import co.paralleluniverse.actors.behaviors.RequestReplyHelper;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import org.glassfish.hk2.api.ServiceLocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.game.GameSessionActor;
import su.iota.backend.game.MatchmakingService;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.messages.internal.GameSessionInitMessage;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Service
@Singleton
public class MatchmakingServiceImpl extends ProxyServerActor implements MatchmakingService {

    @Inject
    ServiceLocator serviceLocator;

    public MatchmakingServiceImpl() {
        super(false);
    }

    @Override
    public @Nullable ActorRef<IncomingMessage> getGameSession(@NotNull UserProfile player, @NotNull ActorRef<Object> frontend) throws SuspendExecution, InterruptedException {
        final Map<ActorRef<Object>, UserProfile> players = new HashMap<>();
        players.put(frontend, player);
        return createGameSession(players); // todo: only single player is supported now
    }

    private @Nullable ActorRef<IncomingMessage> createGameSession(@NotNull Map<ActorRef<Object>, UserProfile> players) throws SuspendExecution, InterruptedException {
        Log.info("Making match for players!");
        final ActorRef<IncomingMessage> gameSessionActor = serviceLocator.getService(GameSessionActor.class).spawn();
        final Boolean ok = RequestReplyHelper.call(gameSessionActor, new GameSessionInitMessage(players));
        if (!ok) {
            throw new AssertionError();
        }
        return gameSessionActor;
    }

}
