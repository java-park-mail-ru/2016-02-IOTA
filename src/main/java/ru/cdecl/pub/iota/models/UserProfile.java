package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cdecl.pub.iota.annotations.UserProfileDetailedView;
import ru.cdecl.pub.iota.annotations.UserProfileIdView;

import javax.xml.bind.annotation.XmlElement;

public class UserProfile {

    @Nullable
    @UserProfileIdView
    private Long userId;
    @NotNull
    @UserProfileDetailedView
    private String login;
    @NotNull
    @UserProfileDetailedView
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
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@Nullable Long userId) {
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

}
