package su.iota.backend.game.impl;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import co.paralleluniverse.actors.behaviors.RequestReplyHelper;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import org.eclipse.jetty.util.ArrayQueue;
import org.glassfish.hk2.api.ServiceLocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.game.GameSessionActor;
import su.iota.backend.game.MatchmakingService;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.messages.game.PlayerActionResultMessage;
import su.iota.backend.messages.internal.GameSessionInitMessage;
import su.iota.backend.misc.SuspendableUtils;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static su.iota.backend.misc.SuspendableUtils.rethrowConsumer;
import static su.iota.backend.misc.SuspendableUtils.rethrowFunction;

@Service
@Singleton
public class MatchmakingServiceImpl extends ProxyServerActor implements MatchmakingService {

    @Inject
    ServiceLocator serviceLocator;

    private Queue<PlayerBucket> buckets = new ArrayQueue<>();

    public MatchmakingServiceImpl() {
        super(false);
    }

    @Override
    public void makeMatch(@NotNull UserProfile player, @NotNull ActorRef<Object> frontend) throws SuspendExecution {
        if (!buckets.isEmpty()) {
            if (!buckets.stream().anyMatch(bucket -> bucket.tryPut(frontend, player))) {
                Log.info("MM: tryPut() failed for all possible buckets for player " + player.getLogin());
            }
        } else {
            final PlayerBucket newBucket = new PlayerBucket(2);
            if (!newBucket.tryPut(frontend, player)) {
                throw new AssertionError();
            }
            buckets.add(newBucket);
        }
        buckets.stream().filter(PlayerBucket::isFull).forEach(rethrowConsumer(b -> {
            final Map<ActorRef<Object>, UserProfile> players = b.getPlayers();
            final ActorRef<IncomingMessage> gameSession = createGameSession(players);
            b.setGameSession(gameSession);
            players.keySet().stream().forEach(rethrowConsumer(a -> a.send(gameSession)));
        }));
        buckets.removeIf(b -> b.getGameSession() != null);
    }

    private @NotNull ActorRef<IncomingMessage> createGameSession(@NotNull Map<ActorRef<Object>, UserProfile> players) throws SuspendExecution {
        Log.info("Making match for players (" +
                players.entrySet().stream()
                        .map(Map.Entry::getValue)
                        .filter(e -> e != null)
                        .map(UserProfile::getLogin)
                        .collect(Collectors.joining(";"))
                + ")!");
        final ActorRef<IncomingMessage> gameSessionActor = serviceLocator.getService(GameSessionActor.class).spawn();
        try {
            final Boolean ok = RequestReplyHelper.call(gameSessionActor, new GameSessionInitMessage(players));
            if (!ok) {
                throw new AssertionError();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return gameSessionActor;
    }

    private static class PlayerBucket {

        private int capacity;
        private Map<ActorRef<Object>, UserProfile> players;
        private ActorRef<IncomingMessage> gameSession;

        PlayerBucket(int capacity) {
            this.capacity = capacity;
            players = new HashMap<>(capacity);
        }

        public boolean isFull() {
            return capacity <= players.size();
        }

        public boolean tryPut(ActorRef<Object> frontend, UserProfile player) {
            return !isFull() && players.putIfAbsent(frontend, player) == null;
        }

        public Map<ActorRef<Object>, UserProfile> getPlayers() {
            return players;
        }

        public ActorRef<IncomingMessage> getGameSession() {
            return gameSession;
        }

        public void setGameSession(ActorRef<IncomingMessage> gameSession) {
            this.gameSession = gameSession;
        }

    }

}
