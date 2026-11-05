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

public class Us22UpdateCurrentProjectTest extends IdpTestCase {

    @Test
    public void testUpdateCurrentProject() {
        UserAccount designer = designer();
        ProjectCategory kitchen = category("Kitchen");
        ProjectCategory bath = category("Bathroom");
        DesignProject created = project(designer, kitchen, "Riverside Kitchen");
        signIn(designer);
        idUpdateCurrentProjectPg page = new idUpdateCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        page.setTitle("Riverside Kitchen v2");
        page.setDescription("Open plan kitchen, revised");
        page.setStyle("Scandinavian");
        page.setBudget("31000");
        page.setCategoryID(String.valueOf(bath.getCategoryID()));
        UpdateProjectResult result = page.update();
        assertTrue(result.success());
        assertEquals(Messages.CURRENT_PROJECT_UPDATED, result.message());
        assertEquals("Riverside Kitchen v2", created.getTitle());
        assertEquals("Scandinavian", created.getStyle());
        assertEquals(new BigDecimal("31000"), created.getBudget());
        assertEquals(bath.getCategoryID(), created.getCategoryID());
    }

    @Test
    public void testUpdateCurrentProjectReplacesImages() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        idUpdateCurrentProjectPg page = new idUpdateCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        page.setTitle("Riverside Kitchen");
        page.setDescription("Open plan kitchen");
        page.setStyle("Modern");
        page.setBudget("25000");
        page.setCategoryID(String.valueOf(created.getCategoryID()));
        page.setImageUrls("new1.png,new2.png,new3.png");
        UpdateProjectResult result = page.update();
        assertTrue(result.success());
        List<ProjectImage> images = ProjectImage.ofProject(created.getProjectID());
        assertEquals(3, images.size());
        assertEquals("new1.png", images.get(0).getImageUrl());
        assertTrue(images.get(0).isCover());
    }

    @Test
    public void testUpdateCurrentProjectFailsWhenItDoesNotExist() {
        signIn(designer());
        idUpdateCurrentProjectPg page = new idUpdateCurrentProjectPg();
        page.setProjectID("9999");
        page.setTitle("Ghost");
        UpdateProjectResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CURRENT_PROJECT_NOT_FOUND, result.message());
    }

    @Test
    public void testUpdateCurrentProjectFailsWhenCategoryIsUnknown() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(designer);
        idUpdateCurrentProjectPg page = new idUpdateCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        page.setTitle("Riverside Kitchen");
        page.setDescription("Open plan kitchen");
        page.setStyle("Modern");
        page.setBudget("25000");
        page.setCategoryID("9999");
        UpdateProjectResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.CHOOSE_CATEGORY, result.message());
    }

    @Test
    public void testUpdateCurrentProjectFailsWhenActorIsNotDesigner() {
        UserAccount designer = designer();
        DesignProject created = project(designer, category("Kitchen"), "Riverside Kitchen");
        signIn(customer("alice"));
        idUpdateCurrentProjectPg page = new idUpdateCurrentProjectPg();
        page.setProjectID(String.valueOf(created.getProjectID()));
        page.setTitle("Hijacked");
        UpdateProjectResult result = page.update();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals("Riverside Kitchen", created.getTitle());
    }
}
