package su.iota.backend.models;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

public class UserProfile {

    @Expose
    private @Nullable Long id;

    @Expose
    private @Nullable String login;

    @Expose
    private @Nullable String email;

    private @Nullable String password;

    private @Nullable DateTime birthDate;

    public UserProfile() {
    }

    public UserProfile(long id, @Nullable String login, @Nullable String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public @Nullable String getLogin() {
        return login;
    }

    public void setLogin(@Nullable String login) {
        this.login = login;
    }

    public @Nullable String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    public @Nullable String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    public @Nullable DateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(@Nullable DateTime birthDate) {
        this.birthDate = birthDate;
    }

}
