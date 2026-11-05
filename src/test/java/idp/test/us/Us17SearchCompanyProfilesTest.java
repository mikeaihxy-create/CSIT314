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

public class Us17SearchCompanyProfilesTest extends IdpTestCase {

    @Test
    public void testSearchCompanyProfiles() {
        UserAccount first = designer("first");
        UserAccount second = designer("second");
        companyOf(first, "Studio North");
        companyOf(second, "Cozy Homes");
        signIn(first);
        idSearchCompanyPg page = new idSearchCompanyPg();
        page.setKeyword("north");
        List<CompanyProfile> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Studio North", matches.get(0).getName());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchCompanyProfilesFailsWhenKeywordIsBlank() {
        companyOf(designer("first"), "Studio North");
        signIn(designer("first"));
        idSearchCompanyPg page = new idSearchCompanyPg();
        page.setKeyword("");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD, page.getMessage());
    }

    @Test
    public void testSearchCompanyProfilesFailsWhenNothingMatches() {
        companyOf(designer("first"), "Studio North");
        signIn(designer("first"));
        idSearchCompanyPg page = new idSearchCompanyPg();
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_COMPANIES, page.getMessage());
    }

    @Test
    public void testSearchCompanyProfilesFailsWhenActorIsNotDesigner() {
        companyOf(designer("first"), "Studio North");
        signIn(customer("alice"));
        idSearchCompanyPg page = new idSearchCompanyPg();
        page.setKeyword("Studio");
        assertTrue(page.search().isEmpty());
    }
}
