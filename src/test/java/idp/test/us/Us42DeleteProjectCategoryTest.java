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

public class Us42DeleteProjectCategoryTest extends IdpTestCase {

    @Test
    public void testDeleteCategory() {
        UserAccount manager = manager();
        ProjectCategory kitchen = category("Kitchen");
        signIn(manager);
        pmDeleteCategoryPg page = new pmDeleteCategoryPg();
        page.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        DeleteCategoryResult result = page.delete();
        assertTrue(result.success());
        assertEquals(Messages.CATEGORY_DELETED, result.message());
        assertEquals(0, Database.get().categories().size());
    }

    @Test
    public void testDeleteCategoryFailsWhenItIsInUse() {
        UserAccount manager = manager();
        ProjectCategory kitchen = category("Kitchen");
        project(designer(), kitchen, "Riverside Kitchen");
        signIn(manager);
        pmDeleteCategoryPg page = new pmDeleteCategoryPg();
        page.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        DeleteCategoryResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.CATEGORY_IN_USE, result.message());
        assertEquals(1, Database.get().categories().size());
    }

    @Test
    public void testDeleteCategoryFailsWhenItDoesNotExist() {
        signIn(manager());
        pmDeleteCategoryPg page = new pmDeleteCategoryPg();
        page.setCategoryID("9999");
        DeleteCategoryResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.CATEGORY_NOT_FOUND, result.message());
    }

    @Test
    public void testDeleteCategoryFailsWhenActorIsNotPlatformManagement() {
        ProjectCategory kitchen = category("Kitchen");
        signIn(designer());
        pmDeleteCategoryPg page = new pmDeleteCategoryPg();
        page.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        DeleteCategoryResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(1, Database.get().categories().size());
    }
}
