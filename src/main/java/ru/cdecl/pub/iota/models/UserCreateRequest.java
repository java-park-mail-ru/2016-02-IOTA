package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;

public class UserCreateRequest {

    @NotNull
    private String login;
    @NotNull
    private String email;
    @NotNull
    private String password;

    public UserCreateRequest() {
        this("", "", "");
    }

    public UserCreateRequest(@NotNull String login, @NotNull String email, @NotNull String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    @NotNull
    public UserProfile toUserProfile() {
        return new UserProfile(login, email);
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

    @NotNull
    public synchronized String getPassword() {
        return password;
    }

    public synchronized void setPassword(@NotNull String password) {
        this.password = password;
    }

}
