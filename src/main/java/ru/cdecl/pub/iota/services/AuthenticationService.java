package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AuthenticationService {

    private final ConcurrentMap<Long, char[]> userPasswords = new ConcurrentHashMap<>();

    public boolean checkPassword(@NotNull Long userId, @NotNull char[] password) {
        char[] storedPassword = userPasswords.get(userId);

        return storedPassword != null && Arrays.equals(storedPassword, password);
    }

    public void setPasswordForUser(@NotNull Long userId, @NotNull char[] password) {
        userPasswords.put(userId, password);
    }

    public void setPasswordForUser(@NotNull Long userId, @Nullable String password) {
        if (password != null) {
            setPasswordForUser(userId, password.toCharArray());
        }
    }

    public void deletePasswordForUser(@NotNull Long userId) {
        userPasswords.remove(userId);
    }
}
