package account.server;


@SuppressWarnings("UnusedDeclaration")
public interface AccountServerControllerMBean {
    int getUsers();
    int getUsersLimit();
    void setUsersLimit(int usersLimit);
}
