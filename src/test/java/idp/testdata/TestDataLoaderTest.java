package idp.testdata;

import idp.boundary.loginPg;
import idp.database.Database;
import idp.dto.LoginResult;
import idp.entity.ActivityEvent;
import idp.entity.CompanyProfile;
import idp.entity.DesignProject;
import idp.entity.Favorite;
import idp.entity.ProjectImage;
import idp.entity.UserAccount;
import idp.entity.UserProfile;
import idp.enumeration.EventType;
import idp.enumeration.ProjectStage;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.test.IdpTestCase;
import idp.util.Messages;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestDataLoaderTest extends IdpTestCase {

    private static final int MINIMUM_ROWS = 100;

    private TestDataLoader.Summary load() {
        return new TestDataLoader().load();
    }

    @Test
    public void testEveryTableLoadsAtLeastOneHundredRecords() {
        TestDataLoader.Summary summary = load();
        assertTrue("accounts: " + summary, summary.accounts() >= MINIMUM_ROWS);
        assertTrue("profiles: " + summary, summary.profiles() >= MINIMUM_ROWS);
        assertTrue("companies: " + summary, summary.companies() >= MINIMUM_ROWS);
        assertTrue("categories: " + summary, summary.categories() >= MINIMUM_ROWS);
        assertTrue("projects: " + summary, summary.projects() >= MINIMUM_ROWS);
        assertTrue("images: " + summary, summary.images() >= MINIMUM_ROWS);
        assertTrue("favorites: " + summary, summary.favorites() >= MINIMUM_ROWS);
        assertTrue("events: " + summary, summary.events() >= MINIMUM_ROWS);

        assertEquals(summary.accounts(), Database.get().accounts().size());
        assertEquals(summary.projects(), Database.get().projects().size());
        assertEquals(summary.events(), Database.get().events().size());
        assertEquals(400, summary.accounts());
        assertEquals(300, summary.projects());
        assertEquals(2000, summary.events());
    }

    @Test
    public void testEveryRoleHasRecordsAndSuspendedAccountsAreLoadedAsSuspended() {
        load();
        int admins = 0;
        int designers = 0;
        int customers = 0;
        int managers = 0;
        for (UserAccount account : Database.get().accounts().all()) {
            switch (account.getRole()) {
                case USER_ADMIN -> admins++;
                case INTERIOR_DESIGNER -> designers++;
                case CUSTOMER -> customers++;
                case PLATFORM_MANAGEMENT -> managers++;
            }
        }
        assertEquals(MINIMUM_ROWS, admins);
        assertEquals(MINIMUM_ROWS, designers);
        assertEquals(MINIMUM_ROWS, customers);
        assertEquals(MINIMUM_ROWS, managers);

        UserAccount suspended = Database.get().accounts().find(50);
        assertNotNull(suspended);
        assertFalse(suspended.isActive());
    }

    @Test
    public void testForeignKeysPointAtExistingRows() {
        load();
        Set<Long> accountIds = idsOfAccounts();
        Set<Long> projectIds = idsOfProjects();
        Set<Long> categoryIds = idsOfCategories();

        for (UserProfile profile : Database.get().profiles().all()) {
            assertTrue("profile " + profile.getProfileID(), accountIds.contains(profile.getAccountID()));
        }
        for (CompanyProfile company : Database.get().companies().all()) {
            assertTrue("company " + company.getCompanyID(), accountIds.contains(company.getOwnerAccountID()));
            UserAccount owner = Database.get().accounts().find(company.getOwnerAccountID());
            assertEquals("company " + company.getCompanyID(), Role.INTERIOR_DESIGNER, owner.getRole());
        }
        for (DesignProject project : Database.get().projects().all()) {
            assertTrue("project " + project.getProjectID(), accountIds.contains(project.getOwnerAccountID()));
            assertTrue("project " + project.getProjectID(), categoryIds.contains(project.getCategoryID()));
            UserAccount owner = Database.get().accounts().find(project.getOwnerAccountID());
            assertEquals("project " + project.getProjectID(), Role.INTERIOR_DESIGNER, owner.getRole());
        }
        for (ProjectImage image : Database.get().images().all()) {
            assertTrue("image " + image.getImageID(), projectIds.contains(image.getProjectID()));
        }
        for (Favorite favorite : Database.get().favorites().all()) {
            assertTrue("favorite " + favorite.getFavoriteID(), accountIds.contains(favorite.getCustomerAccountID()));
            assertTrue("favorite " + favorite.getFavoriteID(), projectIds.contains(favorite.getProjectID()));
            UserAccount customer = Database.get().accounts().find(favorite.getCustomerAccountID());
            assertEquals("favorite " + favorite.getFavoriteID(), Role.CUSTOMER, customer.getRole());
        }
        for (ActivityEvent event : Database.get().events().all()) {
            assertTrue("event " + event.getEventID(), projectIds.contains(event.getProjectID()));
            assertTrue("event " + event.getEventID(), accountIds.contains(event.getActorAccountID()));
        }
    }

    @Test
    public void testProjectsKeepTheirStageAndCounters() {
        load();
        long current = 0;
        long past = 0;
        for (DesignProject project : Database.get().projects().all()) {
            if (project.getStage() == ProjectStage.CURRENT) {
                current++;
                assertNull("current project " + project.getProjectID(), project.getCompletionDate());
            } else {
                past++;
                assertNotNull("past project " + project.getProjectID(), project.getCompletionDate());
            }
            assertTrue(project.getViewCount() >= 0);
            assertTrue(project.getShortlistCount() >= 0);
        }
        assertEquals(200, current);
        assertEquals(100, past);
    }

    @Test
    public void testLoadedAccountCanLogIn() {
        load();
        loginPg page = new loginPg();
        page.setUsernameOrEmail("user0201");
        page.setPassword("Passw0rd!201");
        LoginResult result = page.login();
        assertTrue(result.message(), result.success());
        assertEquals(Role.CUSTOMER, result.role());
        assertNotNull(SessionManager.get().currentSessionId());
        assertEquals(201, SessionManager.get().currentAccountId());
    }

    @Test
    public void testSuspendedLoadedAccountCannotLogIn() {
        load();
        loginPg page = new loginPg();
        page.setUsernameOrEmail("user0050@idp.test");
        page.setPassword("Passw0rd!50");
        LoginResult result = page.login();
        assertFalse(result.success());
        assertEquals(Messages.ACCOUNT_NOT_ACTIVE, result.message());
        assertNull(result.sessionId());
    }

    @Test
    public void testActivityLogContainsEveryEventType() {
        load();
        Set<EventType> types = new HashSet<>();
        for (ActivityEvent event : Database.get().events().all()) {
            types.add(event.getEventType());
        }
        assertTrue(types.contains(EventType.PROJECT_VIEW));
        assertTrue(types.contains(EventType.PROJECT_SHORTLIST));
        assertTrue(types.contains(EventType.PROJECT_CREATE));
        assertTrue(types.contains(EventType.PROJECT_UPDATE));
    }

    private Set<Long> idsOfAccounts() {
        Set<Long> ids = new HashSet<>();
        for (UserAccount account : Database.get().accounts().all()) {
            ids.add(account.getAccountID());
        }
        return ids;
    }

    private Set<Long> idsOfProjects() {
        Set<Long> ids = new HashSet<>();
        for (DesignProject project : Database.get().projects().all()) {
            ids.add(project.getProjectID());
        }
        return ids;
    }

    private Set<Long> idsOfCategories() {
        Set<Long> ids = new HashSet<>();
        for (idp.entity.ProjectCategory category : Database.get().categories().all()) {
            ids.add(category.getCategoryID());
        }
        return ids;
    }
}
