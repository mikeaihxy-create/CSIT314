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

public class Us04SuspendUserAccountTest extends IdpTestCase {

    @Test
    public void testSuspendUserAccount() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        signIn(target);
        assertTrue(SessionManager.get().hasSessionFor(target.getAccountID()));
        signIn(admin);
        uaSuspendAccountPg page = new uaSuspendAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        SuspendAccountResult result = page.suspend();
        assertTrue(result.success());
        assertEquals(Messages.ACCOUNT_SUSPENDED, result.message());
        assertEquals(AccountStatus.SUSPENDED, target.getStatus());
        assertFalse(SessionManager.get().hasSessionFor(target.getAccountID()));
        loginPg login = new loginPg();
        login.setUsernameOrEmail("target");
        login.setPassword(PASSWORD);
        LoginResult attempt = login.login();
        assertFalse(attempt.success());
        assertEquals(Messages.ACCOUNT_NOT_ACTIVE, attempt.message());
        assertNull(attempt.sessionId());
        assertFalse(SessionManager.get().hasSessionFor(target.getAccountID()));
    }

    @Test
    public void testSuspendUserAccountFailsWhenAlreadySuspended() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        signIn(admin);
        uaSuspendAccountPg first = new uaSuspendAccountPg();
        first.setAccountID(String.valueOf(target.getAccountID()));
        assertTrue(first.suspend().success());
        uaSuspendAccountPg second = new uaSuspendAccountPg();
        second.setAccountID(String.valueOf(target.getAccountID()));
        SuspendAccountResult result = second.suspend();
        assertFalse(result.success());
        assertEquals(Messages.ACCOUNT_ALREADY_SUSPENDED, result.message());
    }

    @Test
    public void testSuspendUserAccountFailsWhenAccountDoesNotExist() {
        signIn(admin());
        uaSuspendAccountPg page = new uaSuspendAccountPg();
        page.setAccountID("9999");
        SuspendAccountResult result = page.suspend();
        assertFalse(result.success());
        assertEquals(Messages.ACCOUNT_NOT_FOUND, result.message());
    }

    @Test
    public void testSuspendUserAccountFailsWhenActorIsNotUserAdmin() {
        UserAccount target = customer("target");
        signIn(customer("intruder"));
        uaSuspendAccountPg page = new uaSuspendAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        SuspendAccountResult result = page.suspend();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(AccountStatus.ACTIVE, target.getStatus());
    }
}
