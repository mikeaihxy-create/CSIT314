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

public class Us03UpdateUserAccountTest extends IdpTestCase {

    @Test
    public void testUpdateUserAccount() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        profileOf(target, "Target Person");
        signIn(admin);
        uaUpdateAccountPg page = new uaUpdateAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("Renamed Person");
        page.setEmail("renamed@idp.test");
        page.setRole("INTERIOR_DESIGNER");
        UpdateAccountResult result = page.update();
        assertTrue(result.success());
        assertEquals(Messages.ACCOUNT_UPDATED, result.message());
        assertEquals("renamed@idp.test", target.getEmail());
        assertEquals(Role.INTERIOR_DESIGNER, target.getRole());
        UserProfile profile = UserProfile.ofAccount(target.getAccountID());
        assertNotNull(profile);
        assertEquals("Renamed Person", profile.getFullName());
    }

    @Test
    public void testUpdateUserAccountFailsWhenEmailBelongsToAnotherAccount() {
        UserAccount admin = admin();
        UserAccount first = customer("first");
        UserAccount second = customer("second");
        signIn(admin);
        uaUpdateAccountPg page = new uaUpdateAccountPg();
        page.setAccountID(String.valueOf(second.getAccountID()));
        page.setFullName("Second Person");
        page.setEmail("first@idp.test");
        page.setRole("CUSTOMER");
        UpdateAccountResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.EMAIL_USED, result.message());
        assertEquals("second@idp.test", second.getEmail());
        assertEquals("first@idp.test", first.getEmail());
    }

    @Test
    public void testUpdateUserAccountFailsWhenNameIsBlank() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        signIn(admin);
        uaUpdateAccountPg page = new uaUpdateAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("   ");
        page.setEmail("target@idp.test");
        page.setRole("CUSTOMER");
        UpdateAccountResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_CHANGES, result.message());
    }

    @Test
    public void testUpdateUserAccountFailsWhenRoleIsUnknown() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        signIn(admin);
        uaUpdateAccountPg page = new uaUpdateAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("Target Person");
        page.setEmail("target@idp.test");
        page.setRole("GHOST");
        UpdateAccountResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CHOOSE_ROLE, result.message());
    }

    @Test
    public void testUpdateUserAccountFailsWhenAccountDoesNotExist() {
        signIn(admin());
        uaUpdateAccountPg page = new uaUpdateAccountPg();
        page.setAccountID("9999");
        page.setFullName("Nobody");
        page.setEmail("nobody@idp.test");
        page.setRole("CUSTOMER");
        UpdateAccountResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.ACCOUNT_NOT_FOUND, result.message());
    }

    @Test
    public void testUpdateUserAccountFailsWhenActorIsNotUserAdmin() {
        UserAccount target = customer("target");
        signIn(designer());
        uaUpdateAccountPg page = new uaUpdateAccountPg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("Hacked");
        page.setEmail("hacked@idp.test");
        page.setRole("CUSTOMER");
        UpdateAccountResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals("target@idp.test", target.getEmail());
    }
}
