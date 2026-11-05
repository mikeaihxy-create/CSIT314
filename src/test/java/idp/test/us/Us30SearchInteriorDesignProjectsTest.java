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

public class Us30SearchInteriorDesignProjectsTest extends IdpTestCase {

    @Test
    public void testSearchProjectsByKeyword() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject created = project(designer, category, "Riverside Kitchen");
        signIn(customer("alice"));
        cSearchProjectPg page = new cSearchProjectPg();
        page.setKeyword("Riverside");
        List<DesignProject> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals(created.getProjectID(), matches.get(0).getProjectID());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchProjectsByStyle() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        project(designer, category, "Riverside Kitchen");
        project(designer, category, "Harbour Bathroom");
        signIn(customer("alice"));
        cSearchProjectPg page = new cSearchProjectPg();
        page.setStyle("Modern");
        assertEquals(2, page.search().size());
        cSearchProjectPg unmatched = new cSearchProjectPg();
        unmatched.setStyle("Industrial");
        assertTrue(unmatched.search().isEmpty());
        assertEquals(Messages.NO_PROJECTS, unmatched.getMessage());
    }

    @Test
    public void testSearchProjectsByBudgetRange() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        project(designer, category, "Riverside Kitchen");
        signIn(customer("alice"));
        cSearchProjectPg inRange = new cSearchProjectPg();
        inRange.setBudget("10000-30000");
        assertEquals(1, inRange.search().size());
        cSearchProjectPg tooCheap = new cSearchProjectPg();
        tooCheap.setBudget("1000-5000");
        assertTrue(tooCheap.search().isEmpty());
        cSearchProjectPg atMost = new cSearchProjectPg();
        atMost.setBudget("25000");
        assertEquals(1, atMost.search().size());
    }

    @Test
    public void testSearchProjectsFailsWhenNoKeywordOrFilterIsGiven() {
        UserAccount designer = designer();
        project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(customer("alice"));
        cSearchProjectPg page = new cSearchProjectPg();
        page.setKeyword("");
        page.setStyle("  ");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD_OR_FILTER, page.getMessage());
    }

    @Test
    public void testSearchProjectsFailsWhenNothingMatches() {
        UserAccount designer = designer();
        project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(customer("alice"));
        cSearchProjectPg page = new cSearchProjectPg();
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_PROJECTS, page.getMessage());
    }

    @Test
    public void testSearchProjectsExcludesPastProjects() {
        UserAccount designer = designer();
        pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(customer("alice"));
        cSearchProjectPg page = new cSearchProjectPg();
        page.setKeyword("Kitchen");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_PROJECTS, page.getMessage());
    }

    @Test
    public void testSearchProjectsFailsWhenActorIsNotCustomer() {
        UserAccount designer = designer();
        project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        cSearchProjectPg page = new cSearchProjectPg();
        page.setKeyword("Riverside");
        assertTrue(page.search().isEmpty());
    }
}
