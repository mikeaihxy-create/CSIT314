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

public class Us08UpdateUserProfileTest extends IdpTestCase {

    @Test
    public void testUpdateUserProfile() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(admin);
        uaUpdateProfilePg page = new uaUpdateProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        page.setFullName("Updated Person");
        page.setPhone("98765432");
        page.setAddress("99 Bukit Timah Road");
        UpdateProfileResult result = page.update();
        assertTrue(result.success());
        assertEquals(Messages.PROFILE_UPDATED, result.message());
        assertEquals("Updated Person", profile.getFullName());
        assertEquals("98765432", profile.getPhone());
        assertEquals("99 Bukit Timah Road", profile.getAddress());
    }

    @Test
    public void testUpdateUserProfileFailsWhenProfileIsSuspended() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        profile.setStatus(AccountStatus.SUSPENDED);
        signIn(admin);
        uaUpdateProfilePg page = new uaUpdateProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        page.setFullName("Updated Person");
        UpdateProfileResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.PROFILE_IS_SUSPENDED, result.message());
        assertEquals("Target Person", profile.getFullName());
    }

    @Test
    public void testUpdateUserProfileFailsWhenProfileDoesNotExist() {
        signIn(admin());
        uaUpdateProfilePg page = new uaUpdateProfilePg();
        page.setProfileID("9999");
        page.setFullName("Nobody");
        UpdateProfileResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.PROFILE_NOT_FOUND, result.message());
    }

    @Test
    public void testUpdateUserProfileFailsWhenFullNameIsBlank() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(admin);
        uaUpdateProfilePg page = new uaUpdateProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        page.setFullName("");
        UpdateProfileResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_CHANGES, result.message());
    }
}
