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

public class Us27UpdatePastProjectTest extends IdpTestCase {

    @Test
    public void testUpdatePastProject() {
        UserAccount designer = designer();
        DesignProject created = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idUpdatePastProjectPg page = new idUpdatePastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        page.setTitle("Old Kitchen Restored");
        page.setDescription("Restored kitchen");
        page.setStyle("Heritage");
        page.setBudget("21000");
        page.setCategoryID(String.valueOf(created.getCategoryID()));
        page.setCompletionDate("2024-12-31");
        UpdateProjectResult result = page.update();
        assertTrue(result.success());
        assertEquals(Messages.PAST_PROJECT_UPDATED, result.message());
        assertEquals("Old Kitchen Restored", created.getTitle());
        assertEquals(java.time.LocalDate.parse("2024-12-31"), created.getCompletionDate());
    }

    @Test
    public void testUpdatePastProjectFailsWhenItDoesNotExist() {
        signIn(designer());
        idUpdatePastProjectPg page = new idUpdatePastProjectPg();
        page.setProjectID("9999");
        page.setTitle("Ghost");
        UpdateProjectResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.PAST_PROJECT_NOT_FOUND, result.message());
    }

    @Test
    public void testUpdatePastProjectFailsWhenCompletionDateIsMissing() {
        UserAccount designer = designer();
        DesignProject created = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idUpdatePastProjectPg page = new idUpdatePastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        page.setTitle("Old Kitchen");
        page.setDescription("Completed kitchen renovation");
        page.setStyle("Classic");
        page.setBudget("18000");
        page.setCategoryID(String.valueOf(created.getCategoryID()));
        page.setCompletionDate("");
        UpdateProjectResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_CHANGES, result.message());
    }

    @Test
    public void testUpdatePastProjectFailsWhenActorIsNotDesigner() {
        UserAccount designer = designer();
        DesignProject created = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(customer("alice"));
        idUpdatePastProjectPg page = new idUpdatePastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        page.setTitle("Hijacked");
        UpdateProjectResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }
}
