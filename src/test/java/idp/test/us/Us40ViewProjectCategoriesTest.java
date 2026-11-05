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

public class Us40ViewProjectCategoriesTest extends IdpTestCase {

    @Test
    public void testViewCategories() {
        UserAccount manager = manager();
        category("Kitchen");
        category("Bathroom");
        signIn(manager);
        pmViewCategoryPg page = new pmViewCategoryPg();
        List<ProjectCategory> rows = page.view();
        assertEquals(2, rows.size());
        assertEquals("Kitchen", rows.get(0).getName());
        assertEquals("Bathroom", rows.get(1).getName());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewCategoriesFailsWhenListIsEmpty() {
        signIn(manager());
        pmViewCategoryPg page = new pmViewCategoryPg();
        assertTrue(page.view().isEmpty());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewCategoriesFailsWhenActorIsNotPlatformManagement() {
        category("Kitchen");
        signIn(designer());
        pmViewCategoryPg page = new pmViewCategoryPg();
        assertTrue(page.view().isEmpty());
    }
}
