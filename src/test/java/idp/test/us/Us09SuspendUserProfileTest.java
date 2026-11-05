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

public class Us09SuspendUserProfileTest extends IdpTestCase {

    @Test
    public void testSuspendUserProfileLeavesAccountActive() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(admin);
        uaSuspendProfilePg page = new uaSuspendProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        SuspendProfileResult result = page.suspend();
        assertTrue(result.success());
        assertEquals(Messages.PROFILE_SUSPENDED, result.message());
        assertEquals(AccountStatus.SUSPENDED, profile.getStatus());
        assertEquals(AccountStatus.ACTIVE, target.getStatus());
        loginPg login = new loginPg();
        login.setUsernameOrEmail("target");
        login.setPassword(PASSWORD);
        assertTrue(login.login().success());
    }

    @Test
    public void testSuspendUserProfileFailsWhenAlreadySuspended() {
        UserAccount admin = admin();
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(admin);
        uaSuspendProfilePg first = new uaSuspendProfilePg();
        first.setProfileID(String.valueOf(profile.getProfileID()));
        assertTrue(first.suspend().success());
        uaSuspendProfilePg second = new uaSuspendProfilePg();
        second.setProfileID(String.valueOf(profile.getProfileID()));
        SuspendProfileResult result = second.suspend();
        assertFalse(result.success());
        assertEquals(Messages.PROFILE_ALREADY_SUSPENDED, result.message());
    }

    @Test
    public void testSuspendUserProfileFailsWhenProfileDoesNotExist() {
        signIn(admin());
        uaSuspendProfilePg page = new uaSuspendProfilePg();
        page.setProfileID("9999");
        SuspendProfileResult result = page.suspend();
        assertFalse(result.success());
        assertEquals(Messages.PROFILE_NOT_FOUND, result.message());
    }

    @Test
    public void testSuspendUserProfileFailsWhenActorIsNotUserAdmin() {
        UserAccount target = customer("target");
        UserProfile profile = profileOf(target, "Target Person");
        signIn(designer());
        uaSuspendProfilePg page = new uaSuspendProfilePg();
        page.setProfileID(String.valueOf(profile.getProfileID()));
        SuspendProfileResult result = page.suspend();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(AccountStatus.ACTIVE, profile.getStatus());
    }
}
