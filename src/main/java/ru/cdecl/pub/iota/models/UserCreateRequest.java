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
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login.trim();
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email.trim();
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password.trim();
    }

    public boolean isValid() {
        return !login.isEmpty() && !email.isEmpty() && !password.isEmpty();
    }

}
