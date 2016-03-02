package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.Nullable;

public class UserEditRequest {

    @Nullable
    private String login;
    @Nullable
    private String email;
    @Nullable
    private String password;

    public UserEditRequest() {
        this(null, null, null);
    }

    public UserEditRequest(@Nullable String login, @Nullable String email, @Nullable String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    @Nullable
    public String getLogin() {
        return login;
    }

    public void setLogin(@Nullable String login) {
        this.login = (login != null) ? login.trim() : null;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = (email != null) ? email.trim() : null;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = (password != null) ? password.trim() : null;
    }

    public boolean isValid() {
        boolean isValid = (login == null || !login.isEmpty());

        isValid = isValid && (email == null || !email.isEmpty());
        isValid = isValid && (password == null || !password.isEmpty());

        return isValid;
    }
}
