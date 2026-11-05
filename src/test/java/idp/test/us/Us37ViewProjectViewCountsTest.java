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

public class Us37ViewProjectViewCountsTest extends IdpTestCase {

    @Test
    public void testViewProjectViews() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject first = project(designer, category, "Riverside Kitchen");
        DesignProject second = project(designer, category, "Harbour Bathroom");
        first.setViewCount(3);
        second.setViewCount(5);
        signIn(designer);
        idViewProjectViewsPg page = new idViewProjectViewsPg();
        ProjectViewResult result = page.viewProjectViews();
        assertTrue(result.success());
        assertEquals(8, result.totalViews());
        assertEquals(2, result.projects().size());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewProjectViewsWithNoProjects() {
        signIn(designer());
        idViewProjectViewsPg page = new idViewProjectViewsPg();
        ProjectViewResult result = page.viewProjectViews();
        assertTrue(result.success());
        assertEquals(0, result.totalViews());
        assertEquals(Messages.NO_PROJECT_DATA, page.getMessage());
    }

    @Test
    public void testViewProjectViewsFailsWhenActorIsNotDesigner() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject created = project(designer, category, "Riverside Kitchen");
        created.setViewCount(4);
        signIn(customer("alice"));
        idViewProjectViewsPg page = new idViewProjectViewsPg();
        ProjectViewResult result = page.viewProjectViews();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }
}
