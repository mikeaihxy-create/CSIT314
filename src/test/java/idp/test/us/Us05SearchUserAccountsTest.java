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

public class Us05SearchUserAccountsTest extends IdpTestCase {

    @Test
    public void testSearchUserAccounts() {
        signIn(admin());
        customer("alice");
        customer("bob");
        uaSearchAccountPg page = new uaSearchAccountPg();
        page.setKeyword("ali");
        List<UserAccount> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("alice", matches.get(0).getUsername());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchUserAccountsMatchesEmail() {
        signIn(admin());
        customer("alice");
        designer("bob");
        uaSearchAccountPg page = new uaSearchAccountPg();
        page.setKeyword("bob@idp.test");
        List<UserAccount> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("bob", matches.get(0).getUsername());
    }

    @Test
    public void testSearchUserAccountsFailsWhenKeywordIsBlank() {
        signIn(admin());
        customer("alice");
        uaSearchAccountPg page = new uaSearchAccountPg();
        page.setKeyword("");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD, page.getMessage());
    }

    @Test
    public void testSearchUserAccountsFailsWhenNothingMatches() {
        signIn(admin());
        customer("alice");
        uaSearchAccountPg page = new uaSearchAccountPg();
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_ACCOUNTS, page.getMessage());
    }

    @Test
    public void testSearchUserAccountsFiltersByRole() {
        signIn(admin());
        customer("olive");
        designer("oscar");
        uaSearchAccountPg page = new uaSearchAccountPg();
        page.setKeyword("o");
        page.setRole("CUSTOMER");
        List<UserAccount> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("olive", matches.get(0).getUsername());
        assertEquals(Role.CUSTOMER, matches.get(0).getRole());
    }

    @Test
    public void testSearchUserAccountsFailsWhenActorIsNotUserAdmin() {
        customer("alice");
        signIn(designer());
        uaSearchAccountPg page = new uaSearchAccountPg();
        page.setKeyword("alice");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_ACCOUNTS, page.getMessage());
    }
}
