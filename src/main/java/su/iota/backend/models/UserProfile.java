package su.iota.backend.models;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.mockito.internal.matchers.Equals;

import javax.jws.soap.SOAPBinding;

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(this.id)
                .append(this.login)
                .append(this.email)
                .append(this.password)
                .append(this.birthDate)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof UserProfile)) {
            return false;
        }

        final UserProfile userProfile = (UserProfile) obj;
        return new EqualsBuilder()
                .append(this.id, userProfile.id)
                .append(this.login, userProfile.login)
                .append(this.email, userProfile.email)
                .append(this.password, userProfile.password)
                .append(this.birthDate, userProfile.birthDate)
                .isEquals();
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

}
