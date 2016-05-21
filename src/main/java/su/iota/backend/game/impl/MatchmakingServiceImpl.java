package su.iota.backend.game.impl;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestReplyHelper;
import co.paralleluniverse.fibers.SuspendExecution;
import org.glassfish.hk2.api.ServiceLocator;
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
public class MatchmakingServiceImpl implements MatchmakingService {

    @Inject
    ServiceLocator serviceLocator;

    @Override
    public ActorRef<IncomingMessage> getGameSession(UserProfile player, ActorRef<OutgoingMessage> frontend) throws SuspendExecution, InterruptedException {
        final Map<UserProfile, ActorRef<OutgoingMessage>> players = new HashMap<>();
        players.put(player, frontend);
        return createGameSession(players); // todo: only single player is supported now
    }

    private ActorRef<IncomingMessage> createGameSession(Map<UserProfile, ActorRef<OutgoingMessage>> players) throws SuspendExecution, InterruptedException {
        final ActorRef<IncomingMessage> gameSessionActor = serviceLocator.getService(GameSessionActor.class).spawn();
        final Boolean ok = RequestReplyHelper.call(gameSessionActor, new GameSessionInitMessage(players));
        if (!ok) {
            throw new AssertionError();
        }
        return gameSessionActor;
    }

}
