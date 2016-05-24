package su.iota.backend.accounts;

import org.jvnet.testing.hk2testng.HK2;
import org.testng.annotations.Test;
import su.iota.backend.accounts.impl.AccountServiceMapImpl;

import javax.inject.Inject;
import javax.inject.Named;

import static org.testng.Assert.*;

@HK2
public class AccountServiceTest {

    @Inject
    @Named("AccountServiceMapImpl")
    private AccountService accountService;

    @Test
    public void testAccountServiceNamedInjection() throws Exception {
        assertEquals(accountService.getClass(), AccountServiceMapImpl.class);
    }

    @Test
    public void testCreateUser() throws Exception {
        //
    }

    @Test
    public void testEditUser() throws Exception {
        //
    }

    @Test
    public void testDeleteUser() throws Exception {
        //
    }

    @Test
    public void testGetUserId() throws Exception {
        //
    }

    @Test
    public void testGetUserProfileByLogin() throws Exception {
        //
    }

    @Test
    public void testGetUserProfileById() throws Exception {
        //
    }

    @Test
    public void testIsUserPasswordCorrect() throws Exception {
        //
    }

    @Test
    public void testIsUserExistentByLogin() throws Exception {
        //
    }

    @Test
    public void testIsUserExistentById() throws Exception {
        //
    }

}