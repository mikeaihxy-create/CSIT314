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

public class Us26ViewPastProjectTest extends IdpTestCase {

    @Test
    public void testViewPastProject() {
        UserAccount designer = designer();
        DesignProject created = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idViewPastProjectPg page = new idViewPastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DesignProject found = page.view();
        assertNotNull(found);
        assertEquals(created.getProjectID(), found.getProjectID());
        assertEquals(ProjectStage.PAST, found.getStage());
        assertNotNull(found.getCompletionDate());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewPastProjectFailsWhenItDoesNotExist() {
        signIn(designer());
        idViewPastProjectPg page = new idViewPastProjectPg();
        page.setProjectID("9999");
        assertNull(page.view());
        assertEquals(Messages.PAST_PROJECT_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewPastProjectDoesNotReturnCurrentProject() {
        UserAccount designer = designer();
        DesignProject current = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        idViewPastProjectPg page = new idViewPastProjectPg();
        page.setProjectID(String.valueOf(current.getProjectID()));
        assertNull(page.view());
        assertEquals(Messages.PAST_PROJECT_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewPastProjectFailsWhenAnotherDesignerOwnsIt() {
        UserAccount owner = designer("owner");
        DesignProject created = pastProject(owner, category("Kitchen"), "Old Kitchen");
        signIn(designer("other"));
        idViewPastProjectPg page = new idViewPastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        assertNull(page.view());
        assertEquals(Messages.PAST_PROJECT_NOT_FOUND, page.getMessage());
    }
}
