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

public class Us32SaveInteriorDesignProjectTest extends IdpTestCase {

    @Test
    public void testSaveProjectToShortlist() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg page = new cSaveProjectPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        page.setProjectID(String.valueOf(created.getProjectID()));
        SaveProjectResult result = page.save();
        assertTrue(result.success());
        assertEquals(Messages.PROJECT_SAVED, result.message());
        assertNotNull(result.favorite());
        assertEquals(alice.getAccountID(), result.favorite().getCustomerAccountID());
        assertEquals(created.getProjectID(), result.favorite().getProjectID());
        assertNotNull(result.favorite().getSavedAt());
        assertEquals(1, created.getShortlistCount());
        assertEquals(1, Database.get().favorites().size());
    }

    @Test
    public void testSaveProjectTwiceIsRejected() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg first = new cSaveProjectPg();
        first.setCustomerID(String.valueOf(alice.getAccountID()));
        first.setProjectID(String.valueOf(created.getProjectID()));
        assertTrue(first.save().success());
        cSaveProjectPg second = new cSaveProjectPg();
        second.setCustomerID(String.valueOf(alice.getAccountID()));
        second.setProjectID(String.valueOf(created.getProjectID()));
        SaveProjectResult result = second.save();
        assertFalse(result.success());
        assertEquals(Messages.PROJECT_ALREADY_SAVED, result.message());
        assertEquals(1, created.getShortlistCount());
        assertEquals(1, Database.get().favorites().size());
    }

    @Test
    public void testSaveProjectFailsWhenItDoesNotExist() {
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg page = new cSaveProjectPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        page.setProjectID("9999");
        SaveProjectResult result = page.save();
        assertFalse(result.success());
        assertEquals(Messages.PROJECT_NOT_AVAILABLE, result.message());
        assertEquals(0, Database.get().favorites().size());
    }

    @Test
    public void testSaveProjectFailsWhenActorIsNotCustomer() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        cSaveProjectPg page = new cSaveProjectPg();
        page.setCustomerID(String.valueOf(designer.getAccountID()));
        page.setProjectID(String.valueOf(created.getProjectID()));
        SaveProjectResult result = page.save();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(0, created.getShortlistCount());
    }

    @Test
    public void testSavedProjectAppearsInTheShortlist() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(alice.getAccountID()));
        save.setProjectID(String.valueOf(created.getProjectID()));
        assertTrue(save.save().success());
        cViewShortlistPg page = new cViewShortlistPg();
        page.setCustomerID(String.valueOf(alice.getAccountID()));
        List<Favorite> saved = page.view();
        assertEquals(1, saved.size());
        assertEquals(created.getProjectID(), saved.get(0).getProjectID());
        assertEquals("", page.getMessage());
    }
}
