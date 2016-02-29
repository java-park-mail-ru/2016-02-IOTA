package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cdecl.pub.iota.models.UserProfile;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserProfileService {

    private ConcurrentMap<Long, UserProfile> users = new ConcurrentHashMap<>();

    public UserProfileService() {
        UserProfile[] userProfiles = new UserProfile[]{
                new UserProfile("admin", "admin", "admin@example.com"),
                new UserProfile("guest", "12345", "guest@example.com")
        };

        for (UserProfile userProfile : userProfiles) {
            users.putIfAbsent(userProfile.getUserId(), userProfile);
        }
    }

    @NotNull
    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    public boolean addUser(@NotNull Long userId, @NotNull UserProfile userProfile) {
        return null != users.putIfAbsent(userId, userProfile);
    }

    @Nullable
    public UserProfile getUserById(@NotNull Long userId) {
        return users.get(userId);
    }

}
