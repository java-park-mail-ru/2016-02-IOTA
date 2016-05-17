package su.iota.backend.accounts.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.accounts.AccountService;
import su.iota.backend.accounts.exceptions.UserAlreadyExistsException;
import su.iota.backend.accounts.exceptions.UserNotFoundException;
import su.iota.backend.models.UserProfile;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

//@Rank(-100)
@Named
@Service
@Singleton
public class AccountServiceMapImpl implements AccountService {

    private final AtomicLong userIdGenerator = new AtomicLong(1L);
    private final ConcurrentMap<Long, UserProfile> userProfiles = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> userPasswords = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> userIds = new ConcurrentHashMap<>();

    @Override
    public long createUser(@NotNull UserProfile userProfile) throws UserAlreadyExistsException {
        final String userLogin = userProfile.getLogin();
        if (userIds.containsKey(userLogin)) {
            throw new UserAlreadyExistsException();
        }
        final long userId = userIdGenerator.getAndIncrement();
        userProfile.setId(userId);
        userIds.put(userLogin, userId);
        userProfiles.put(userId, userProfile);
        userPasswords.put(userId, userProfile.getPassword());
        return userId;
    }

    @Override
    public void editUser(long userId, @NotNull UserProfile newUserProfile) throws UserNotFoundException, UserAlreadyExistsException {
        final String newUserLogin = newUserProfile.getLogin();
        if (newUserLogin.isEmpty()) {
            return;
        }
        if (userIds.containsKey(newUserLogin)) {
            throw new UserAlreadyExistsException();
        }
        final UserProfile oldUserProfile = userProfiles.get(userId);
        if (oldUserProfile == null) {
            throw new UserNotFoundException();
        }
        userPasswords.replace(userId, newUserProfile.getPassword());
        userIds.remove(oldUserProfile.getLogin());
        userIds.put(newUserLogin, userId);
        oldUserProfile.setLogin(newUserLogin);
        oldUserProfile.setEmail(newUserProfile.getEmail());
    }

    @Override
    public void deleteUser(long userId) throws UserNotFoundException {
        if (!userProfiles.containsKey(userId)) {
            throw new UserNotFoundException();
        }
        userPasswords.remove(userId);
        final UserProfile userProfile = userProfiles.get(userId);
        userIds.remove(userProfile.getLogin());
        userProfiles.remove(userId);
    }

    @Override
    public @Nullable Long getUserId(@NotNull String userLogin) {
        return userIds.get(userLogin);
    }

    @Override
    public @Nullable UserProfile getUserProfile(@NotNull String userLogin) {
        final Long userId = userIds.get(userLogin);
        if (userId == null) {
            return null;
        }
        return getUserProfile(userId);
    }

    @Override
    public @Nullable UserProfile getUserProfile(long userId) {
        return userProfiles.get(userId);
    }

    @Override
    public boolean isUserPasswordCorrect(long userId, @NotNull String password) throws UserNotFoundException {
        if (!userPasswords.containsKey(userId)) {
            throw new UserNotFoundException();
        }
        return password.equals(userPasswords.get(userId));
    }

    @Override
    public boolean isUserExistent(long userId) {
        return userProfiles.containsKey(userId);
    }

    @Override
    public boolean isUserExistent(@NotNull String userLogin) {
        return userIds.containsKey(userLogin);
    }

}
