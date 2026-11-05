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

public class Us31ViewInteriorDesignProjectTest extends IdpTestCase {

    @Test
    public void testViewProjectIncrementsViewCount() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(customer("alice"));
        cViewProjectPg page = new cViewProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DesignProject found = page.view();
        assertNotNull(found);
        assertEquals("Riverside Kitchen", found.getTitle());
        assertEquals(1, created.getViewCount());
        assertEquals("", page.getMessage());
        cViewProjectPg again = new cViewProjectPg();
        again.setProjectID(String.valueOf(created.getProjectID()));
        assertNotNull(again.view());
        assertEquals(2, created.getViewCount());
        int views = 0;
        for (ActivityEvent event : Database.get().events().all()) {
            if (event.getEventType() == EventType.PROJECT_VIEW) {
                views++;
            }
        }
        assertEquals(2, views);
    }

    @Test
    public void testViewProjectFailsWhenItDoesNotExist() {
        signIn(customer("alice"));
        cViewProjectPg page = new cViewProjectPg();
        page.setProjectID("9999");
        assertNull(page.view());
        assertEquals(Messages.PROJECT_NOT_AVAILABLE, page.getMessage());
    }

    @Test
    public void testViewPastProjectIsNotAvailable() {
        UserAccount designer = designer();
        DesignProject past = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(customer("alice"));
        cViewProjectPg page = new cViewProjectPg();
        page.setProjectID(String.valueOf(past.getProjectID()));
        assertNull(page.view());
        assertEquals(Messages.PROJECT_NOT_AVAILABLE, page.getMessage());
        assertEquals(0, past.getViewCount());
    }

    @Test
    public void testViewProjectFailsWhenActorIsNotCustomer() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        cViewProjectPg page = new cViewProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        assertNull(page.view());
        assertEquals(Messages.PROJECT_NOT_AVAILABLE, page.getMessage());
        assertEquals(0, created.getViewCount());
    }
}
