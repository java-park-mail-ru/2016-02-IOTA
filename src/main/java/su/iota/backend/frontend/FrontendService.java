package su.iota.backend.frontend;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.fibers.SuspendExecution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.messages.game.PlayerActionMessage;
import su.iota.backend.models.UserProfile;

@Contract
public interface FrontendService {

    boolean signUp(@Nullable UserProfile userProfile) throws SuspendExecution;

    boolean signIn(@Nullable UserProfile userProfile) throws SuspendExecution;

    void signOut() throws SuspendExecution;

    @Nullable
    UserProfile getSignedInUser() throws SuspendExecution;

    boolean editProfile(@Nullable UserProfile userProfile) throws SuspendExecution;

    boolean deleteUser(@Nullable UserProfile userProfile) throws SuspendExecution;

    @Nullable
    UserProfile getUserById(long userId) throws SuspendExecution;

    @NotNull
    PlayerActionMessage.ResultMessage performPlayerAction(@NotNull PlayerActionMessage playerActionMessage) throws SuspendExecution, InterruptedException;

    void setGameSession(@NotNull ActorRef<Object> frontend, Server<IncomingMessage, OutgoingMessage, ActorRef<Object>> gameSessionActor) throws SuspendExecution, InterruptedException;

    void resetGameSession() throws SuspendExecution;

    boolean askGameStateUpdate(ActorRef<Object> frontend) throws SuspendExecution, InterruptedException;

}
