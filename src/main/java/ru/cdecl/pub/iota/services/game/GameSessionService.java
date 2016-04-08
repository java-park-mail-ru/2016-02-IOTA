package ru.cdecl.pub.iota.services.game;

import org.glassfish.hk2.api.ServiceLocator;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.mechanics.GameSession;
import ru.cdecl.pub.iota.models.game.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Singleton
public class GameSessionService {

    private final ServiceLocator serviceLocator;
    private ConcurrentMap<Long, GameSession> gameSessions = new ConcurrentHashMap<>();

    @Inject
    public GameSessionService(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    public Long spawnGameSessionForPlayers(Iterable<Player> players) {
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

    public void cleanUpDeadSessions() {
        // todo
    }

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1L);

}
