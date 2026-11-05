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

public class Us21ViewCurrentProjectTest extends IdpTestCase {

    @Test
    public void testViewCurrentProject() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        idViewCurrentProjectPg page = new idViewCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DesignProject found = page.view();
        assertNotNull(found);
        assertEquals(created.getProjectID(), found.getProjectID());
        assertEquals("Riverside Kitchen", found.getTitle());
        assertEquals(ProjectStage.CURRENT, found.getStage());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewCurrentProjectFailsWhenAnotherDesignerOwnsIt() {
        UserAccount owner = designer("owner");
        DesignProject created = project(owner, category("Kitchen"), "Riverside Kitchen");
        signIn(designer("other"));
        idViewCurrentProjectPg page = new idViewCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        assertNull(page.view());
        assertEquals(Messages.CURRENT_PROJECT_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewCurrentProjectFailsWhenItDoesNotExist() {
        signIn(designer());
        idViewCurrentProjectPg page = new idViewCurrentProjectPg();
        page.setProjectID("9999");
        assertNull(page.view());
        assertEquals(Messages.CURRENT_PROJECT_NOT_FOUND, page.getMessage());
    }

    @Test
    public void testViewCurrentProjectDoesNotReturnPastProject() {
        UserAccount designer = designer();
        DesignProject past = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idViewCurrentProjectPg page = new idViewCurrentProjectPg();
        page.setProjectID(String.valueOf(past.getProjectID()));
        assertNull(page.view());
        assertEquals(Messages.CURRENT_PROJECT_NOT_FOUND, page.getMessage());
    }
}
