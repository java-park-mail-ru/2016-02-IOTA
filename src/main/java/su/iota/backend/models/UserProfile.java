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

    public synchronized @Nullable Long getId() {
        return id;
    }

    public synchronized void setId(@Nullable Long id) {
        this.id = id;
    }

    public synchronized @Nullable String getLogin() {
        return login;
    }

    public synchronized void setLogin(@Nullable String login) {
        this.login = login;
    }

    public synchronized @Nullable String getEmail() {
        return email;
    }

    public synchronized void setEmail(@Nullable String email) {
        this.email = email;
    }

    public synchronized @Nullable String getPassword() {
        return password;
    }

    public synchronized void setPassword(@Nullable String password) {
        this.password = password;
    }

    public synchronized @Nullable DateTime getBirthDate() {
        return birthDate;
    }

    public synchronized void setBirthDate(@Nullable DateTime birthDate) {
        this.birthDate = birthDate;
    }

}
