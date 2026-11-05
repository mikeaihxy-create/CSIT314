package idp.test;

import idp.boundary.cSaveProjectPg;
import idp.boundary.cSearchProjectPg;
import idp.boundary.cSearchShortlistPg;
import idp.boundary.cViewProjectPg;
import idp.boundary.cViewShortlistPg;
import idp.boundary.idCreateCompanyPg;
import idp.boundary.idCreateCurrentProjectPg;
import idp.boundary.idCreatePastProjectPg;
import idp.boundary.idDeleteCompanyPg;
import idp.boundary.idDeleteCurrentProjectPg;
import idp.boundary.idDeletePastProjectPg;
import idp.boundary.idSearchCompanyPg;
import idp.boundary.idSearchCurrentProjectPg;
import idp.boundary.idSearchPastProjectPg;
import idp.boundary.idUpdateCompanyPg;
import idp.boundary.idUpdateCurrentProjectPg;
import idp.boundary.idUpdatePastProjectPg;
import idp.boundary.idViewCompanyPg;
import idp.boundary.idViewCurrentProjectPg;
import idp.boundary.idViewPastProjectPg;
import idp.boundary.idViewProjectShortlistsPg;
import idp.boundary.idViewProjectViewsPg;
import idp.boundary.loginPg;
import idp.boundary.logoutPg;
import idp.boundary.pmCreateCategoryPg;
import idp.boundary.pmDeleteCategoryPg;
import idp.boundary.pmGenerateDailyReportPg;
import idp.boundary.pmGenerateMonthlyReportPg;
import idp.boundary.pmGenerateWeeklyReportPg;
import idp.boundary.pmSearchCategoryPg;
import idp.boundary.pmUpdateCategoryPg;
import idp.boundary.pmViewCategoryPg;
import idp.boundary.uaCreateAccountPg;
import idp.boundary.uaCreateProfilePg;
import idp.boundary.uaSearchAccountPg;
import idp.boundary.uaSearchProfilePg;
import idp.boundary.uaSuspendAccountPg;
import idp.boundary.uaSuspendProfilePg;
import idp.boundary.uaUpdateAccountPg;
import idp.boundary.uaUpdateProfilePg;
import idp.boundary.uaViewAccountPg;
import idp.boundary.uaViewProfilePg;
import idp.dto.CreateProjectResult;
import idp.dto.ProjectShortlistResult;
import idp.dto.ProjectViewResult;
import idp.dto.ReportResult;
import idp.entity.CompanyProfile;
import idp.entity.DesignProject;
import idp.entity.ProjectCategory;
import idp.entity.UserAccount;
import idp.entity.UserProfile;
import idp.enumeration.AccountStatus;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import idp.util.Messages;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoundaryDemo extends IdpTestCase {

    private static final int TITLE_WIDTH = 50;
    private static final String LOGIN_OK = "Login successful.";

    private final List<String> failures = new ArrayList<>();
    private int passed;
    private int failed;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" Interior Design Platform (IDP) - boundary demonstration");
        System.out.println(" CSIT314 Software Development Methodologies - group project");
        System.out.println(" run at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("================================================================================");
        System.out.println();

        BoundaryDemo demo = new BoundaryDemo();
        demo.sprintOne();
        demo.sprintTwo();
        demo.sprintThree();
        demo.sprintFour();
        demo.sprintFive();
        demo.summary();

        System.exit(demo.failed == 0 ? 0 : 1);
    }

    private void fresh() {
        setUp();
    }

    private void banner(String text) {
        System.out.println();
        System.out.println("-- " + text + " " + repeat('-', Math.max(0, 76 - text.length())));
    }

    private void sprintOne() {
        banner("Sprint 1: Login and Logout (US11, US12)");

        fresh();
        UserAccount admin = admin();
        loginPg login = new loginPg();
        login.setUsernameOrEmail("admin");
        login.setPassword(PASSWORD);
        login.login();
        check("US11", "Log in with correct password", LOGIN_OK, login.getMessage());
        checkValue("US11", "Log in starts a session", Boolean.TRUE, SessionManager.get().isLoggedIn());

        fresh();
        admin = admin();
        login = new loginPg();
        login.setUsernameOrEmail("admin");
        login.setPassword("WrongPass1");
        login.login();
        check("US11", "Log in with wrong password is rejected", Messages.LOGIN_WRONG, login.getMessage());

        fresh();
        admin = admin();
        login = new loginPg();
        login.setUsernameOrEmail("nobody");
        login.setPassword(PASSWORD);
        login.login();
        check("US11", "Log in with unknown username is rejected", Messages.LOGIN_WRONG, login.getMessage());

        fresh();
        customer("someone");
        login = new loginPg();
        login.setUsernameOrEmail("someone@idp.test");
        login.setPassword(PASSWORD);
        login.login();
        check("US11", "Log in with the email instead of the username", LOGIN_OK, login.getMessage());

        fresh();
        UserAccount blocked = account("blocked", "blocked@idp.test", PASSWORD, Role.CUSTOMER);
        blocked.setStatus(AccountStatus.SUSPENDED);
        login = new loginPg();
        login.setUsernameOrEmail("blocked");
        login.setPassword(PASSWORD);
        login.login();
        check("US11", "Suspended account cannot log in", Messages.ACCOUNT_NOT_ACTIVE, login.getMessage());

        fresh();
        admin = admin();
        login = new loginPg();
        login.setUsernameOrEmail("admin");
        login.setPassword("");
        login.login();
        check("US11", "Log in with empty password is rejected", Messages.FILL_REQUIRED_FIELDS, login.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        logoutPg logout = new logoutPg();
        logout.logout();
        check("US12", "Log out ends the session", Messages.LOGGED_OUT, logout.getMessage());
        checkValue("US12", "Log out clears the current session", Boolean.FALSE, SessionManager.get().isLoggedIn());
    }

    private void sprintTwo() {
        banner("Sprint 2: User Admin (US01 - US10)");

        fresh();
        UserAccount admin = admin();
        signIn(admin);
        uaCreateAccountPg create = new uaCreateAccountPg();
        create.setUsername("alice");
        create.setEmail("alice@idp.test");
        create.setPassword(PASSWORD);
        create.setRole("CUSTOMER");
        create.create();
        check("US01", "Create account with valid details", Messages.ACCOUNT_CREATED, create.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        create = new uaCreateAccountPg();
        create.setUsername("admin");
        create.setEmail("other@idp.test");
        create.setPassword(PASSWORD);
        create.setRole("CUSTOMER");
        create.create();
        check("US01", "Create account with a taken username", Messages.USERNAME_OR_EMAIL_USED, create.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        create = new uaCreateAccountPg();
        create.setUsername("bob");
        create.setEmail("bob@idp.test");
        create.setPassword("123");
        create.setRole("CUSTOMER");
        create.create();
        check("US01", "Create account with a short password", Messages.CHECK_DETAILS, create.getMessage());

        fresh();
        UserAccount customer1 = customer("cust1");
        signIn(customer1);
        create = new uaCreateAccountPg();
        create.setUsername("eve");
        create.setEmail("eve@idp.test");
        create.setPassword(PASSWORD);
        create.setRole("CUSTOMER");
        create.create();
        check("US01", "Only User Admin may create accounts", Messages.NOT_AUTHORISED, create.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        uaViewAccountPg viewAccount = new uaViewAccountPg();
        viewAccount.setAccountID(String.valueOf(admin.getAccountID()));
        viewAccount.view();
        check("US02", "View an existing account", "", viewAccount.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        viewAccount = new uaViewAccountPg();
        viewAccount.setAccountID("99999");
        viewAccount.view();
        check("US02", "View an account that does not exist", Messages.ACCOUNT_NOT_FOUND, viewAccount.getMessage());

        fresh();
        admin = admin();
        UserAccount designer1 = designer("des1");
        signIn(admin);
        uaUpdateAccountPg updateAccount = new uaUpdateAccountPg();
        updateAccount.setAccountID(String.valueOf(designer1.getAccountID()));
        updateAccount.setFullName("Designer One");
        updateAccount.setEmail("des1.new@idp.test");
        updateAccount.setRole("INTERIOR_DESIGNER");
        updateAccount.update();
        check("US03", "Update an account with valid changes", Messages.ACCOUNT_UPDATED, updateAccount.getMessage());

        fresh();
        admin = admin();
        designer1 = designer("des1");
        customer1 = customer("cust1");
        signIn(admin);
        updateAccount = new uaUpdateAccountPg();
        updateAccount.setAccountID(String.valueOf(designer1.getAccountID()));
        updateAccount.setFullName("Designer One");
        updateAccount.setEmail(customer1.getEmail());
        updateAccount.setRole("INTERIOR_DESIGNER");
        updateAccount.update();
        check("US03", "Update to an email another user owns", Messages.EMAIL_USED, updateAccount.getMessage());

        fresh();
        admin = admin();
        designer1 = designer("des1");
        signIn(admin);
        uaSuspendAccountPg suspendAccount = new uaSuspendAccountPg();
        suspendAccount.setAccountID(String.valueOf(designer1.getAccountID()));
        suspendAccount.suspend();
        check("US04", "Suspend an active account", Messages.ACCOUNT_SUSPENDED, suspendAccount.getMessage());
        checkValue("US04", "Suspend sets the status to SUSPENDED", AccountStatus.SUSPENDED, designer1.getStatus());

        fresh();
        admin = admin();
        designer1 = designer("des1");
        signIn(admin);
        suspendAccount = new uaSuspendAccountPg();
        suspendAccount.setAccountID(String.valueOf(designer1.getAccountID()));
        suspendAccount.suspend();
        suspendAccount.suspend();
        check("US04", "Suspend an account twice", Messages.ACCOUNT_ALREADY_SUSPENDED, suspendAccount.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        uaSearchAccountPg searchAccount = new uaSearchAccountPg();
        searchAccount.setKeyword("adm");
        searchAccount.search();
        check("US05", "Search accounts by keyword", "", searchAccount.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        searchAccount = new uaSearchAccountPg();
        searchAccount.setKeyword("");
        searchAccount.search();
        check("US05", "Search accounts with an empty keyword", Messages.ENTER_KEYWORD, searchAccount.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        searchAccount = new uaSearchAccountPg();
        searchAccount.setKeyword("zzzz");
        searchAccount.search();
        check("US05", "Search accounts with no match", Messages.NO_ACCOUNTS, searchAccount.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        signIn(admin);
        uaCreateProfilePg createProfile = new uaCreateProfilePg();
        createProfile.setAccountID(String.valueOf(customer1.getAccountID()));
        createProfile.setFullName("Customer One");
        createProfile.setPhone("91234567");
        createProfile.setAddress("12 Orchard Road");
        createProfile.create();
        check("US06", "Create a profile for an account", Messages.PROFILE_CREATED, createProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profileOf(customer1, "Customer One");
        signIn(admin);
        createProfile = new uaCreateProfilePg();
        createProfile.setAccountID(String.valueOf(customer1.getAccountID()));
        createProfile.setFullName("Customer One Again");
        createProfile.setPhone("91234567");
        createProfile.setAddress("12 Orchard Road");
        createProfile.create();
        check("US06", "An account may only have one profile", Messages.CHECK_DETAILS, createProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        UserProfile profile = profileOf(customer1, "Customer One");
        signIn(admin);
        uaViewProfilePg viewProfile = new uaViewProfilePg();
        viewProfile.setProfileID(String.valueOf(profile.getProfileID()));
        viewProfile.view();
        check("US07", "View an existing profile", "", viewProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profileOf(customer1, "Customer One");
        signIn(admin);
        viewProfile = new uaViewProfilePg();
        viewProfile.setProfileID("99999");
        viewProfile.view();
        check("US07", "View a profile that does not exist", Messages.PROFILE_NOT_FOUND, viewProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profile = profileOf(customer1, "Customer One");
        signIn(admin);
        uaUpdateProfilePg updateProfile = new uaUpdateProfilePg();
        updateProfile.setProfileID(String.valueOf(profile.getProfileID()));
        updateProfile.setFullName("Customer One Updated");
        updateProfile.setPhone("98765432");
        updateProfile.setAddress("8 Marina Bay");
        updateProfile.update();
        check("US08", "Update a profile with valid changes", Messages.PROFILE_UPDATED, updateProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profile = profileOf(customer1, "Customer One");
        signIn(admin);
        uaSuspendProfilePg suspendProfile = new uaSuspendProfilePg();
        suspendProfile.setProfileID(String.valueOf(profile.getProfileID()));
        suspendProfile.suspend();
        check("US09", "Suspend a profile", Messages.PROFILE_SUSPENDED, suspendProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profile = profileOf(customer1, "Customer One");
        signIn(admin);
        suspendProfile = new uaSuspendProfilePg();
        suspendProfile.setProfileID(String.valueOf(profile.getProfileID()));
        suspendProfile.suspend();
        suspendProfile.suspend();
        check("US09", "Suspend a profile twice", Messages.PROFILE_ALREADY_SUSPENDED, suspendProfile.getMessage());
        checkValue("US09", "Suspending a profile keeps the account ACTIVE", AccountStatus.ACTIVE, customer1.getStatus());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profile = profileOf(customer1, "Customer One");
        signIn(admin);
        suspendProfile = new uaSuspendProfilePg();
        suspendProfile.setProfileID(String.valueOf(profile.getProfileID()));
        suspendProfile.suspend();
        updateProfile = new uaUpdateProfilePg();
        updateProfile.setProfileID(String.valueOf(profile.getProfileID()));
        updateProfile.setFullName("Should Not Change");
        updateProfile.setPhone("98765432");
        updateProfile.setAddress("8 Marina Bay");
        updateProfile.update();
        check("US08", "A suspended profile cannot be updated", Messages.PROFILE_IS_SUSPENDED, updateProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profileOf(customer1, "Customer One");
        signIn(admin);
        uaSearchProfilePg searchProfile = new uaSearchProfilePg();
        searchProfile.setKeyword("Customer");
        searchProfile.search();
        check("US10", "Search profiles by keyword", "", searchProfile.getMessage());

        fresh();
        admin = admin();
        signIn(admin);
        searchProfile = new uaSearchProfilePg();
        searchProfile.setKeyword("  ");
        searchProfile.search();
        check("US10", "Search profiles with an empty keyword", Messages.ENTER_KEYWORD, searchProfile.getMessage());

        fresh();
        admin = admin();
        customer1 = customer("cust1");
        profileOf(customer1, "Customer One");
        signIn(admin);
        searchProfile = new uaSearchProfilePg();
        searchProfile.setKeyword("zzzz");
        searchProfile.search();
        check("US10", "Search profiles with no match", Messages.NO_PROFILES, searchProfile.getMessage());
    }

    private void sprintThree() {
        banner("Sprint 3: Interior Designer (US13 - US29, US37, US38)");

        fresh();
        UserAccount designer1 = designer("des1");
        signIn(designer1);
        idCreateCompanyPg createCompany = new idCreateCompanyPg();
        createCompany.setName("Studio One");
        createCompany.setDescription("Minimalist interiors");
        createCompany.setLocation("Singapore");
        createCompany.setContactEmail("hello@studioone.test");
        createCompany.setPhone("61234567");
        createCompany.setLogoUrl("logo.png");
        createCompany.create();
        check("US13", "Create a company profile", Messages.COMPANY_CREATED, createCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        signIn(designer1);
        createCompany = new idCreateCompanyPg();
        createCompany.setName("Studio One");
        createCompany.setDescription("Minimalist interiors");
        createCompany.setLocation("Singapore");
        createCompany.setContactEmail("hello@studioone.test");
        createCompany.setPhone("61234567");
        createCompany.setLogoUrl("logo.png");
        createCompany.create();
        createCompany = new idCreateCompanyPg();
        createCompany.setName("Studio Two");
        createCompany.setDescription("Second studio");
        createCompany.setLocation("Singapore");
        createCompany.setContactEmail("hello@studiotwo.test");
        createCompany.setPhone("61234567");
        createCompany.setLogoUrl("logo.png");
        createCompany.create();
        check("US13", "A designer may only own one company", Messages.CHECK_DETAILS, createCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        UserAccount designer2 = designer("des2");
        signIn(designer1);
        createCompany = new idCreateCompanyPg();
        createCompany.setName("Studio One");
        createCompany.setDescription("Minimalist interiors");
        createCompany.setLocation("Singapore");
        createCompany.setContactEmail("hello@studioone.test");
        createCompany.setPhone("61234567");
        createCompany.setLogoUrl("logo.png");
        createCompany.create();
        signIn(designer2);
        createCompany = new idCreateCompanyPg();
        createCompany.setName("Studio One");
        createCompany.setDescription("Copy cat");
        createCompany.setLocation("Singapore");
        createCompany.setContactEmail("hello@studiotwo.test");
        createCompany.setPhone("61234567");
        createCompany.setLogoUrl("logo.png");
        createCompany.create();
        check("US13", "A company name may only be used once", Messages.COMPANY_NAME_USED, createCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        CompanyProfile ownCompany = companyOf(designer1, "Studio One");
        signIn(designer1);
        idViewCompanyPg viewCompany = new idViewCompanyPg();
        viewCompany.setCompanyID(String.valueOf(ownCompany.getCompanyID()));
        viewCompany.view();
        check("US14", "View own company profile", "", viewCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        designer2 = designer("des2");
        ownCompany = companyOf(designer1, "Studio One");
        signIn(designer2);
        viewCompany = new idViewCompanyPg();
        viewCompany.setCompanyID(String.valueOf(ownCompany.getCompanyID()));
        viewCompany.view();
        check("US14", "Another designer cannot view that company", Messages.COMPANY_NOT_FOUND, viewCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        ownCompany = companyOf(designer1, "Studio One");
        signIn(designer1);
        idUpdateCompanyPg updateCompany = new idUpdateCompanyPg();
        updateCompany.setCompanyID(String.valueOf(ownCompany.getCompanyID()));
        updateCompany.setName("Studio One Renamed");
        updateCompany.setDescription("Warm interiors");
        updateCompany.setLocation("Singapore");
        updateCompany.setContactEmail("hello@studioone.test");
        updateCompany.setPhone("61234567");
        updateCompany.setLogoUrl("logo.png");
        updateCompany.update();
        check("US15", "Update the company profile", Messages.COMPANY_UPDATED, updateCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        designer2 = designer("des2");
        ownCompany = companyOf(designer1, "Studio One");
        companyOf(designer2, "Studio Two");
        signIn(designer1);
        updateCompany = new idUpdateCompanyPg();
        updateCompany.setCompanyID(String.valueOf(ownCompany.getCompanyID()));
        updateCompany.setName("Studio Two");
        updateCompany.setDescription("Warm interiors");
        updateCompany.setLocation("Singapore");
        updateCompany.setContactEmail("hello@studioone.test");
        updateCompany.setPhone("61234567");
        updateCompany.setLogoUrl("logo.png");
        updateCompany.update();
        check("US15", "Update to a company name in use", Messages.COMPANY_NAME_USED, updateCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        ownCompany = companyOf(designer1, "Studio One");
        signIn(designer1);
        idDeleteCompanyPg deleteCompany = new idDeleteCompanyPg();
        deleteCompany.setCompanyID(String.valueOf(ownCompany.getCompanyID()));
        deleteCompany.delete();
        check("US16", "Delete the company profile", Messages.COMPANY_DELETED, deleteCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        signIn(designer1);
        deleteCompany = new idDeleteCompanyPg();
        deleteCompany.setCompanyID("99999");
        deleteCompany.delete();
        check("US16", "Delete a company profile that does not exist", Messages.COMPANY_NOT_FOUND, deleteCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        companyOf(designer1, "Studio One");
        signIn(designer1);
        idSearchCompanyPg searchCompany = new idSearchCompanyPg();
        searchCompany.setKeyword("Studio");
        searchCompany.search();
        check("US17", "Search company profiles", "", searchCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        signIn(designer1);
        searchCompany = new idSearchCompanyPg();
        searchCompany.setKeyword("");
        searchCompany.search();
        check("US17", "Search companies with an empty keyword", Messages.ENTER_KEYWORD, searchCompany.getMessage());

        fresh();
        designer1 = designer("des1");
        ProjectCategory category = category("Kitchen");
        signIn(designer1);
        idCreateCurrentProjectPg createCurrent = new idCreateCurrentProjectPg();
        createCurrent.setTitle("Loft Kitchen");
        createCurrent.setDescription("A bright loft kitchen");
        createCurrent.setStyle("Modern");
        createCurrent.setBudget("25000");
        createCurrent.setCategoryID(String.valueOf(category.getCategoryID()));
        createCurrent.setImageUrls("cover.png,plan.png");
        createCurrent.create();
        check("US18", "Create a current project", Messages.CURRENT_PROJECT_CREATED, createCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        signIn(designer1);
        createCurrent = new idCreateCurrentProjectPg();
        createCurrent.setTitle("Loft Kitchen");
        createCurrent.setDescription("A bright loft kitchen");
        createCurrent.setStyle("Modern");
        createCurrent.setBudget("25000");
        createCurrent.setCategoryID("99999");
        createCurrent.create();
        check("US18", "Create a project with an unknown category", Messages.CHOOSE_CATEGORY, createCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        signIn(designer1);
        idCreatePastProjectPg createPast = new idCreatePastProjectPg();
        createPast.setTitle("Riverside Flat");
        createPast.setDescription("A completed riverside flat");
        createPast.setStyle("Scandinavian");
        createPast.setBudget("18000");
        createPast.setCategoryID(String.valueOf(category.getCategoryID()));
        createPast.setCompletionDate("2025-06-30");
        createPast.setImageUrls("cover.png");
        createPast.create();
        check("US23", "Create a past project", Messages.PAST_PROJECT_CREATED, createPast.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        signIn(designer1);
        createPast = new idCreatePastProjectPg();
        createPast.setTitle("Riverside Flat");
        createPast.setDescription("A completed riverside flat");
        createPast.setStyle("Scandinavian");
        createPast.setBudget("18000");
        createPast.setCategoryID(String.valueOf(category.getCategoryID()));
        createPast.setCompletionDate("");
        createPast.create();
        check("US23", "A past project needs a completion date", Messages.CHECK_DETAILS, createPast.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        DesignProject project = project(designer1, category, "Loft Kitchen");
        signIn(designer1);
        idViewCurrentProjectPg viewCurrent = new idViewCurrentProjectPg();
        viewCurrent.setProjectID(String.valueOf(project.getProjectID()));
        viewCurrent.view();
        check("US19", "View own current project", "", viewCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        signIn(designer1);
        viewCurrent = new idViewCurrentProjectPg();
        viewCurrent.setProjectID("99999");
        viewCurrent.view();
        check("US19", "View a current project that does not exist", Messages.CURRENT_PROJECT_NOT_FOUND, viewCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        signIn(designer1);
        idUpdateCurrentProjectPg updateCurrent = new idUpdateCurrentProjectPg();
        updateCurrent.setProjectID(String.valueOf(project.getProjectID()));
        updateCurrent.setTitle("Loft Kitchen Updated");
        updateCurrent.setDescription("Updated description");
        updateCurrent.setStyle("Modern");
        updateCurrent.setBudget("30000");
        updateCurrent.setCategoryID(String.valueOf(category.getCategoryID()));
        updateCurrent.setImageUrls("cover.png");
        updateCurrent.update();
        check("US20", "Update the current project", Messages.CURRENT_PROJECT_UPDATED, updateCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        signIn(designer1);
        updateCurrent = new idUpdateCurrentProjectPg();
        updateCurrent.setProjectID(String.valueOf(project.getProjectID()));
        updateCurrent.setTitle("");
        updateCurrent.setDescription("Updated description");
        updateCurrent.setStyle("Modern");
        updateCurrent.setBudget("30000");
        updateCurrent.setCategoryID(String.valueOf(category.getCategoryID()));
        updateCurrent.update();
        check("US20", "Update with an empty title", Messages.CHECK_CHANGES, updateCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        signIn(designer1);
        idDeleteCurrentProjectPg deleteCurrent = new idDeleteCurrentProjectPg();
        deleteCurrent.setProjectID(String.valueOf(project.getProjectID()));
        deleteCurrent.delete();
        check("US21", "Delete the current project", Messages.CURRENT_PROJECT_DELETED, deleteCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project(designer1, category, "Loft Kitchen");
        signIn(designer1);
        idSearchCurrentProjectPg searchCurrent = new idSearchCurrentProjectPg();
        searchCurrent.setKeyword("Loft");
        searchCurrent.search();
        check("US22", "Search own current projects", "", searchCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        signIn(designer1);
        searchCurrent = new idSearchCurrentProjectPg();
        searchCurrent.setKeyword("");
        searchCurrent.search();
        check("US22", "Search current projects with an empty keyword", Messages.ENTER_KEYWORD, searchCurrent.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = pastProject(designer1, category, "Riverside Flat");
        signIn(designer1);
        idViewPastProjectPg viewPast = new idViewPastProjectPg();
        viewPast.setProjectID(String.valueOf(project.getProjectID()));
        viewPast.view();
        check("US24", "View own past project", "", viewPast.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = pastProject(designer1, category, "Riverside Flat");
        signIn(designer1);
        idUpdatePastProjectPg updatePast = new idUpdatePastProjectPg();
        updatePast.setProjectID(String.valueOf(project.getProjectID()));
        updatePast.setTitle("Riverside Flat Renovated");
        updatePast.setDescription("Updated description");
        updatePast.setStyle("Modern");
        updatePast.setBudget("25000");
        updatePast.setCategoryID(String.valueOf(category.getCategoryID()));
        updatePast.setCompletionDate("2025-06-30");
        updatePast.update();
        check("US25", "Update the past project", Messages.PAST_PROJECT_UPDATED, updatePast.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = pastProject(designer1, category, "Riverside Flat");
        signIn(designer1);
        idDeletePastProjectPg deletePast = new idDeletePastProjectPg();
        deletePast.setProjectID(String.valueOf(project.getProjectID()));
        deletePast.delete();
        check("US26", "Delete the past project", Messages.PAST_PROJECT_DELETED, deletePast.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        pastProject(designer1, category, "Riverside Flat");
        signIn(designer1);
        idSearchPastProjectPg searchPast = new idSearchPastProjectPg();
        searchPast.setKeyword("Riverside");
        searchPast.search();
        check("US27", "Search own past projects", "", searchPast.getMessage());

        fresh();
        designer1 = designer("des1");
        signIn(designer1);
        idViewProjectViewsPg views = new idViewProjectViewsPg();
        views.viewProjectViews();
        check("US28", "View counts when there is no project yet", Messages.NO_PROJECT_DATA, views.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        UserAccount customer1 = customer("cust1");
        signIn(customer1);
        cViewProjectPg viewProject = new cViewProjectPg();
        viewProject.setProjectID(String.valueOf(project.getProjectID()));
        viewProject.view();
        viewProject.view();
        signIn(designer1);
        views = new idViewProjectViewsPg();
        ProjectViewResult viewResult = views.viewProjectViews();
        check("US28", "View counts of a viewed project", "", views.getMessage());
        checkValue("US28", "Total views counted", 2, viewResult.totalViews());

        fresh();
        designer1 = designer("des1");
        signIn(designer1);
        idViewProjectShortlistsPg shortlists = new idViewProjectShortlistsPg();
        shortlists.viewProjectShortlists();
        check("US29", "Shortlist counts when there is no project yet", Messages.NO_PROJECT_DATA, shortlists.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        UserAccount customer2 = customer("cust2");
        signIn(customer1);
        cSaveProjectPg save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer1.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        signIn(customer2);
        save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer2.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        signIn(designer1);
        shortlists = new idViewProjectShortlistsPg();
        ProjectShortlistResult shortlistResult = shortlists.viewProjectShortlists();
        check("US29", "Shortlist counts of a saved project", "", shortlists.getMessage());
        checkValue("US29", "Total shortlists counted", 2, shortlistResult.totalShortlists());
    }

    private void sprintFour() {
        banner("Sprint 4: Customer (US30 - US34)");

        fresh();
        UserAccount designer1 = designer("des1");
        ProjectCategory category = category("Kitchen");
        DesignProject project = project(designer1, category, "Loft Kitchen");
        UserAccount customer1 = customer("cust1");
        signIn(customer1);
        cSearchProjectPg searchProjects = new cSearchProjectPg();
        searchProjects.setKeyword("Loft");
        searchProjects.search();
        check("US30", "Search published projects by keyword", "", searchProjects.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        searchProjects = new cSearchProjectPg();
        searchProjects.search();
        check("US30", "Search with no keyword and no filter", Messages.ENTER_KEYWORD_OR_FILTER, searchProjects.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        searchProjects = new cSearchProjectPg();
        searchProjects.setKeyword("zzzz");
        searchProjects.search();
        check("US30", "Search with no match", Messages.NO_PROJECTS, searchProjects.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        cViewProjectPg viewProject = new cViewProjectPg();
        viewProject.setProjectID(String.valueOf(project.getProjectID()));
        viewProject.view();
        check("US31", "View a published project", "", viewProject.getMessage());
        checkValue("US31", "Viewing increments the view count", 1, project.getViewCount());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = pastProject(designer1, category, "Riverside Flat");
        customer1 = customer("cust1");
        signIn(customer1);
        viewProject = new cViewProjectPg();
        viewProject.setProjectID(String.valueOf(project.getProjectID()));
        viewProject.view();
        check("US31", "A past project is not available to customers", Messages.PROJECT_NOT_AVAILABLE, viewProject.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        cSaveProjectPg save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer1.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        check("US32", "Save a project to the shortlist", Messages.PROJECT_SAVED, save.getMessage());
        checkValue("US32", "Saving increments the shortlist count", 1, project.getShortlistCount());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer1.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer1.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        check("US32", "Saving the same project twice", Messages.PROJECT_ALREADY_SAVED, save.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer1.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        cSearchShortlistPg searchShortlist = new cSearchShortlistPg();
        searchShortlist.setCustomerID(String.valueOf(customer1.getAccountID()));
        searchShortlist.setKeyword("Loft");
        searchShortlist.search();
        check("US33", "Search the shortlist", "", searchShortlist.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer1.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        searchShortlist = new cSearchShortlistPg();
        searchShortlist.setCustomerID(String.valueOf(customer1.getAccountID()));
        searchShortlist.setKeyword("zzzz");
        searchShortlist.search();
        check("US33", "Search the shortlist with no match", Messages.NO_SHORTLISTED_PROJECTS, searchShortlist.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        cViewShortlistPg viewShortlist = new cViewShortlistPg();
        viewShortlist.setCustomerID(String.valueOf(customer1.getAccountID()));
        viewShortlist.view();
        check("US34", "View an empty shortlist", Messages.NO_SAVED_PROJECTS, viewShortlist.getMessage());

        fresh();
        designer1 = designer("des1");
        category = category("Kitchen");
        project = project(designer1, category, "Loft Kitchen");
        customer1 = customer("cust1");
        signIn(customer1);
        save = new cSaveProjectPg();
        save.setCustomerID(String.valueOf(customer1.getAccountID()));
        save.setProjectID(String.valueOf(project.getProjectID()));
        save.save();
        viewShortlist = new cViewShortlistPg();
        viewShortlist.setCustomerID(String.valueOf(customer1.getAccountID()));
        viewShortlist.view();
        check("US34", "View a shortlist with one saved project", "", viewShortlist.getMessage());
    }

    private void sprintFive() {
        banner("Sprint 5: Platform Management (US35 - US43, US46 - US48)");

        fresh();
        UserAccount manager = manager("mgr");
        signIn(manager);
        pmCreateCategoryPg createCategory = new pmCreateCategoryPg();
        createCategory.setName("Kitchen");
        createCategory.setDescription("Kitchen renovation projects");
        createCategory.create();
        check("US35", "Create a project category", Messages.CATEGORY_CREATED, createCategory.getMessage());

        fresh();
        manager = manager("mgr");
        category("Kitchen");
        signIn(manager);
        createCategory = new pmCreateCategoryPg();
        createCategory.setName("Kitchen");
        createCategory.setDescription("Duplicate name");
        createCategory.create();
        check("US35", "Create a category with a name in use", Messages.CATEGORY_NAME_USED, createCategory.getMessage());

        fresh();
        manager = manager("mgr");
        category("Kitchen");
        category("Living Room");
        signIn(manager);
        pmViewCategoryPg viewCategories = new pmViewCategoryPg();
        viewCategories.view();
        check("US36", "View all project categories", "", viewCategories.getMessage());

        fresh();
        manager = manager("mgr");
        ProjectCategory kitchen = category("Kitchen");
        signIn(manager);
        pmUpdateCategoryPg updateCategory = new pmUpdateCategoryPg();
        updateCategory.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        updateCategory.setName("Kitchen and Dining");
        updateCategory.setDescription("Kitchen and dining projects");
        updateCategory.update();
        check("US37", "Update a project category", Messages.CATEGORY_UPDATED, updateCategory.getMessage());

        fresh();
        manager = manager("mgr");
        kitchen = category("Kitchen");
        signIn(manager);
        pmDeleteCategoryPg deleteCategory = new pmDeleteCategoryPg();
        deleteCategory.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        deleteCategory.delete();
        check("US38", "Delete an unused category", Messages.CATEGORY_DELETED, deleteCategory.getMessage());

        fresh();
        manager = manager("mgr");
        kitchen = category("Kitchen");
        UserAccount designer1 = designer("des1");
        project(designer1, kitchen, "Loft Kitchen");
        signIn(manager);
        deleteCategory = new pmDeleteCategoryPg();
        deleteCategory.setCategoryID(String.valueOf(kitchen.getCategoryID()));
        deleteCategory.delete();
        check("US38", "A category used by a project cannot be deleted", Messages.CATEGORY_IN_USE, deleteCategory.getMessage());

        fresh();
        manager = manager("mgr");
        category("Kitchen");
        signIn(manager);
        pmSearchCategoryPg searchCategory = new pmSearchCategoryPg();
        searchCategory.setKeyword("Kit");
        searchCategory.search();
        check("US39", "Search categories", "", searchCategory.getMessage());

        fresh();
        manager = manager("mgr");
        signIn(manager);
        searchCategory = new pmSearchCategoryPg();
        searchCategory.setKeyword("");
        searchCategory.search();
        check("US39", "Search categories with an empty keyword", Messages.ENTER_KEYWORD, searchCategory.getMessage());

        fresh();
        manager = manager("mgr");
        category("Kitchen");
        signIn(manager);
        searchCategory = new pmSearchCategoryPg();
        searchCategory.setKeyword("zzzz");
        searchCategory.search();
        check("US39", "Search categories with no match", Messages.NO_CATEGORIES, searchCategory.getMessage());

        fresh();
        Clock.freezeAt("2026-03-10");
        manager = manager("mgr");
        designer1 = designer("des1");
        kitchen = category("Kitchen");
        DesignProject project = project(designer1, kitchen, "Loft Kitchen");
        UserAccount customer1 = customer("cust1");
        signIn(customer1);
        cViewProjectPg viewProject = new cViewProjectPg();
        viewProject.setProjectID(String.valueOf(project.getProjectID()));
        viewProject.view();
        signIn(manager);
        pmGenerateDailyReportPg daily = new pmGenerateDailyReportPg();
        daily.setDate("2026-03-10");
        ReportResult dailyResult = daily.generateDaily();
        check("US46", "Generate the daily report", "", daily.getMessage());
        checkValue("US46", "Daily report counts the view", 1, dailyResult.views());

        fresh();
        Clock.freezeAt("2026-03-10");
        manager = manager("mgr");
        signIn(manager);
        daily = new pmGenerateDailyReportPg();
        daily.setDate("2026-01-01");
        dailyResult = daily.generateDaily();
        check("US46", "Daily report for a day without activity", Messages.NO_ACTIVITY, daily.getMessage());

        fresh();
        Clock.freezeAt("2026-03-11");
        manager = manager("mgr");
        designer1 = designer("des1");
        kitchen = category("Kitchen");
        project = project(designer1, kitchen, "Loft Kitchen");
        signIn(manager);
        pmGenerateWeeklyReportPg weekly = new pmGenerateWeeklyReportPg();
        weekly.setDate("2026-03-11");
        ReportResult weeklyResult = weekly.generateWeekly();
        check("US47", "Generate the weekly report", "", weekly.getMessage());
        checkValue("US47", "Weekly report starts on Monday", LocalDate.parse("2026-03-09"), weeklyResult.from());

        fresh();
        Clock.freezeAt("2026-03-20");
        manager = manager("mgr");
        signIn(manager);
        pmGenerateMonthlyReportPg monthly = new pmGenerateMonthlyReportPg();
        monthly.setDate("2026-03-20");
        ReportResult monthlyResult = monthly.generateMonthly();
        check("US48", "Generate the monthly report", "", monthly.getMessage());
        checkValue("US48", "Monthly report starts on the first day", LocalDate.parse("2026-03-01"), monthlyResult.from());

        fresh();
        customer1 = customer("cust1");
        signIn(customer1);
        daily = new pmGenerateDailyReportPg();
        daily.setDate("2026-03-10");
        daily.generateDaily();
        check("US46", "Only Platform Management may generate reports", Messages.NOT_AUTHORISED, daily.getMessage());
    }

    private void check(String id, String title, String expected, String actual) {
        boolean ok = Objects.equals(normalise(expected), normalise(actual));
        record(id, title, ok, ok ? show(actual) : "expected " + show(expected) + " but was " + show(actual));
    }

    private void checkValue(String id, String title, Object expected, Object actual) {
        boolean ok = Objects.equals(expected, actual);
        record(id, title, ok, ok ? String.valueOf(actual) : "expected " + expected + " but was " + actual);
    }

    private void record(String id, String title, boolean ok, String detail) {
        if (ok) {
            passed++;
            System.out.println("PASS | " + id + " | " + pad(title) + " | " + detail);
        } else {
            failed++;
            failures.add(id + " " + title + " -> " + detail);
            System.out.println("FAIL | " + id + " | " + pad(title) + " | " + detail);
        }
    }

    private void summary() {
        System.out.println();
        System.out.println("================================================================================");
        System.out.println(" cases : " + (passed + failed) + "    PASS : " + passed + "    FAIL : " + failed);
        if (failed == 0) {
            System.out.println(" result: ALL BOUNDARY CASES PASSED");
        } else {
            System.out.println(" result: " + failed + " CASE(S) FAILED");
            for (String failure : failures) {
                System.out.println("   - " + failure);
            }
        }
        System.out.println("================================================================================");
    }

    private static String normalise(String message) {
        return message == null ? "" : message;
    }

    private static String show(String message) {
        return message == null || message.isEmpty() ? "\"\"" : '\"' + message + '\"';
    }

    private static String pad(String text) {
        if (text.length() >= TITLE_WIDTH) {
            return text.substring(0, TITLE_WIDTH);
        }
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < TITLE_WIDTH) {
            builder.append(' ');
        }
        return builder.toString();
    }

    private static String repeat(char c, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(c);
        }
        return builder.toString();
    }
}
