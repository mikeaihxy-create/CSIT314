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

public class Us41UpdateProjectCategoryTest extends IdpTestCase {

    @Test
    public void testUpdateCategory() {
        UserAccount manager = manager();
        ProjectCategory kitchen = category("Kitchen");
        signIn(manager);
        pmUpdateCategoryPg page = new pmUpdateCategoryPg();
        page.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        page.setName("Kitchen and Dining");
        page.setDescription("Kitchen and dining projects");
        UpdateCategoryResult result = page.update();
        assertTrue(result.success());
        assertEquals(Messages.CATEGORY_UPDATED, result.message());
        assertEquals("Kitchen and Dining", kitchen.getName());
        assertEquals("Kitchen and dining projects", kitchen.getDescription());
    }

    @Test
    public void testUpdateCategoryFailsWhenNameIsTaken() {
        UserAccount manager = manager();
        category("Kitchen");
        ProjectCategory bath = category("Bathroom");
        signIn(manager);
        pmUpdateCategoryPg page = new pmUpdateCategoryPg();
        page.setCategoryID(String.valueOf(bath.getCategoryID()));
        page.setName("Kitchen");
        page.setDescription("Clash");
        UpdateCategoryResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CATEGORY_NAME_USED, result.message());
        assertEquals("Bathroom", bath.getName());
    }

    @Test
    public void testUpdateCategoryFailsWhenItDoesNotExist() {
        signIn(manager());
        pmUpdateCategoryPg page = new pmUpdateCategoryPg();
        page.setCategoryID("9999");
        page.setName("Ghost");
        UpdateCategoryResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CATEGORY_NOT_FOUND, result.message());
    }

    @Test
    public void testUpdateCategoryFailsWhenNameIsBlank() {
        UserAccount manager = manager();
        ProjectCategory kitchen = category("Kitchen");
        signIn(manager);
        pmUpdateCategoryPg page = new pmUpdateCategoryPg();
        page.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        page.setName(" ");
        page.setDescription("No name");
        UpdateCategoryResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_CHANGES, result.message());
    }
}
