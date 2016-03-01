package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cdecl.pub.iota.models.UserEditRequest;
import ru.cdecl.pub.iota.models.UserProfile;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserProfileService {

    private final Map<Long, UserProfile> users = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> nameToProfile = new ConcurrentHashMap<>();

    @NotNull
    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    public boolean addUser(@NotNull Long userId, @NotNull UserProfile userProfile) {
        if (users.containsKey(userId)) {
            return false;
        }

        if(nameToProfile.containsKey(userProfile.getLogin())) {
            return false;
        }

        users.put(userId, userProfile);
        nameToProfile.put(userProfile.getLogin(), userProfile);

        return true;
    }

    public void updateUser(long userId, UserEditRequest userEditRequest) {
        final UserProfile userProfile = users.get(userId);

        if (userProfile != null) {
            final String newLogin = userEditRequest.getLogin();

            if (newLogin != null) {
                if (nameToProfile.containsKey(newLogin)) {
                    throw new IllegalArgumentException("User with this name already exists.");
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
        }
    }

    public void deleteUser(long userId) {
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

}
