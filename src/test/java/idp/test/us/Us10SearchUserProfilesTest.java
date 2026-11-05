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

public class Us10SearchUserProfilesTest extends IdpTestCase {

    @Test
    public void testSearchUserProfiles() {
        signIn(admin());
        UserAccount alice = customer("alice");
        UserAccount bob = customer("bob");
        profileOf(alice, "Alice Tan");
        profileOf(bob, "Bob Lee");
        uaSearchProfilePg page = new uaSearchProfilePg();
        page.setKeyword("alice");
        List<UserProfile> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Alice Tan", matches.get(0).getFullName());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchUserProfilesFailsWhenKeywordIsBlank() {
        signIn(admin());
        profileOf(customer("alice"), "Alice Tan");
        uaSearchProfilePg page = new uaSearchProfilePg();
        page.setKeyword(" ");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD, page.getMessage());
    }

    @Test
    public void testSearchUserProfilesFailsWhenNothingMatches() {
        signIn(admin());
        profileOf(customer("alice"), "Alice Tan");
        uaSearchProfilePg page = new uaSearchProfilePg();
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_PROFILES, page.getMessage());
    }

    @Test
    public void testSearchUserProfilesFiltersSuspended() {
        signIn(admin());
        UserAccount alice = customer("alice");
        UserAccount bob = customer("bob");
        profileOf(alice, "Alice Tan");
        UserProfile suspended = profileOf(bob, "Bob Lee");
        suspended.setStatus(AccountStatus.SUSPENDED);
        uaSearchProfilePg page = new uaSearchProfilePg();
        page.setKeyword("Tan");
        page.setStatus("ACTIVE");
        List<UserProfile> active = page.search();
        assertEquals(1, active.size());
        assertEquals("Alice Tan", active.get(0).getFullName());
        uaSearchProfilePg other = new uaSearchProfilePg();
        other.setKeyword("Lee");
        other.setStatus("SUSPENDED");
        List<UserProfile> paused = other.search();
        assertEquals(1, paused.size());
        assertEquals("Bob Lee", paused.get(0).getFullName());
    }

    @Test
    public void testSearchUserProfilesFailsWhenActorIsNotUserAdmin() {
        profileOf(customer("alice"), "Alice Tan");
        signIn(designer());
        uaSearchProfilePg page = new uaSearchProfilePg();
        page.setKeyword("Alice");
        assertTrue(page.search().isEmpty());
    }
}
