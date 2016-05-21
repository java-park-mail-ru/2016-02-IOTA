package su.iota.backend.frontend.impl;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import org.apache.commons.beanutils.BeanUtils;
import org.glassfish.hk2.api.PerLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.accounts.AccountService;
import su.iota.backend.accounts.exceptions.UserAlreadyExistsException;
import su.iota.backend.accounts.exceptions.UserNotFoundException;
import su.iota.backend.frontend.FrontendService;
import su.iota.backend.game.MatchmakingService;
import su.iota.backend.messages.IncomingMessage;
import su.iota.backend.messages.game.PlayerActionMessage;
import su.iota.backend.messages.game.PlayerActionResultMessage;
import su.iota.backend.messages.internal.GameSessionTerminateMessage;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;

@Service
@PerLookup
public class FrontendServiceImpl implements FrontendService {

    @Inject
    MatchmakingService matchmakingService;

    @Inject
    AccountService accountService;

    private UserProfile signedInUser;
    private ActorRef<IncomingMessage> gameSessionActor;

    @Override
    public boolean signUp(@Nullable UserProfile userProfile) throws SuspendExecution {
        if (userProfile == null) {
            return false;
        }
        final String login = userProfile.getLogin();
        final String password = userProfile.getPassword();
        if (login == null || password == null) {
            return false;
        }
        try {
            final long userId = accountService.createUser(userProfile);
            userProfile.setId(userId);
            return true;
        } catch (UserAlreadyExistsException ignored) {
        }
        return false;
    }

    @Override
    public boolean signIn(@Nullable UserProfile userProfile) throws SuspendExecution {
        if (userProfile == null) {
            return false;
        }
        final String login = userProfile.getLogin();
        final String password = userProfile.getPassword();
        if (login == null || password == null) {
            return false;
        }
        final Long userId = accountService.getUserId(login);
        if (userId == null) {
            return false;
        }
        try {
            if (accountService.isUserPasswordCorrect(userId, password)) {
                signedInUser = accountService.getUserProfile(userId);
                return true;
            }
        } catch (UserNotFoundException ignored) {
        }
        return false;
    }

    @Override
    public void signOut() throws SuspendExecution {
        signedInUser = null;
    }

    @Override
    public @Nullable UserProfile getSignedInUser() throws SuspendExecution {
        return signedInUser;
    }

    @Override
    public boolean editProfile(@Nullable UserProfile userProfile) throws SuspendExecution {
        return false; // todo
    }

    @Override
    public boolean deleteUser(@Nullable UserProfile userProfile) throws SuspendExecution {
        return false; // todo
    }

    @Override
    public boolean getUserDetails(@Nullable UserProfile userProfile) throws SuspendExecution {
        if (userProfile == null) {
            return false;
        }
        final Long userId = userProfile.getId();
        if (userId == null) {
            return false;
        }
        final UserProfile storedUserProfile = accountService.getUserProfile(userId);
        if (storedUserProfile == null) {
            return false;
        }
        try {
            BeanUtils.copyProperties(userProfile, storedUserProfile);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Log.error(null, ex);
            return false;
        }
        return true;
    }

    @Override
    public void performPlayerAction(@NotNull PlayerActionMessage playerActionMessage) throws SuspendExecution {
        //noinspection unchecked
        final ActorRef<Object> frontend = (ActorRef<Object>) playerActionMessage.getFrom();
        final PlayerActionResultMessage resultMessage = new PlayerActionResultMessage();
        if (signedInUser != null) {
            if (gameSessionActor == null) {
                matchmakingService.makeMatch(signedInUser, frontend);
                resultMessage.setOk(true);
            } else {
                gameSessionActor.send(playerActionMessage);
                return;
            }
        } else {
            resultMessage.setOk(false);
        }
        frontend.send(resultMessage);
    }

    @Override
    public void setGameSession(@NotNull ActorRef<Object> frontend, @NotNull ActorRef<IncomingMessage> gameSessionActor) throws SuspendExecution {
        this.gameSessionActor = gameSessionActor;
        final PlayerActionResultMessage resultMessage = new PlayerActionResultMessage();
        resultMessage.setOk(true);
        resultMessage.setPayload(gameSessionActor.toString());
        frontend.send(resultMessage);
    }

    @Override
    public void resetGameSession() throws SuspendExecution {
        this.gameSessionActor.send(new GameSessionTerminateMessage());
        this.gameSessionActor = null;
    }

}
