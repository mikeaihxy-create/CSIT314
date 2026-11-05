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

public class Us02ViewUserAccountTest extends IdpTestCase {

    @Test
    public void testViewUserAccount() {
        signIn(admin());
        UserAccount target = customer("target");
        uaViewAccountPg page = new uaViewAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        UserAccount found = page.view();
        assertNotNull(found);
        assertEquals(target.getAccountID(), found.getAccountID());
        assertEquals("target", found.getUsername());
        assertEquals("target@idp.test", found.getEmail());
        assertEquals(Role.CUSTOMER, found.getRole());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewUserAccountFailsWhenAccountDoesNotExist() {
        signIn(admin());
        uaViewAccountPg page = new uaViewAccountPg();
        page.setAccountID("9999");
        assertNull(page.view());
        assertEquals(Messages.ACCOUNT_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewUserAccountFailsWhenActorIsNotUserAdmin() {
        UserAccount target = customer("target");
        signIn(customer("intruder"));
        uaViewAccountPg page = new uaViewAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        assertNull(page.view());
        assertEquals(Messages.ACCOUNT_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewUserAccountFailsWhenIdIsNotANumber() {
        signIn(admin());
        uaViewAccountPg page = new uaViewAccountPg();
        page.setAccountID("abc");
        assertNull(page.view());
        assertEquals(Messages.ACCOUNT_NOT_FOUND, page.getMessage());
    }
}
