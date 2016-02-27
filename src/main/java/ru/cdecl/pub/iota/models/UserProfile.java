package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;

public class UserProfile {

    @NotNull
    private String login;
    @NotNull
    private String password;

    public UserProfile() {
        login = "";
        password = "";
    }

    public UserProfile(@NotNull String login, @NotNull String password) {
        this.login = login;
        this.password = password;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

}
