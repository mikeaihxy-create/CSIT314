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

public class Us24SearchCurrentProjectsTest extends IdpTestCase {

    @Test
    public void testSearchCurrentProjects() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        project(designer, category, "Riverside Kitchen");
        project(designer, category, "Harbour Bathroom");
        signIn(designer);
        idSearchCurrentProjectPg page = new idSearchCurrentProjectPg();
        page.setKeyword("kitchen");
        List<DesignProject> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Riverside Kitchen", matches.get(0).getTitle());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchCurrentProjectsFailsWhenKeywordIsBlank() {
        UserAccount designer = designer();
        project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        idSearchCurrentProjectPg page = new idSearchCurrentProjectPg();
        page.setKeyword("  ");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD, page.getMessage());
    }

    @Test
    public void testSearchCurrentProjectsFailsWhenNothingMatches() {
        UserAccount designer = designer();
        project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        idSearchCurrentProjectPg page = new idSearchCurrentProjectPg();
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_CURRENT_PROJECTS, page.getMessage());
    }

    @Test
    public void testSearchCurrentProjectsOnlyReturnsOwnProjects() {
        UserAccount owner = designer("owner");
        UserAccount other = designer("other");
        project(owner, category("Kitchen"), "Riverside Kitchen");
        project(other, category("Kitchen"), "Riverside Annex");
        signIn(other);
        idSearchCurrentProjectPg page = new idSearchCurrentProjectPg();
        page.setKeyword("Riverside");
        List<DesignProject> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Riverside Annex", matches.get(0).getTitle());
    }

    @Test
    public void testSearchCurrentProjectsExcludesPastProjects() {
        UserAccount designer = designer();
        pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idSearchCurrentProjectPg page = new idSearchCurrentProjectPg();
        page.setKeyword("Kitchen");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_CURRENT_PROJECTS, page.getMessage());
    }
}
