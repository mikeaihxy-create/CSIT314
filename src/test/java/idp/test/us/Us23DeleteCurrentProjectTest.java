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

public class Us23DeleteCurrentProjectTest extends IdpTestCase {

    @Test
    public void testDeleteCurrentProject() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        UserAccount alice = customer("alice");
        signIn(alice);
        cSaveProjectPg save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(alice.getAccountID()));
        save.setProjectID(String.valueOf(created.getProjectID()));
        assertTrue(save.save().success());
        signIn(designer);
        idDeleteCurrentProjectPg page = new idDeleteCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DeleteProjectResult result = page.delete();
        assertTrue(result.success());
        assertEquals(Messages.CURRENT_PROJECT_DELETED, result.message());
        assertEquals(0, Database.get().projects().size());
        assertEquals(0, Database.get().images().size());
        assertEquals(0, Database.get().favorites().size());
    }

    @Test
    public void testDeleteCurrentProjectFailsWhenItDoesNotExist() {
        signIn(designer());
        idDeleteCurrentProjectPg page = new idDeleteCurrentProjectPg();
        page.setProjectID("9999");
        DeleteProjectResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.CURRENT_PROJECT_NOT_FOUND, result.message());
    }

    @Test
    public void testDeleteCurrentProjectFailsWhenActorIsNotDesigner() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(customer("alice"));
        idDeleteCurrentProjectPg page = new idDeleteCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DeleteProjectResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(1, Database.get().projects().size());
    }

    @Test
    public void testDeleteCurrentProjectFailsWhenAnotherDesignerOwnsIt() {
        UserAccount owner = designer("owner");
        DesignProject created = project(owner, category("Kitchen"), "Riverside Kitchen");
        signIn(designer("other"));
        idDeleteCurrentProjectPg page = new idDeleteCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DeleteProjectResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.CURRENT_PROJECT_NOT_FOUND, result.message());
        assertEquals(1, Database.get().projects().size());
    }
}
