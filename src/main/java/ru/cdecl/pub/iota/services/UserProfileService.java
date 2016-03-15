package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cdecl.pub.iota.models.UserEditRequest;
import ru.cdecl.pub.iota.models.UserProfile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfileService {

    private final Map<Long, UserProfile> users = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> nameToProfile = new ConcurrentHashMap<>();

    public UserProfileService() {
        this(null);
    }

    public UserProfileService(@Nullable AtomicLong idGenerator) {
        this.idGenerator = (idGenerator != null)
                ? idGenerator
                : new AtomicLong(0);
    }

    public boolean isUserPresent(@NotNull String login) {
        return nameToProfile.containsKey(login);
    }

    public boolean addUser(@NotNull UserProfile userProfile) {
        if (isUserPresent(userProfile.getLogin())) {
            return false;
        }

        userProfile.setUserId(idGenerator.getAndIncrement());
        users.put(userProfile.getUserId(), userProfile);
        nameToProfile.put(userProfile.getLogin(), userProfile);

        return true;
    }

    public boolean updateUser(long userId, UserEditRequest userEditRequest) {
        @Nullable final UserProfile userProfile = users.get(userId);

        if (userProfile == null) {
            return false;
        }

        @Nullable final String newLogin = userEditRequest.getLogin();

        if (newLogin != null) {
            if (isUserPresent(newLogin)) {
                return false;
            }

            nameToProfile.remove(userProfile.getLogin());
            userProfile.setLogin(newLogin);
            nameToProfile.put(newLogin, userProfile);
        }

        @Nullable final String newEmail = userEditRequest.getEmail();

        if (newEmail != null) {
            userProfile.setEmail(newEmail);
        }

        users.put(userId, userProfile);

        return true;
    }

    public void deleteUser(long userId) {
        @Nullable final UserProfile userProfile = users.get(userId);

        if(userProfile == null) {
            return;
        }

        nameToProfile.remove(userProfile.getLogin());
        users.remove(userId);
    }

    @Nullable
    public UserProfile getUserByLogin(@NotNull String login) {
        return nameToProfile.get(login);
    }

    @Nullable
    public UserProfile getUserById(@NotNull Long userId) {
        return users.get(userId);
    }

    private final AtomicLong idGenerator;

}
