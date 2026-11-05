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

public class Us16DeleteCompanyProfileTest extends IdpTestCase {

    @Test
    public void testDeleteCompanyProfile() {
        UserAccount designer = designer();
        CompanyProfile company = companyOf(designer, "Studio North");
        signIn(designer);
        idDeleteCompanyPg page = new idDeleteCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        DeleteCompanyResult result = page.delete();
        assertTrue(result.success());
        assertEquals(Messages.COMPANY_DELETED, result.message());
        assertEquals(0, Database.get().companies().size());
        assertNull(Database.get().companies().find(company.getCompanyID()));
    }

    @Test
    public void testDeleteCompanyProfileFailsWhenItDoesNotExist() {
        signIn(designer());
        idDeleteCompanyPg page = new idDeleteCompanyPg();
        page.setCompanyID("9999");
        DeleteCompanyResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.COMPANY_NOT_FOUND, result.message());
    }

    @Test
    public void testDeleteCompanyProfileFailsWhenAnotherDesignerOwnsIt() {
        UserAccount owner = designer("owner");
        UserAccount other = designer("other");
        CompanyProfile company = companyOf(owner, "Studio North");
        signIn(other);
        idDeleteCompanyPg page = new idDeleteCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        DeleteCompanyResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.COMPANY_NOT_FOUND, result.message());
        assertEquals(1, Database.get().companies().size());
    }

    @Test
    public void testDeleteCompanyProfileFailsWhenActorIsNotDesigner() {
        UserAccount owner = designer("owner");
        CompanyProfile company = companyOf(owner, "Studio North");
        signIn(customer("alice"));
        idDeleteCompanyPg page = new idDeleteCompanyPg();
        page.setCompanyID(String.valueOf(company.getCompanyID()));
        DeleteCompanyResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(1, Database.get().companies().size());
    }
}
