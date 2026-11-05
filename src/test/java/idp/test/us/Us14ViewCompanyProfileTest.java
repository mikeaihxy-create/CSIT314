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

public class Us14ViewCompanyProfileTest extends IdpTestCase {

    @Test
    public void testViewCompanyProfile() {
        UserAccount designer = designer();
        CompanyProfile company = companyOf(designer, "Studio North");
        signIn(designer);
        idViewCompanyPg page = new idViewCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        CompanyProfile found = page.view();
        assertNotNull(found);
        assertEquals(company.getCompanyID(), found.getCompanyID());
        assertEquals("Studio North", found.getName());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewCompanyProfileFailsWhenItBelongsToAnotherDesigner() {
        UserAccount owner = designer("owner");
        UserAccount other = designer("other");
        CompanyProfile company = companyOf(owner, "Studio North");
        signIn(other);
        idViewCompanyPg page = new idViewCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        assertNull(page.view());
        assertEquals(Messages.COMPANY_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewCompanyProfileFailsWhenItDoesNotExist() {
        signIn(designer());
        idViewCompanyPg page = new idViewCompanyPg();
        page.setCompanyID("9999");
        assertNull(page.view());
        assertEquals(Messages.COMPANY_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewCompanyProfileFailsWhenActorIsNotDesigner() {
        UserAccount owner = designer("owner");
        CompanyProfile company = companyOf(owner, "Studio North");
        signIn(customer("alice"));
        idViewCompanyPg page = new idViewCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        assertNull(page.view());
        assertEquals(Messages.COMPANY_NOT_FOUND, page.getMessage());
    }
}
