package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfile implements Serializable, Cloneable {

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

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull String email) {
        this.login = login;
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

}
