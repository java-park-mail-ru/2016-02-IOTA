package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

public class UserProfile {

    private long userId;
    @NotNull
    private String login;
    @NotNull
    private String email;

    public UserProfile() {
        userId = ID_GENERATOR.getAndIncrement();
        login = "";
        email = "";
    }

    public UserProfile(@NotNull String login, @NotNull String email) {
        this.login = login;
        this.email = email;
    }

    public synchronized long getUserId() {
        return userId;
    }

    @NotNull
    public synchronized String getLogin() {
        return login;
    }

    public synchronized void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public synchronized String getEmail() {
        return email;
    }

    public synchronized void setEmail(@NotNull String email) {
        this.email = email;
    }

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

}
