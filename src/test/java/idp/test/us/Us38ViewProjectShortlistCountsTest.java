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

public class Us38ViewProjectShortlistCountsTest extends IdpTestCase {

    @Test
    public void testViewProjectShortlists() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject first = project(designer, category, "Riverside Kitchen");
        DesignProject second = project(designer, category, "Harbour Bathroom");
        first.setShortlistCount(2);
        second.setShortlistCount(6);
        signIn(designer);
        idViewProjectShortlistsPg page = new idViewProjectShortlistsPg();
        ProjectShortlistResult result = page.viewProjectShortlists();
        assertTrue(result.success());
        assertEquals(8, result.totalShortlists());
        assertEquals(2, result.projects().size());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewProjectShortlistsWithNoProjects() {
        signIn(designer());
        idViewProjectShortlistsPg page = new idViewProjectShortlistsPg();
        ProjectShortlistResult result = page.viewProjectShortlists();
        assertTrue(result.success());
        assertEquals(0, result.totalShortlists());
        assertEquals(Messages.NO_PROJECT_DATA, page.getMessage());
    }

    @Test
    public void testViewProjectShortlistsFailsWhenActorIsNotDesigner() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        created.setShortlistCount(3);
        signIn(customer("alice"));
        idViewProjectShortlistsPg page = new idViewProjectShortlistsPg();
        ProjectShortlistResult result = page.viewProjectShortlists();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }
}
