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

public class Us20CreateCurrentProjectTest extends IdpTestCase {

    @Test
    public void testCreateCurrentProject() {
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        signIn(designer);
        idCreateCurrentProjectPg page = new idCreateCurrentProjectPg();
        page.setTitle("Riverside Kitchen");
        page.setDescription("Open plan kitchen with an island");
        page.setStyle("Modern");
        page.setBudget("25000");
        page.setCategoryID(String.valueOf(category.getCategoryID()));
        page.setImageUrls("cover.png,plan.png");
        CreateProjectResult result = page.create();
        assertTrue(result.success());
        assertEquals(Messages.CURRENT_PROJECT_CREATED, result.message());
        DesignProject created = result.project();
        assertNotNull(created);
        assertEquals("Riverside Kitchen", created.getTitle());
        assertEquals(designer.getAccountID(), created.getOwnerAccountID());
        assertEquals(category.getCategoryID(), created.getCategoryID());
        assertEquals(ProjectStage.CURRENT, created.getStage());
        assertEquals(new BigDecimal("25000"), created.getBudget());
        assertEquals(0, created.getViewCount());
        assertEquals(0, created.getShortlistCount());
        assertEquals(2, ProjectImage.ofProject(created.getProjectID()).size());
        assertTrue(created.isCurrent());
    }

    @Test
    public void testCreateCurrentProjectFailsWhenCategoryIsMissing() {
        signIn(designer());
        idCreateCurrentProjectPg page = new idCreateCurrentProjectPg();
        page.setTitle("Riverside Kitchen");
        page.setDescription("Open plan kitchen");
        page.setStyle("Modern");
        page.setBudget("25000");
        page.setCategoryID("9999");
        CreateProjectResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHOOSE_CATEGORY, result.message());
        assertEquals(0, Database.get().projects().size());
    }

    @Test
    public void testCreateCurrentProjectFailsWhenDetailsAreMissing() {
        signIn(designer());
        idCreateCurrentProjectPg page = new idCreateCurrentProjectPg();
        page.setTitle("");
        page.setDescription("Open plan kitchen");
        page.setStyle("Modern");
        page.setBudget("25000");
        page.setCategoryID(String.valueOf(category("Kitchen").getCategoryID()));
        CreateProjectResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
    }

    @Test
    public void testCreateCurrentProjectFailsWhenBudgetIsNegative() {
        signIn(designer());
        idCreateCurrentProjectPg page = new idCreateCurrentProjectPg();
        page.setTitle("Riverside Kitchen");
        page.setDescription("Open plan kitchen");
        page.setStyle("Modern");
        page.setBudget("-100");
        page.setCategoryID(String.valueOf(category("Kitchen").getCategoryID()));
        CreateProjectResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
    }

    @Test
    public void testCreateCurrentProjectFailsWhenActorIsNotDesigner() {
        signIn(customer("alice"));
        idCreateCurrentProjectPg page = new idCreateCurrentProjectPg();
        page.setTitle("Riverside Kitchen");
        page.setDescription("Open plan kitchen");
        page.setStyle("Modern");
        page.setBudget("25000");
        page.setCategoryID(String.valueOf(category("Kitchen").getCategoryID()));
        CreateProjectResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }

    @Test
    public void testCreateCurrentProjectIsVisibleToCustomers() {
        project(designer(), category("Kitchen"), "Riverside Kitchen");
        signIn(customer("alice"));
        cSearchProjectPg page = new cSearchProjectPg();
        page.setKeyword("Riverside");
        List<DesignProject> matches = page.search();
        assertEquals(1, matches.size());
        assertEquals("Riverside Kitchen", matches.get(0).getTitle());
    }
}
