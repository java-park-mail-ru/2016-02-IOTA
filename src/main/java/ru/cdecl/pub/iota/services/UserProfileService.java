package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cdecl.pub.iota.models.UserCreateRequest;
import ru.cdecl.pub.iota.models.UserProfile;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserProfileService {

    private ConcurrentMap<Long, UserProfile> users = new ConcurrentHashMap<>();

    public UserProfileService() {
        // todo: удалить
        UserCreateRequest[] userCreateRequests = new UserCreateRequest[]{
                new UserCreateRequest("admin", "admin@example.com", "12345"),
                new UserCreateRequest("guest", "guest@example.com", "11")
        };

        for (UserCreateRequest userCreateRequest : userCreateRequests) {
            users.putIfAbsent(userCreateRequest.getUserId(), userCreateRequest);
        }
    }

    @NotNull
    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    public synchronized boolean addUser(@NotNull Long userId, @NotNull UserProfile userProfile) {
        if (users.containsKey(userId)) {
            return false;
        }

        for (UserProfile aUserProfile : users.values()) {
            if (aUserProfile.getLogin().equals(userProfile.getLogin())) {
                return false;
            }
        }

        users.put(userId, userProfile);

        return true;
    }

    @Nullable
    public UserProfile getUserByLogin(@NotNull String login) {
        for (UserProfile userProfile : users.values()) {
            if (userProfile.getLogin().equals(login)) {
                return userProfile;
            }
        }

        return null;
    }

    @Nullable
    public UserProfile getUserById(@NotNull Long userId) {
        return users.get(userId);
    }

}
