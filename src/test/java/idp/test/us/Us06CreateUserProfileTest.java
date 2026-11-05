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

public class Us06CreateUserProfileTest extends IdpTestCase {

    @Test
    public void testCreateUserProfile() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        signIn(admin);
        uaCreateProfilePg page = new uaCreateProfilePg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("Target Person");
        page.setPhone("91234567");
        page.setAddress("12 Orchard Road");
        CreateProfileResult result = page.create();
        assertTrue(result.success());
        assertEquals(Messages.PROFILE_CREATED, result.message());
        assertNotNull(result.profile());
        assertEquals(target.getAccountID(), result.profile().getAccountID());
        assertEquals("Target Person", result.profile().getFullName());
        assertEquals(AccountStatus.ACTIVE, result.profile().getStatus());
        assertEquals(1, Database.get().profiles().size());
    }

    @Test
    public void testCreateUserProfileFailsWhenAccountAlreadyHasOne() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        profileOf(target, "Target Person");
        signIn(admin);
        uaCreateProfilePg page = new uaCreateProfilePg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("Second Profile");
        CreateProfileResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
        assertEquals(1, Database.get().profiles().size());
    }

    @Test
    public void testCreateUserProfileFailsWhenAccountDoesNotExist() {
        signIn(admin());
        uaCreateProfilePg page = new uaCreateProfilePg();
        page.setAccountID("9999");
        page.setFullName("Ghost");
        CreateProfileResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
    }

    @Test
    public void testCreateUserProfileFailsWhenFullNameIsBlank() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        signIn(admin);
        uaCreateProfilePg page = new uaCreateProfilePg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("  ");
        CreateProfileResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
    }

    @Test
    public void testCreateUserProfileFailsWhenActorIsNotUserAdmin() {
        UserAccount target = customer("target");
        signIn(designer());
        uaCreateProfilePg page = new uaCreateProfilePg();
        page.setAccountID(String.valueOf(target.getAccountID()));
        page.setFullName("Target Person");
        CreateProfileResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }
}
