package su.iota.backend.models;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

public class UserProfile {

    @Nullable
    @Expose
    private Long id;

    @Nullable
    @Expose
    private String login;

    @Nullable
    @Expose
    private String email;

    @Nullable
    private String password;

    @Nullable
    private DateTime birthDate;

    public UserProfile() {
    }

    public UserProfile(long id, @Nullable String login, @Nullable String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }

    public UserProfile(@Nullable String login, @Nullable String email, @Nullable String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nullable
    public String getLogin() {
        return login;
    }

    public void setLogin(@Nullable String login) {
        this.login = login;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @Nullable
    public DateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(@Nullable DateTime birthDate) {
        this.birthDate = birthDate;
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserProfile userProfile = (UserProfile) o;

        if (id != null ? !id.equals(userProfile.id) : userProfile.id != null) return false;
        if (login != null ? !login.equals(userProfile.login) : userProfile.login != null) return false;
        if (email != null ? !email.equals(userProfile.email) : userProfile.email != null) return false;
        //noinspection SimplifiableIfStatement
        if (password != null ? !password.equals(userProfile.password) : userProfile.password != null) return false;
        return birthDate != null ? birthDate.equals(userProfile.birthDate) : userProfile.birthDate == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        return result;
    }
}
