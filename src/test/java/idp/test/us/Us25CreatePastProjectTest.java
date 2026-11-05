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

public class Us25CreatePastProjectTest extends IdpTestCase {

    @Test
    public void testCreatePastProject() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        signIn(designer);
        idCreatePastProjectPg page = new idCreatePastProjectPg();
        page.setTitle("Old Kitchen");
        page.setDescription("Completed kitchen renovation");
        page.setStyle("Classic");
        page.setBudget("18000");
        page.setCategoryID(String.valueOf(category.getCategoryID()));
        page.setCompletionDate("2025-06-30");
        page.setImageUrls("before.png,after.png");
        CreateProjectResult result = page.create();
        assertTrue(result.success());
        assertEquals(Messages.PAST_PROJECT_CREATED, result.message());
        DesignProject created = result.project();
        assertNotNull(created);
        assertEquals(ProjectStage.PAST, created.getStage());
        assertEquals(java.time.LocalDate.parse("2025-06-30"), created.getCompletionDate());
        assertEquals(designer.getAccountID(), created.getOwnerAccountID());
        assertFalse(created.isCurrent());
    }

    @Test
    public void testCreatePastProjectFailsWhenCompletionDateIsMissing() {
        signIn(designer());
        idCreatePastProjectPg page = new idCreatePastProjectPg();
        page.setTitle("Old Kitchen");
        page.setDescription("Completed kitchen renovation");
        page.setStyle("Classic");
        page.setBudget("18000");
        page.setCategoryID(String.valueOf(category("Kitchen").getCategoryID()));
        page.setCompletionDate("");
        CreateProjectResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
        assertEquals(0, Database.get().projects().size());
    }

    @Test
    public void testCreatePastProjectFailsWhenCategoryIsMissing() {
        signIn(designer());
        idCreatePastProjectPg page = new idCreatePastProjectPg();
        page.setTitle("Old Kitchen");
        page.setDescription("Completed kitchen renovation");
        page.setStyle("Classic");
        page.setBudget("18000");
        page.setCategoryID("9999");
        page.setCompletionDate("2025-06-30");
        CreateProjectResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHOOSE_CATEGORY, result.message());
    }

    @Test
    public void testCreatePastProjectFailsWhenActorIsNotDesigner() {
        signIn(customer("alice"));
        idCreatePastProjectPg page = new idCreatePastProjectPg();
        page.setTitle("Old Kitchen");
        page.setDescription("Completed kitchen renovation");
        page.setStyle("Classic");
        page.setBudget("18000");
        page.setCategoryID(String.valueOf(category("Kitchen").getCategoryID()));
        page.setCompletionDate("2025-06-30");
        CreateProjectResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }
}
