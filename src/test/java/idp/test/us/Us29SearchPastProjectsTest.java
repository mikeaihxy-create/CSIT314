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

public class Us29SearchPastProjectsTest extends IdpTestCase {

    @Test
    public void testSearchPastProjects() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        pastProject(designer, category, "Old Kitchen");
        pastProject(designer, category, "Old Bathroom");
        signIn(designer);
        idSearchPastProjectPg page = new idSearchPastProjectPg();
        page.setKeyword("kitchen");
        List<DesignProject> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Old Kitchen", matches.get(0).getTitle());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchPastProjectsFailsWhenKeywordIsBlank() {
        UserAccount designer = designer();
        pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idSearchPastProjectPg page = new idSearchPastProjectPg();
        page.setKeyword("");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD, page.getMessage());
    }

    @Test
    public void testSearchPastProjectsFailsWhenNothingMatches() {
        UserAccount designer = designer();
        pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idSearchPastProjectPg page = new idSearchPastProjectPg();
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_PAST_PROJECTS, page.getMessage());
    }

    @Test
    public void testSearchPastProjectsExcludesCurrentProjects() {
        UserAccount designer = designer();
        project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        idSearchPastProjectPg page = new idSearchPastProjectPg();
        page.setKeyword("Kitchen");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_PAST_PROJECTS, page.getMessage());
    }
}
