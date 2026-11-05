package idp.test;

import idp.test.us.*;
import idp.testdata.TestDataLoaderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Us01CreateUserAccountTest.class,
        Us02ViewUserAccountTest.class,
        Us03UpdateUserAccountTest.class,
        Us04SuspendUserAccountTest.class,
        Us05SearchUserAccountsTest.class,
        Us06CreateUserProfileTest.class,
        Us07ViewUserProfileTest.class,
        Us08UpdateUserProfileTest.class,
        Us09SuspendUserProfileTest.class,
        Us10SearchUserProfilesTest.class,
        Us11LoginTest.class,
        Us12LogoutTest.class,
        Us13CreateCompanyProfileTest.class,
        Us14ViewCompanyProfileTest.class,
        Us15UpdateCompanyProfileTest.class,
        Us16DeleteCompanyProfileTest.class,
        Us17SearchCompanyProfilesTest.class,
        Us20CreateCurrentProjectTest.class,
        Us21ViewCurrentProjectTest.class,
        Us22UpdateCurrentProjectTest.class,
        Us23DeleteCurrentProjectTest.class,
        Us24SearchCurrentProjectsTest.class,
        Us25CreatePastProjectTest.class,
        Us26ViewPastProjectTest.class,
        Us27UpdatePastProjectTest.class,
        Us28DeletePastProjectTest.class,
        Us29SearchPastProjectsTest.class,
        Us30SearchInteriorDesignProjectsTest.class,
        Us31ViewInteriorDesignProjectTest.class,
        Us32SaveInteriorDesignProjectTest.class,
        Us33SearchShortlistedProjectsTest.class,
        Us34ViewShortlistedProjectsTest.class,
        Us37ViewProjectViewCountsTest.class,
        Us38ViewProjectShortlistCountsTest.class,
        Us39CreateProjectCategoryTest.class,
        Us40ViewProjectCategoriesTest.class,
        Us41UpdateProjectCategoryTest.class,
        Us42DeleteProjectCategoryTest.class,
        Us43SearchProjectCategoriesTest.class,
        Us46GenerateDailyReportTest.class,
        Us47GenerateWeeklyReportTest.class,
        Us48GenerateMonthlyReportTest.class,
        TestDataLoaderTest.class
})
public class AllTests {
}
