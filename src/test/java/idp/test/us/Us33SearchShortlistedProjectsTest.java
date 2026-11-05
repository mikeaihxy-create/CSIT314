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

public class Us33SearchShortlistedProjectsTest extends IdpTestCase {

    @Test
    public void testSearchShortlistByKeyword() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject kitchen = project(designer, category, "Riverside Kitchen");
        DesignProject bath = project(designer, category, "Harbour Bathroom");
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(alice.getAccountID()));
        save.setProjectID(String.valueOf(kitchen.getProjectID()));
        assertTrue(save.save().success());
        cSaveProjectPg other = new cSaveProjectPg();
        other.setCustomerID(String.valueOf(alice.getAccountID()));
        other.setProjectID(String.valueOf(bath.getProjectID()));
        assertTrue(other.save().success());
        cSearchShortlistPg page = new cSearchShortlistPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        page.setKeyword("kitchen");
        List<DesignProject> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Riverside Kitchen", matches.get(0).getTitle());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testSearchShortlistFailsWhenKeywordIsBlank() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(alice.getAccountID()));
        save.setProjectID(String.valueOf(created.getProjectID()));
        assertTrue(save.save().success());
        cSearchShortlistPg page = new cSearchShortlistPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        page.setKeyword("");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.ENTER_KEYWORD, page.getMessage());
    }

    @Test
    public void testSearchShortlistFailsWhenNothingMatches() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(alice.getAccountID()));
        save.setProjectID(String.valueOf(created.getProjectID()));
        assertTrue(save.save().success());
        cSearchShortlistPg page = new cSearchShortlistPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        page.setKeyword("zzz");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_SHORTLISTED_PROJECTS, page.getMessage());
    }

    @Test
    public void testSearchShortlistOnlyReturnsOwnProjects() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject kitchen = project(designer, category, "Riverside Kitchen");
        DesignProject bath = project(designer, category, "Harbour Bathroom");
        UserAccount alice = customer("alice");
        UserAccount bob = customer("bob");
        signIn(alice);
        cSaveProjectPg aliceSave = new cSaveProjectPg();
        aliceSave.setCustomerID(String.valueOf(alice.getAccountID()));
        aliceSave.setProjectID(String.valueOf(kitchen.getProjectID()));
        assertTrue(aliceSave.save().success());
        signIn(bob);
        cSaveProjectPg bobSave = new cSaveProjectPg();
        bobSave.setCustomerID(String.valueOf(bob.getAccountID()));
        bobSave.setProjectID(String.valueOf(bath.getProjectID()));
        assertTrue(bobSave.save().success());
        signIn(alice);
        cSearchShortlistPg page = new cSearchShortlistPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        page.setKeyword("Bathroom");
        assertTrue(page.search().isEmpty());
        assertEquals(Messages.NO_SHORTLISTED_PROJECTS, page.getMessage());
    }
}
