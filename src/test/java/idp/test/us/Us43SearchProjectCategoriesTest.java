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

public class Us43SearchProjectCategoriesTest extends IdpTestCase {

    @Test
    public void testSearchCategories() {
        UserAccount manager = manager();
        category("Kitchen");
        category("Bathroom");
        signIn(manager);
        pmSearchCategoryPg page = new pmSearchCategoryPg();
        page.setKeyword("kit");
        List<ProjectCategory> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Kitchen", matches.get(0).getName());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchCategoriesFailsWhenKeywordIsBlank() {
        category("Kitchen");
        signIn(manager());
        pmSearchCategoryPg page = new pmSearchCategoryPg();
        page.setKeyword("");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD, page.getMessage());
    }

    @Test
    public void testSearchCategoriesFailsWhenNothingMatches() {
        category("Kitchen");
        signIn(manager());
        pmSearchCategoryPg page = new pmSearchCategoryPg();
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_CATEGORIES, page.getMessage());
    }

    @Test
    public void testSearchCategoriesFailsWhenActorIsNotPlatformManagement() {
        category("Kitchen");
        signIn(designer());
        pmSearchCategoryPg page = new pmSearchCategoryPg();
        page.setKeyword("Kitchen");
        assertTrue(page.search().isEmpty());
    }
}
