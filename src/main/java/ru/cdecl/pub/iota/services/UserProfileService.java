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

    public boolean isUserPresent(@NotNull String login) {
        return nameToProfile.containsKey(login);
    }

    public boolean addUser(@NotNull UserProfile userProfile) {
        if (isUserPresent(userProfile.getLogin())) {
            return false;
        }

        userProfile.setUserId(ID_GENERATOR.getAndIncrement());
        users.put(userProfile.getUserId(), userProfile);
        nameToProfile.put(userProfile.getLogin(), userProfile);

        return true;
    }

    public boolean updateUser(long userId, UserEditRequest userEditRequest) {
        final UserProfile userProfile = users.get(userId);

        if (userProfile == null) {
            return false;
        }

        final String newLogin = userEditRequest.getLogin();

        if (newLogin != null) {
            if (isUserPresent(newLogin)) {
                return false;
            }

            nameToProfile.remove(userProfile.getLogin());
            userProfile.setLogin(newLogin);
            nameToProfile.put(newLogin, userProfile);
        }

        final String newEmail = userEditRequest.getEmail();

        if (newEmail != null) {
            userProfile.setEmail(newEmail);
        }

        users.put(userId, userProfile);

        return true;
    }

    public void deleteUser(long userId) {
        final UserProfile userProfile = users.get(userId);

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

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

}
