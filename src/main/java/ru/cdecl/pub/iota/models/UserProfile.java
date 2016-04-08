package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class UserProfile {

    @Nullable
    private Long id;

    @NotNull
    private String login = "";

    @NotNull
    private String email = "";

    @Nullable
    private Date birthDate = null;

    public UserProfile(@Nullable Long id, @NotNull String login, @NotNull String email) {
        this(login, email);
        this.id = id;
    }

    public UserProfile(@NotNull String login, @NotNull String email) {
        setLogin(login);
        setEmail(email);
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
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
    
}
