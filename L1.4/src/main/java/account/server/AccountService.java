package account.server;

/**
 * Created by jim on 3/13/15.
 */
public interface AccountService {
    void addNewUser();

    void removeUser();

    int getUsersLimit();

    void setUsersLimit(int usersLimit);

    int getUsersCount();
}
