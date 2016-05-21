package su.iota.backend.game;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.SuspendExecution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.models.UserProfile;

@Contract
public interface MatchmakingService {

    @Nullable ActorRef<IncomingMessage> getGameSession(@NotNull UserProfile player, @NotNull ActorRef<Object> frontend) throws SuspendExecution, InterruptedException;

}
