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

public class Us34ViewShortlistedProjectsTest extends IdpTestCase {

    @Test
    public void testViewShortlist() {
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
        cViewShortlistPg page = new cViewShortlistPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        List<Favorite> saved = page.view();
        assertEquals(2, saved.size());
        assertNotNull(saved.get(0).getSavedAt());
        assertNotNull(saved.get(1).getSavedAt());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testViewShortlistFailsWhenItIsEmpty() {
        UserAccount alice = customer("alice");
        signIn(alice);
        cViewShortlistPg page = new cViewShortlistPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        assertTrue(page.view().isEmpty());
        assertEquals(Messages.NO_SAVED_PROJECTS, page.getMessage());
    }

    @Test
    public void testViewShortlistFailsWhenActorIsNotCustomer() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        cViewShortlistPg page = new cViewShortlistPg();
        page.setCustomerID(String.valueOf(designer.getAccountID()));
        assertTrue(page.view().isEmpty());
    }
}
