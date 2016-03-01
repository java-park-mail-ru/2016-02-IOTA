package ru.cdecl.pub.iota.services;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AuthenticationService {

    private ConcurrentMap<Long, char[]> userPasswords = new ConcurrentHashMap<>();

    public boolean checkPassword(@NotNull Long userId, @NotNull char[] password) {
        char[] storedPassword = userPasswords.get(userId);

        return storedPassword != null && Arrays.equals(storedPassword, password);
    }

    public void setPasswordForUser(@NotNull Long userId, @NotNull char[] password) {
        userPasswords.put(userId, password);
    }
}
