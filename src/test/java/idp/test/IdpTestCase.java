package idp.test;

import idp.boundary.idCreateCurrentProjectPg;
import idp.boundary.idCreatePastProjectPg;
import idp.database.Database;
import idp.dto.CreateProjectResult;
import idp.entity.CompanyProfile;
import idp.entity.DesignProject;
import idp.entity.ProjectCategory;
import idp.entity.UserAccount;
import idp.entity.UserProfile;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import org.junit.After;
import org.junit.Before;

public abstract class IdpTestCase {

    protected static final String PASSWORD = "Passw0rd!";

    @Before
    public void setUp() {
        SessionManager.get().reset();
        Database.reset();
        Clock.useSystemClock();
    }

    @After
    public void tearDown() {
        SessionManager.get().reset();
        Database.reset();
        Clock.useSystemClock();
    }

    protected UserAccount account(String username, String email, String password, Role role) {
        UserAccount account = new UserAccount(username, email, password, role);
        Database.get().accounts().save(account);
        return account;
    }

    protected UserAccount admin() {
        return admin("admin");
    }

    protected UserAccount admin(String username) {
        return account(username, username + "@idp.test", PASSWORD, Role.USER_ADMIN);
    }

    protected UserAccount designer() {
        return designer("designer");
    }

    protected UserAccount designer(String username) {
        return account(username, username + "@idp.test", PASSWORD, Role.INTERIOR_DESIGNER);
    }

    protected UserAccount customer() {
        return customer("customer");
    }

    protected UserAccount customer(String username) {
        return account(username, username + "@idp.test", PASSWORD, Role.CUSTOMER);
    }

    protected UserAccount manager() {
        return manager("manager");
    }

    protected UserAccount manager(String username) {
        return account(username, username + "@idp.test", PASSWORD, Role.PLATFORM_MANAGEMENT);
    }

    protected String signIn(UserAccount account) {
        return SessionManager.get().start(account);
    }

    protected void signOut() {
        SessionManager.get().end();
    }

    protected UserProfile profileOf(UserAccount account, String fullName) {
        UserProfile profile = new UserProfile(account.getAccountID(), fullName, "91234567", "12 Orchard Road");
        Database.get().profiles().save(profile);
        return profile;
    }

    protected CompanyProfile companyOf(UserAccount owner, String name) {
        CompanyProfile company = new CompanyProfile(owner.getAccountID(), name, "Interior design studio",
                "Singapore", owner.getEmail(), "61234567", "logo.png");
        Database.get().companies().save(company);
        return company;
    }

    protected ProjectCategory category(String name) {
        ProjectCategory category = new ProjectCategory(name, name + " projects");
        Database.get().categories().save(category);
        return category;
    }

    protected DesignProject project(UserAccount owner, ProjectCategory category, String title) {
        return createProject(owner, category, title, null);
    }

    protected DesignProject pastProject(UserAccount owner, ProjectCategory category, String title) {
        return createProject(owner, category, title, "2025-06-30");
    }

    private DesignProject createProject(UserAccount owner, ProjectCategory category, String title, String completionDate) {
        signIn(owner);
        CreateProjectResult result;
        if (completionDate == null) {
            idCreateCurrentProjectPg page = new idCreateCurrentProjectPg();
            page.setTitle(title);
            page.setDescription(title + " description");
            page.setStyle("Modern");
            page.setBudget("25000");
            page.setCategoryID(String.valueOf(category.getCategoryID()));
            page.setImageUrls("cover.png,plan.png");
            result = page.create();
        } else {
            idCreatePastProjectPg page = new idCreatePastProjectPg();
            page.setTitle(title);
            page.setDescription(title + " description");
            page.setStyle("Modern");
            page.setBudget("25000");
            page.setCategoryID(String.valueOf(category.getCategoryID()));
            page.setCompletionDate(completionDate);
            page.setImageUrls("cover.png,plan.png");
            result = page.create();
        }
        return result.project();
    }
}
