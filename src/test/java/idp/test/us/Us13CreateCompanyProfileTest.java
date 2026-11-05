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

public class Us13CreateCompanyProfileTest extends IdpTestCase {

    @Test
    public void testCreateCompanyProfile() {
        UserAccount designer = designer();
        signIn(designer);
        idCreateCompanyPg page = new idCreateCompanyPg();
        page.setName("Studio North");
        page.setDescription("Modern homes");
        page.setLocation("Singapore");
        page.setContactEmail("hello@studionorth.test");
        page.setPhone("61234567");
        page.setLogoUrl("logo.png");
        CreateCompanyResult result = page.create();
        assertTrue(result.success());
        assertEquals(Messages.COMPANY_CREATED, result.message());
        assertNotNull(result.company());
        assertEquals(designer.getAccountID(), result.company().getOwnerAccountID());
        assertEquals("Studio North", result.company().getName());
        assertEquals("Singapore", result.company().getLocation());
        assertEquals(CompanyStatus.ACTIVE, result.company().getStatus());
        assertEquals(1, Database.get().companies().size());
    }

    @Test
    public void testCreateCompanyProfileFailsWhenDesignerAlreadyHasOne() {
        UserAccount designer = designer();
        signIn(designer);
        idCreateCompanyPg first = new idCreateCompanyPg();
        first.setName("Studio North");
        first.setDescription("Modern homes");
        first.setLocation("Singapore");
        assertTrue(first.create().success());
        idCreateCompanyPg second = new idCreateCompanyPg();
        second.setName("Studio South");
        second.setDescription("Second studio");
        second.setLocation("Singapore");
        CreateCompanyResult result = second.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
        assertEquals(1, Database.get().companies().size());
    }

    @Test
    public void testCreateCompanyProfileFailsWhenNameIsTaken() {
        UserAccount first = designer("first");
        UserAccount second = designer("second");
        companyOf(first, "Studio North");
        signIn(second);
        idCreateCompanyPg page = new idCreateCompanyPg();
        page.setName("Studio North");
        page.setDescription("Copied name");
        page.setLocation("Singapore");
        CreateCompanyResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.COMPANY_NAME_USED, result.message());
        assertEquals(1, Database.get().companies().size());
    }

    @Test
    public void testCreateCompanyProfileFailsWhenActorIsNotDesigner() {
        signIn(customer("alice"));
        idCreateCompanyPg page = new idCreateCompanyPg();
        page.setName("Studio North");
        page.setDescription("Modern homes");
        page.setLocation("Singapore");
        CreateCompanyResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(0, Database.get().companies().size());
    }

    @Test
    public void testCreateCompanyProfileFailsWhenDetailsAreMissing() {
        signIn(designer());
        idCreateCompanyPg page = new idCreateCompanyPg();
        page.setName("   ");
        page.setDescription("Modern homes");
        page.setLocation("Singapore");
        CreateCompanyResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
    }
}
