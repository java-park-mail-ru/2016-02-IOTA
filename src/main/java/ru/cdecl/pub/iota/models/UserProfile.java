package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cdecl.pub.iota.annotations.UserProfileDetailedView;
import ru.cdecl.pub.iota.annotations.UserProfileIdView;

import javax.xml.bind.annotation.XmlElement;

public class UserProfile {

    @Nullable
    private Long userId;
    @NotNull
    private String login;
    @NotNull
    private String email;

    public UserProfile() {
        this("", "");
    }

    public UserProfile(@NotNull String login, @NotNull String email) {
        userId = null;
        this.login = login;
        this.email = email;
    }

    @Nullable
    @XmlElement(name = "id")
    @UserProfileIdView
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@Nullable Long userId) {
        this.userId = userId;
    }

    @NotNull
    @UserProfileDetailedView
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login.trim();
    }

    @NotNull
    @UserProfileDetailedView
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email.trim();
    }

}
