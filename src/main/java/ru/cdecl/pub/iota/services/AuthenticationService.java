package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationService {

    private final Map<Long, String> userPasswords = new ConcurrentHashMap<>();

    public boolean checkPassword(@Nullable Long userId, @NotNull String password) {
        final String storedPassword = userPasswords.get(userId);

        return storedPassword != null && storedPassword.equals(password.trim());
    }

    public void setPasswordForUser(@NotNull Long userId, @Nullable String password) {
        if (password != null) {
            userPasswords.put(userId, password.trim());
        }
    }

    public void deletePasswordForUser(@NotNull Long userId) {
        userPasswords.remove(userId);
    }

}
