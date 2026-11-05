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

public class Us15UpdateCompanyProfileTest extends IdpTestCase {

    @Test
    public void testUpdateCompanyProfile() {
        UserAccount designer = designer();
        CompanyProfile company = companyOf(designer, "Studio North");
        signIn(designer);
        idUpdateCompanyPg page = new idUpdateCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        page.setName("Studio North Design");
        page.setDescription("Award winning studio");
        page.setLocation("Tanjong Pagar");
        page.setContactEmail("studio@north.test");
        page.setPhone("61234567");
        page.setLogoUrl("newlogo.png");
        UpdateCompanyResult result = page.update();
        assertTrue(result.success());
        assertEquals(Messages.COMPANY_UPDATED, result.message());
        assertEquals("Studio North Design", company.getName());
        assertEquals("Award winning studio", company.getDescription());
        assertEquals("Tanjong Pagar", company.getLocation());
    }

    @Test
    public void testUpdateCompanyProfileFailsWhenNameIsTaken() {
        UserAccount first = designer("first");
        UserAccount second = designer("second");
        companyOf(first, "Studio North");
        CompanyProfile other = companyOf(second, "Studio South");
        signIn(second);
        idUpdateCompanyPg page = new idUpdateCompanyPg();
        page.setCompanyID(String.valueOf(other.getCompanyID()));
        page.setName("Studio North");
        page.setDescription("Renamed");
        page.setLocation("Singapore");
        UpdateCompanyResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.COMPANY_NAME_USED, result.message());
        assertEquals("Studio South", other.getName());
    }

    @Test
    public void testUpdateCompanyProfileFailsWhenItDoesNotExist() {
        signIn(designer());
        idUpdateCompanyPg page = new idUpdateCompanyPg();
        page.setCompanyID("9999");
        page.setName("Ghost Studio");
        UpdateCompanyResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.COMPANY_NOT_FOUND, result.message());
    }

    @Test
    public void testUpdateCompanyProfileFailsWhenActorIsNotDesigner() {
        UserAccount owner = designer("owner");
        CompanyProfile company = companyOf(owner, "Studio North");
        signIn(customer("alice"));
        idUpdateCompanyPg page = new idUpdateCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        page.setName("Hijacked");
        UpdateCompanyResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals("Studio North", company.getName());
    }
}
