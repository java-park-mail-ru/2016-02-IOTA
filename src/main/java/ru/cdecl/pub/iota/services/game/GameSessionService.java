package ru.cdecl.pub.iota.services.game;

import org.glassfish.hk2.api.ServiceLocator;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.mechanics.GamePlayer;
import ru.cdecl.pub.iota.mechanics.GameSession;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Singleton
public class GameSessionService {

    @Inject
    private ServiceLocator serviceLocator;

    private final ConcurrentMap<Long, GameSession> gameSessions = new ConcurrentHashMap<>();

    public GameSessionService() {
        final TimerTask cleanupTask = new TimerTask() {
            @Override
            public void run() {
                cleanupDeadSessions();
            }
        };
        final Timer cleanupTimer = new Timer(true);
        cleanupTimer.schedule(cleanupTask, 0, CLEANUP_PERIOD);
    }

    public Long spawnGameSessionForPlayers(Iterable<UserProfile> players) {
        final long gameSessionId = ID_GENERATOR.getAndIncrement();
        final GameSession gameSession = serviceLocator.getService(GameSession.class);
        gameSession.setPlayers(players);
        gameSessions.put(gameSessionId, gameSession);
        return gameSessionId;
    }

    @Nullable
    public GameSession getGameSessionById(Long gameSessionId) {
        if (gameSessionId == null) {
            return null;
        }
        return gameSessions.get(gameSessionId);
    }

    public synchronized void cleanupDeadSessions() {
        final List<Long> deadGameSessionIds = new ArrayList<>();
        gameSessions.forEach((id, gameSession) ->
                gameSession.getGamePlayers().forEach(player -> {
                    if (!player.isConnected()) {
                        deadGameSessionIds.add(id);
                    }
                }));
        deadGameSessionIds.forEach(gameSessions::remove);
    }

    public static final int CLEANUP_PERIOD = 3000;

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1L);

}
