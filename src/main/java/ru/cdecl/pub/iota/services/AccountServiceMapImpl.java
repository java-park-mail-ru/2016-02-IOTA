package ru.cdecl.pub.iota.services;

import org.glassfish.hk2.api.Rank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.UserAlreadyExistsException;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.models.UserProfile;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Rank(-100)
@Named
@Service
@Singleton
public class AccountServiceMapImpl implements AccountService {

    private final AtomicLong userIdGenerator = new AtomicLong(1L);
    private final ConcurrentMap<Long, UserProfile> userProfiles = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, char[]> userPasswords = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> userIds = new ConcurrentHashMap<>();

    @Override
    public long createUser(@NotNull UserProfile userProfile, char[] password) throws UserAlreadyExistsException {
        final String userLogin = userProfile.getLogin();
        if (userIds.containsKey(userLogin)) {
            throw new UserAlreadyExistsException();
        }
        final long userId = userIdGenerator.getAndIncrement();
        userProfile.setId(userId);
        userIds.put(userLogin, userId);
        userProfiles.put(userId, userProfile);
        userPasswords.put(userId, password);
        return userId;
    }

    @Override
    public void editUser(long userId, @NotNull UserProfile newUserProfile, char[] newPassword) throws UserNotFoundException, UserAlreadyExistsException {
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
        final char[] oldPassword = userPasswords.get(userId);
        Arrays.fill(oldPassword, '\0');
        userPasswords.replace(userId, newPassword);
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
        final char[] userPassword = userPasswords.get(userId);
        Arrays.fill(userPassword, '\0');
        userPasswords.remove(userId);
        final UserProfile userProfile = userProfiles.get(userId);
        userIds.remove(userProfile.getLogin());
        userProfiles.remove(userId);
    }

    @Override
    @Nullable
    public Long getUserId(@NotNull String userLogin) {
        return userIds.get(userLogin);
    }

    @Override
    @Nullable
    public UserProfile getUserProfile(@NotNull String userLogin) {
        final Long userId = userIds.get(userLogin);
        if (userId == null) {
            return null;
        }
        return getUserProfile(userId);
    }

    @Override
    @Nullable
    public UserProfile getUserProfile(long userId) {
        return userProfiles.get(userId);
    }

    @Override
    public boolean isUserPasswordCorrect(long userId, char[] password) throws UserNotFoundException {
        if (!userPasswords.containsKey(userId)) {
            throw new UserNotFoundException();
        }
        final boolean isPasswordCorrect = Arrays.equals(password, userPasswords.get(userId));
        Arrays.fill(password, '\0');
        return isPasswordCorrect;
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
