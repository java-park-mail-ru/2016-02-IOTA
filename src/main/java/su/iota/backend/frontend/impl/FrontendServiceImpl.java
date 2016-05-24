package su.iota.backend.frontend.impl;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.Server;
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
import su.iota.backend.messages.OutgoingMessage;
import su.iota.backend.messages.game.PlayerActionMessage;
import su.iota.backend.messages.internal.GameSessionDropPlayerMessage;
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
    private Server<IncomingMessage, OutgoingMessage, Object> gameSession;

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
            if (!signIn(userProfile)) {
                throw new AssertionError();
            }
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
        resetGameSession();
        signedInUser = null;
    }

    @Nullable
    @Override
    public UserProfile getSignedInUser() throws SuspendExecution {
        return signedInUser;
    }

    @Override
    public boolean editProfile(@Nullable UserProfile userProfile) throws SuspendExecution {
        return false; // todo
    }

    @Override
    public boolean deleteUser(long userId) throws SuspendExecution {
        return false; // todo
    }

    @Nullable
    @Override
    public UserProfile getUserById(long userId) throws SuspendExecution {
        if (!accountService.isUserExistent(userId)) {
            return null;
        }
        return accountService.getUserProfile(userId);
    }

    @NotNull
    @Override
    public PlayerActionMessage.ResultMessage performPlayerAction(@NotNull PlayerActionMessage playerActionMessage) throws SuspendExecution, InterruptedException {
        if (signedInUser == null || gameSession == null) {
            return new PlayerActionMessage.ResultMessage(false);
        }
        final OutgoingMessage result = gameSession.call(playerActionMessage);
        if (!(result instanceof PlayerActionMessage.ResultMessage)) {
            throw new AssertionError();
        }
        return (PlayerActionMessage.ResultMessage) result;
    }

    @Override
    public boolean askGameStateUpdate(@NotNull ActorRef<Object> frontend) throws SuspendExecution, InterruptedException {
        if (signedInUser == null) {
            return false;
        }
        if (gameSession != null) {
            gameSession.cast(frontend);
        } else {
            matchmakingService.makeMatch(signedInUser, frontend);
        }
        return true;
    }

    @Override
    public void setGameSession(@NotNull ActorRef<Object> frontend, Server<IncomingMessage, OutgoingMessage, Object> gameSession) throws SuspendExecution, InterruptedException {
        this.gameSession = gameSession;
        if (!askGameStateUpdate(frontend)) {
            throw new AssertionError();
        }
    }

    @Override
    public void resetGameSession() throws SuspendExecution {
        gameSession = null;
    }

    @Override
    public void dropPlayerFromGameSession(@NotNull ActorRef<Object> frontend) throws SuspendExecution, InterruptedException {
        if (gameSession != null) {
            gameSession.cast(new GameSessionDropPlayerMessage(frontend));
        }
        resetGameSession();
    }
}
