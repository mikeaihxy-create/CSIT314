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

public class Us28DeletePastProjectTest extends IdpTestCase {

    @Test
    public void testDeletePastProject() {
        UserAccount designer = designer();
        DesignProject created = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(designer);
        idDeletePastProjectPg page = new idDeletePastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DeleteProjectResult result = page.delete();
        assertTrue(result.success());
        assertEquals(Messages.PAST_PROJECT_DELETED, result.message());
        assertEquals(0, Database.get().projects().size());
        assertEquals(0, Database.get().images().size());
    }

    @Test
    public void testDeletePastProjectFailsWhenItDoesNotExist() {
        signIn(designer());
        idDeletePastProjectPg page = new idDeletePastProjectPg();
        page.setProjectID("9999");
        DeleteProjectResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.PAST_PROJECT_NOT_FOUND, result.message());
    }

    @Test
    public void testDeletePastProjectFailsWhenActorIsNotDesigner() {
        UserAccount designer = designer();
        DesignProject created = pastProject(designer, category("Kitchen"), "Old Kitchen");
        signIn(customer("alice"));
        idDeletePastProjectPg page = new idDeletePastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DeleteProjectResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(1, Database.get().projects().size());
    }

    @Test
    public void testDeletePastProjectFailsWhenAnotherDesignerOwnsIt() {
        UserAccount owner = designer("owner");
        DesignProject created = pastProject(owner, category("Kitchen"), "Old Kitchen");
        signIn(designer("other"));
        idDeletePastProjectPg page = new idDeletePastProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        DeleteProjectResult result = page.delete();
        assertFalse(result.success());
        assertEquals(Messages.PAST_PROJECT_NOT_FOUND, result.message());
    }
}
