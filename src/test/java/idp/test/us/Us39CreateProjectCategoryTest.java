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

public class Us39CreateProjectCategoryTest extends IdpTestCase {

    @Test
    public void testCreateCategory() {
        signIn(manager());
        pmCreateCategoryPg page = new pmCreateCategoryPg();
        page.setName("Kitchen");
        page.setDescription("Kitchen renovation projects");
        CreateCategoryResult result = page.create();
        assertTrue(result.success());
        assertEquals(Messages.CATEGORY_CREATED, result.message());
        assertNotNull(result.category());
        assertEquals("Kitchen", result.category().getName());
        assertEquals(CategoryStatus.ACTIVE, result.category().getStatus());
        assertEquals(1, Database.get().categories().size());
    }

    @Test
    public void testCreateCategoryFailsWhenNameIsTaken() {
        category("Kitchen");
        signIn(manager());
        pmCreateCategoryPg page = new pmCreateCategoryPg();
        page.setName("Kitchen");
        page.setDescription("Duplicate");
        CreateCategoryResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CATEGORY_NAME_USED, result.message());
        assertEquals(1, Database.get().categories().size());
    }

    @Test
    public void testCreateCategoryFailsWhenDetailsAreMissing() {
        signIn(manager());
        pmCreateCategoryPg page = new pmCreateCategoryPg();
        page.setName("   ");
        page.setDescription("No name");
        CreateCategoryResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
    }

    @Test
    public void testCreateCategoryFailsWhenActorIsNotPlatformManagement() {
        signIn(designer());
        pmCreateCategoryPg page = new pmCreateCategoryPg();
        page.setName("Kitchen");
        page.setDescription("Kitchen renovation projects");
        CreateCategoryResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(0, Database.get().categories().size());
    }
}
