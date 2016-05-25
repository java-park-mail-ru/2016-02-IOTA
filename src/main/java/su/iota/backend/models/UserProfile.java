package su.iota.backend.models;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

public class UserProfile {

    @NotNull
    @Expose
    private Long id = -1L;

    @NotNull
    @Expose
    private String login = "";

    @NotNull
    @Expose
    private String email = "";

    @NotNull
    private String password = "";

    @NotNull
    private DateTime birthDate = new DateTime();

    public UserProfile() {
    }

    public UserProfile(long id, @NotNull String login, @NotNull String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }

    public UserProfile(@NotNull String login, @NotNull String email, @NotNull String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    @NotNull
    public DateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(@NotNull DateTime birthDate) {
        this.birthDate = birthDate;
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserProfile userProfile = (UserProfile) o;

        if (!id.equals(userProfile.id)) return false;
        if (!login.equals(userProfile.login)) return false;
        if (!email.equals(userProfile.email)) return false;
        //noinspection SimplifiableIfStatement
        if (!password.equals(userProfile.password)) return false;
        return birthDate.equals(userProfile.birthDate);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + login.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + birthDate.hashCode();
        return result;
    }
}
