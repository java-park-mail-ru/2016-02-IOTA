package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;

public class UserLoginRequest {

    @NotNull
    private String login;
    @NotNull
    private String password;

    public UserLoginRequest() {
        this("", "");
    }

    protected UserLoginRequest(@NotNull String login, @NotNull String password) {
        this.login = login;
        this.password = password;
    }

    @NotNull
    public synchronized String getLogin() {
        return login;
    }

    public synchronized void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public synchronized String getPassword() {
        return password;
    }

    public synchronized void setPassword(@NotNull String password) {
        this.password = password;
    }

}
