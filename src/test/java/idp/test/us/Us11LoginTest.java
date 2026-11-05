package idp.test.us;

import idp.boundary.*;
import idp.database.Database;
import idp.dto.*;
import idp.entity.*;
import idp.enumeration.*;
import idp.session.SessionManager;
import idp.test.IdpTestCase;
import idp.util.Clock;
import idp.util.Messages;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class Us11LoginTest extends IdpTestCase {

    @Test
    public void testLoginWithUsername() {
        customer("alice");
        loginPg page = new loginPg();
        page.setUsernameOrEmail("alice");
        page.setPassword(PASSWORD);
        LoginResult result = page.login();
        assertTrue(result.success());
        assertEquals("Login successful.", result.message());
        assertNotNull(result.sessionId());
        assertNotNull(result.account());
        assertEquals(Role.CUSTOMER, result.role());
        assertTrue(SessionManager.get().isLoggedIn());
        assertEquals(Role.CUSTOMER, SessionManager.get().currentRole());
    }

    @Test
    public void testLoginWithEmail() {
        UserAccount alice = customer("alice");
        loginPg page = new loginPg();
        page.setUsernameOrEmail("alice@idp.test");
        page.setPassword(PASSWORD);
        LoginResult result = page.login();
        assertTrue(result.success());
        assertEquals(alice.getAccountID(), result.account().getAccountID());
        assertEquals(alice.getAccountID(), SessionManager.get().currentAccountId());
    }

    @Test
    public void testLoginFailsWhenPasswordIsWrong() {
        account("alice", "alice@idp.test", PASSWORD, Role.CUSTOMER);
        loginPg page = new loginPg();
        page.setUsernameOrEmail("alice");
        page.setPassword("WrongPass1");
        LoginResult result = page.login();
        assertFalse(result.success());
        assertEquals(Messages.LOGIN_WRONG, result.message());
        assertNull(result.sessionId());
        assertFalse(SessionManager.get().isLoggedIn());
        assertEquals(0, SessionManager.get().activeSessionCount());
    }

    @Test
    public void testLoginFailsWhenUsernameIsWrong() {
        account("alice", "alice@idp.test", PASSWORD, Role.CUSTOMER);
        loginPg page = new loginPg();
        page.setUsernameOrEmail("alice2");
        page.setPassword(PASSWORD);
        LoginResult result = page.login();
        assertFalse(result.success());
        assertEquals(Messages.LOGIN_WRONG, result.message());
        assertFalse(SessionManager.get().isLoggedIn());
    }

    @Test
    public void testLoginFailsWhenEmailIsWrong() {
        account("alice", "alice@idp.test", PASSWORD, Role.CUSTOMER);
        loginPg page = new loginPg();
        page.setUsernameOrEmail("alice@other.test");
        page.setPassword(PASSWORD);
        LoginResult result = page.login();
        assertFalse(result.success());
        assertEquals(Messages.LOGIN_WRONG, result.message());
    }

    @Test
    public void testLoginFailsWhenFieldsAreMissing() {
        account("alice", "alice@idp.test", PASSWORD, Role.CUSTOMER);
        loginPg missingPassword = new loginPg();
        missingPassword.setUsernameOrEmail("alice");
        missingPassword.setPassword("");
        assertEquals(Messages.FILL_REQUIRED_FIELDS, missingPassword.login().message());
        loginPg missingUsername = new loginPg();
        missingUsername.setUsernameOrEmail("  ");
        missingUsername.setPassword(PASSWORD);
        assertEquals(Messages.FILL_REQUIRED_FIELDS, missingUsername.login().message());
        assertFalse(SessionManager.get().isLoggedIn());
    }

    @Test
    public void testLoginFailsWhenAccountIsSuspended() {
        UserAccount banned = account("banned", "banned@idp.test", PASSWORD, Role.CUSTOMER);
        banned.setStatus(AccountStatus.SUSPENDED);
        loginPg page = new loginPg();
        page.setUsernameOrEmail("banned");
        page.setPassword(PASSWORD);
        LoginResult result = page.login();
        assertFalse(result.success());
        assertEquals(Messages.ACCOUNT_NOT_ACTIVE, result.message());
        assertFalse(SessionManager.get().isLoggedIn());
    }

    @Test
    public void testLoginWorksForEveryRole() {
        account("admin", "admin@idp.test", PASSWORD, Role.USER_ADMIN);
        account("designer", "designer@idp.test", PASSWORD, Role.INTERIOR_DESIGNER);
        account("customer", "customer@idp.test", PASSWORD, Role.CUSTOMER);
        account("manager", "manager@idp.test", PASSWORD, Role.PLATFORM_MANAGEMENT);
        String[] names = {"admin", "designer", "customer", "manager"};
        Role[] roles = {Role.USER_ADMIN, Role.INTERIOR_DESIGNER, Role.CUSTOMER, Role.PLATFORM_MANAGEMENT};
        for (int i = 0; i < names.length; i++) {
            loginPg page = new loginPg();
            page.setUsernameOrEmail(names[i]);
            page.setPassword(PASSWORD);
            LoginResult result = page.login();
            assertTrue(names[i] + " should log in", result.success());
            assertEquals(roles[i], result.role());
            assertEquals(roles[i], SessionManager.get().currentRole());
        }
    }
}
