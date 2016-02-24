package main;

import org.jetbrains.annotations.NotNull;

/**
 * @author esin88
 */
public class UserProfile {
    @NotNull
    private String login;
    @NotNull
    private String password;

    public UserProfile(@NotNull String login, @NotNull String password) {
        this.login = login;
        this.password = password;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }
}
