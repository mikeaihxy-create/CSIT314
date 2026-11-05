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

public class Us07ViewUserProfileTest extends IdpTestCase {

    @Test
    public void testViewUserProfile() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(admin);
        uaViewProfilePg page = new uaViewProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        UserProfile found = page.view();
        assertNotNull(found);
        assertEquals(profile.getProfileID(), found.getProfileID());
        assertEquals("Target Person", found.getFullName());
        assertEquals("91234567", found.getPhone());
        assertEquals("12 Orchard Road", found.getAddress());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewUserProfileShowsSuspendedProfile() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(admin);
        uaSuspendProfilePg suspend = new uaSuspendProfilePg();
        suspend.setProfileID(String.valueOf(profile.getProfileID()));
        assertTrue(suspend.suspend().success());
        uaViewProfilePg page = new uaViewProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        UserProfile found = page.view();
        assertNotNull(found);
        assertEquals(AccountStatus.SUSPENDED, found.getStatus());
        assertEquals(Messages.PROFILE_IS_SUSPENDED, page.getMessage());
    }

    @Test
    public void testViewUserProfileFailsWhenProfileDoesNotExist() {
        signIn(admin());
        uaViewProfilePg page = new uaViewProfilePg();
        page.setProfileID("9999");
        assertNull(page.view());
        assertEquals(Messages.PROFILE_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewUserProfileFailsWhenActorIsNotUserAdmin() {
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(customer("intruder"));
        uaViewProfilePg page = new uaViewProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        assertNull(page.view());
        assertEquals(Messages.PROFILE_NOT_FOUND, page.getMessage());
    }
}
