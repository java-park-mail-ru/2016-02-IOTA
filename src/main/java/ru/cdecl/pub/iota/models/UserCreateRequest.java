package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlTransient;

public class UserCreateRequest extends UserProfile {

    @NotNull
    private String password;

    public UserCreateRequest() {
        this("", "", "");
    }

    public UserCreateRequest(@NotNull String login, @NotNull String email, @NotNull String password) {
        super(login, email);
        this.password = password;
    }

    public void eraseSensitiveData() {
        password = "__erased__";
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }
}
