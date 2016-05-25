package su.iota.backend.accounts;

import org.jvnet.testing.hk2testng.HK2;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import su.iota.backend.accounts.exceptions.UserAlreadyExistsException;
import su.iota.backend.accounts.impl.AccountServiceMapImpl;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import javax.inject.Named;

import static org.testng.Assert.*;

@HK2
public class AccountServiceTest {

    private final String login = "Ustimov";
    private final String password = "Qwerty123!";
    private final String email = "art@ustimov.org";

    private final String login2 = "admin1";
    private final String password2 = "admin1";
    private final String email2 = "admin1@mail.ru";

    @Inject
    @Named("AccountServiceMapImpl")
    private AccountService accountService;

    @Test
    public void testAccountServiceNamedInjection() throws Exception {
        assertEquals(accountService.getClass(), AccountServiceMapImpl.class);
    }

    @AfterMethod
    public void tearDown() throws Exception{
        deleteUserWithLogin(login);
        deleteUserWithLogin(login2);
    }

    public void deleteUserWithLogin(String userLogin) throws Exception {
        if (accountService.isUserExistent(userLogin)) {
            final Long id = accountService.getUserId(userLogin);
            if (id == null) {
                return;
            }
            accountService.deleteUser(id);
        }
    }

    @Test(expectedExceptions = UserAlreadyExistsException.class)
    public void testCreateUserWithUsedEmail() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile newUserProfile = new UserProfile(login2, email, password2);
        accountService.createUser(newUserProfile);
    }

    @Test(expectedExceptions = UserAlreadyExistsException.class)
    public void testCreateUserWithUsedLogin() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile newUserProfile = new UserProfile(login, email2, password2);
        accountService.createUser(newUserProfile);
    }

    @Test(expectedExceptions = UserAlreadyExistsException.class)
    public void testRepeatedCreateUser() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        accountService.createUser(userProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithNullData() throws Exception {
        final UserProfile newUserProfile = new UserProfile();
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithEmptyData() throws Exception {
        final UserProfile newUserProfile = new UserProfile("", "", "");
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithEmptyLogin() throws Exception {
        final UserProfile newUserProfile = new UserProfile("", email2, password2);
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithNullLogin() throws Exception {
        final UserProfile newUserProfile = new UserProfile(null, email2, password2);
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithEmptyEmail() throws Exception {
        final UserProfile newUserProfile = new UserProfile(login2, "", password2);
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithNullEmail() throws Exception {
        final UserProfile newUserProfile = new UserProfile(login2, null, password2);
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithEmptyPassword() throws Exception {
        final UserProfile newUserProfile = new UserProfile(login2, email2, "");
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithNullPassword() throws Exception {
        final UserProfile newUserProfile = new UserProfile(login2, email2, null);
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithBadEmail() throws Exception {
        final UserProfile newUserProfile = new UserProfile(login2, "email", password2); // No @
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithSpaceInLogin() throws Exception {
        final UserProfile newUserProfile = new UserProfile("some login", email2, password2);
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithShortLogin() throws Exception {
        final UserProfile newUserProfile = new UserProfile("login", email2, password2); // Min length is 6
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithWithBadLogin() throws Exception {
        final UserProfile newUserProfile = new UserProfile("логин!#@", email2, password2); // /^[a-z0-9]+$/i
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithShortPassword() throws Exception {
        final UserProfile newUserProfile = new UserProfile(login2, email2, "pwd"); // Min length is 6
        accountService.createUser(newUserProfile);
    }

    // Todo: specify exception type
    @Test(expectedExceptions = Exception.class)
    public void testCreateUserWithBadPassword() throws Exception {
        final UserProfile newUserProfile = new UserProfile(login2, email2, "пароль!#@"); // /^[a-z0-9]+$/i
        accountService.createUser(newUserProfile);
    }

    @Test
    public void testEditUser() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile newUserProfile = new UserProfile(login2, email2, password2);
        accountService.editUser(userId, newUserProfile);
        final UserProfile updatedUserProfile = accountService.getUserProfile(userId);
        assertEquals(newUserProfile, updatedUserProfile);
    }

    @Test
    public void testUpdateEmail() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile newUserProfile = new UserProfile(login, email2, password);
        accountService.editUser(userId, newUserProfile);
        final UserProfile updatedUserProfile = accountService.getUserProfile(userId);
        assertEquals(newUserProfile, updatedUserProfile);
    }

    @Test
    public void testUpdatePassword() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile newUserProfile = new UserProfile(login, email, password2);
        accountService.editUser(userId, newUserProfile);
        final UserProfile updatedUserProfile = accountService.getUserProfile(userId);
        assertEquals(newUserProfile, updatedUserProfile);
    }

    @Test
    public void testUpdateLogin() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile newUserProfile = new UserProfile(login2, email, password);
        accountService.editUser(userId, newUserProfile);
        final UserProfile updatedUserProfile = accountService.getUserProfile(userId);
        assertEquals(newUserProfile, updatedUserProfile);
    }

    @Test
    public void testDeleteUser() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        accountService.deleteUser(userId);
        assertFalse(accountService.isUserExistent(userId));
    }

    @Test
    public void testGetUserId() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final Long userIdByLogin = accountService.getUserId(login);
        if (userIdByLogin != null) {
            assertEquals((long) userIdByLogin, userId);
        }
    }

    @Test
    public void testGetUserProfileByLogin() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile userProfileByLogin = accountService.getUserProfile(login);
        assertEquals(userProfile, userProfileByLogin);
    }

    @Test
    public void testGetUserProfileById() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        final UserProfile userProfileById = accountService.getUserProfile(userId);
        assertEquals(userProfile, userProfileById);
    }

    @Test
    public void testIsUserPasswordCorrect() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        assertTrue(accountService.isUserPasswordCorrect(userId, password));
    }

    @Test
    public void testIsUserExistentByLogin() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        assertTrue(accountService.isUserExistent(login));
    }

    @Test
    public void testIsUserExistentById() throws Exception {
        final UserProfile userProfile = new UserProfile(login, email, password);
        final long userId = accountService.createUser(userProfile);
        assertTrue(accountService.isUserExistent(userId));
    }
}