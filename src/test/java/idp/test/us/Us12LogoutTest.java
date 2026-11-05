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

public class Us12LogoutTest extends IdpTestCase {

    @Test
    public void testLogout() {
        customer("alice");
        loginPg login = new loginPg();
        login.setUsernameOrEmail("alice");
        login.setPassword(PASSWORD);
        assertTrue(login.login().success());
        assertTrue(SessionManager.get().isLoggedIn());
        logoutPg page = new logoutPg();
        LogoutResult result = page.logout();
        assertTrue(result.success());
        assertEquals(Messages.LOGGED_OUT, result.message());
        assertEquals(Messages.LOGGED_OUT, page.getMessage());
        assertFalse(SessionManager.get().isLoggedIn());
        assertEquals(0, SessionManager.get().activeSessionCount());
        assertEquals(1, Database.get().accounts().size());
    }

    @Test
    public void testLogoutTwiceIsHarmless() {
        customer("alice");
        loginPg login = new loginPg();
        login.setUsernameOrEmail("alice");
        login.setPassword(PASSWORD);
        assertTrue(login.login().success());
        assertTrue(new logoutPg().logout().success());
        logoutPg page = new logoutPg();
        LogoutResult result = page.logout();
        assertTrue(result.success());
        assertEquals(Messages.LOGGED_OUT, result.message());
        assertFalse(SessionManager.get().isLoggedIn());
    }

    @Test
    public void testLogoutKeepsTheAccount() {
        UserAccount alice = customer("alice");
        loginPg login = new loginPg();
        login.setUsernameOrEmail("alice");
        login.setPassword(PASSWORD);
        assertTrue(login.login().success());
        new logoutPg().logout();
        assertNotNull(UserAccount.lookup(alice.getAccountID()));
        loginPg again = new loginPg();
        again.setUsernameOrEmail("alice");
        again.setPassword(PASSWORD);
        assertTrue(again.login().success());
    }
}
