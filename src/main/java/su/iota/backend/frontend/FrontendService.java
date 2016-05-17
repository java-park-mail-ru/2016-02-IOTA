package su.iota.backend.frontend;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.SuspendExecution;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.models.UserProfile;

@Contract
public interface FrontendService {

    boolean signUp(@Nullable UserProfile userProfile) throws SuspendExecution;

    boolean checkSignIn(@Nullable UserProfile userProfile) throws SuspendExecution;

    boolean editUser(@Nullable UserProfile userProfile) throws SuspendExecution;

    boolean getUserDetails(@Nullable UserProfile userProfile) throws SuspendExecution;

    ActorRef<IncomingMessage> getGameSession(UserProfile userProfile, ActorRef<OutgoingMessage> frontend) throws SuspendExecution, InterruptedException;

}
