package su.iota.backend.accounts;

import org.jvnet.testing.hk2testng.HK2;
import org.omg.CORBA.portable.Streamable;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import su.iota.backend.accounts.impl.AccountServiceMapImpl;
import su.iota.backend.models.UserProfile;

import javax.inject.Inject;
import javax.inject.Named;

import static org.testng.Assert.*;

@HK2
public class AccountServiceTest {

    private final String login = "Ustimov";
    private final String password = "Qwerty123!";
    private UserProfile userProfile = new UserProfile();
    private long userId;

    @Inject
    @Named("AccountServiceMapImpl")
    private AccountService accountService;

    @Test
    public void testAccountServiceNamedInjection() throws Exception {
        assertEquals(accountService.getClass(), AccountServiceMapImpl.class);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        userProfile.setLogin(login);
        userProfile.setPassword(password);
        userProfile.setEmail("art@ustimov.org");
        userId = accountService.createUser(userProfile);
    }

    @AfterMethod
    public void tearDown() throws Exception{
        if (accountService.isUserExistent(userId)) {
            accountService.deleteUser(userId);
        }
    }

    @Test
    public void testDeleteUser() throws Exception {
        accountService.deleteUser(userId);
        assertFalse(accountService.isUserExistent(userId));
    }

    @Test
    public void testGetUserId() throws Exception {
        final Long userIdByLogin = accountService.getUserId(login);
        if (userIdByLogin != null) {
            assertEquals((long) userIdByLogin, userId);
        }
    }

    @Test
    public void testGetUserProfileByLogin() throws Exception {
        final UserProfile userProfileByLogin = accountService.getUserProfile(login);
        assertEquals(userProfile, userProfileByLogin);
    }

    @Test
    public void testGetUserProfileById() throws Exception {
        final UserProfile userProfileById = accountService.getUserProfile(userId);
        assertEquals(userProfile, userProfileById);
    }

    @Test
    public void testIsUserPasswordCorrect() throws Exception {
        assertTrue(accountService.isUserPasswordCorrect(userId, password));
    }

    @Test
    public void testIsUserExistentByLogin() throws Exception {
        assertTrue(accountService.isUserExistent(login));
    }

    @Test
    public void testIsUserExistentById() throws Exception {
        assertTrue(accountService.isUserExistent(userId));
    }
}