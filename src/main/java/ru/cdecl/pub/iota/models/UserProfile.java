package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlElement;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfile {

    private long userId;
    @NotNull
    private String login;
    @NotNull
    private String email;

    public UserProfile() {
        this("", "");
    }

    public UserProfile(@NotNull String login, @NotNull String email) {
        userId = ID_GENERATOR.getAndIncrement();
        this.login = login;
        this.email = email;
    }

    @XmlElement(name = "id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

}
