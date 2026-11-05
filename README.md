# Interior Design Platform (IDP)

CSIT314 Software Development Methodologies - group project implementation.

The platform helps a renovation customer find an interior designer. A customer searches the
portfolio of current and past interior design projects, opens a project, and saves the ones worth
keeping to a shortlist. A designer maintains a company profile and that portfolio, and sees how
often the work was viewed and shortlisted. Platform Management maintains the project categories and
reads the daily, weekly and monthly activity reports. A User Admin maintains accounts and profiles.

The code follows the **boundary - control - entity** framework described in the use case report, and
was written test first: each user story has a JUnit test class that fails before the story is
implemented. Every design decision below is traceable to the use case report, the final class
diagram and the final ERD in the project documentation.

---

## 1. What is in the box

```
IDP/
  src/main/java/idp/
    boundary/       42 screens  - one per use case, holds the form, shows the message
    controller/     42 control classes - one per use case, delegates to the entity
    entity/         8 entities  - UserAccount, UserProfile, CompanyProfile, DesignProject,
                                  ProjectImage, ProjectCategory, Favorite, ActivityEvent
    enumeration/    6 enums     - Role, AccountStatus, CompanyStatus, CategoryStatus,
                                  ProjectStage, EventType
    dto/            30 request/response records passed between the three layers
    database/       in-memory tables (Table, Database) used by the entities
    session/        SessionManager - the one place that knows who is signed in
    util/           Clock, Texts, Passwords, Messages, Forms, Identifiable
  src/test/java/idp/
    test/           IdpTestCase (fixture), AllTests (suite), TestRunner (driver)
    test/us/        42 user-story test classes
    testdata/       TestDataLoader + TestDataLoaderTest
  testdata/         eight CSV files, the sample data (accounts, profiles, companies, ...)
  database/         schema.sql - the ERD as DDL, with keys and CHECK constraints
  lib/              junit-4.13.2.jar, hamcrest-core-1.3.jar
  _gen/             the small Python generators that wrote the screens, the controls and the suite
                    (development aid only - it is not compiled and not part of the build)
  pom.xml           optional Maven build (mvn test)
```

### The three layers, and why they are split this way

* **Boundary** (`*Pg`) is a screen. It owns the form fields, has one action method per use case, and
  one `display...Result` method that turns the returned DTO into the message the user reads. It
  validates only what a screen must validate - whether a required box is empty - and never business
  rules.
* **Control** (`*C`) is a genuine pass-through, as the brief requires: it holds one reference to the
  entity and returns the entity's answer unchanged.

  ```java
  public class uaCreateAccountC {
      private final UserAccount entity = new UserAccount();

      public CreateAccountResult create(CreateAccountDetail detail) {
          return entity.create(detail);
      }
  }
  ```

  There is no validation, no extra lookup and no extra state in a control class.
* **Entity** owns the rules and the data. Every check from the use case report - role check,
  required fields, uniqueness, status, ownership, "category in use", the report date ranges - lives
  in the entity method that the use case names.

The benefit is visible in the tests: a test drives the boundary, and the assertion on the message
proves the entity made the decision, while the assertion on the database proves the decision was
stored.

---

## 2. Build and run

The project builds with a JDK 17 and nothing else. `lib/` already contains the two JUnit jars, so
no download is needed.

```bash
# from the IDP directory
javac -encoding UTF-8 -d build/classes -cp "lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" \
      $(find src/main/java src/test/java -name "*.java")

# run the whole suite (the TestSuite slide)
java -cp "build/classes;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" \
     org.junit.runner.JUnitCore idp.test.AllTests

# or run the driver, which prints the slide's line
java -cp "build/classes;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" idp.test.TestRunner
```

`TestRunner` prints exactly what the TDD lecture shows:

```
Is the test successful?: true
Tests run: 191, failures: 0, ignored: 0
```

With Maven installed: `mvn test` (the pom points surefire at the suite and passes the test-data
directory through `idp.testdata`).

---

## 3. Tests

191 tests in total: 184 use-case tests across 42 classes, plus 7 tests that check the
sample data itself.

* One class per user story, named after it (`Us11LoginTest`, `Us32SaveInteriorDesignProjectTest`).
* One `@Test` per normal flow and per alternative flow, so a failure names the flow that broke.
* Multi-condition assertions, as required: a login test signs in with the right password and the
  wrong username, with the right username and the wrong password, by email, and by username, and
  asserts that the first three fail with `Email, username, or password is wrong.` while the last
  two succeed. Nothing passes by accident.
* Each test asserts the message **and** the resulting state, e.g. suspending an account asserts
  `ACCOUNT_SUSPENDED`, the stored status `SUSPENDED`, and that the suspended user's session is gone.
* `IdpTestCase` resets the database, the session and the clock before and after every test, so tests
  are independent and the report tests can freeze the date to a known Monday.

---

## 4. Test data

`testdata/` holds eight CSV files, one per table of the ERD, generated deterministically (400
accounts, 400 profiles, 100 companies, 100 categories, 300 projects, 400 images, 400 favorites and
2000 activity events - well above the "100 records per datatype" the brief asks for).
`TestDataLoader` loads them into the in-memory database and `TestDataLoaderTest` proves the row
counts, the foreign keys and that a loaded account can actually log in. See `testdata/README.md`.

---

## 5. Use case traceability

Every use case of the report, its screen, its control class, the entity method that implements it,
and the test class that proves it:

| # | Use case (Taiga) | Actor | Boundary | Control | Entity method | Test class |
|---|---|---|---|---|---|---|
| 1 | Create user account | User Admin | `uaCreateAccountPg` | `uaCreateAccountC` | `UserAccount.create` | `Us01CreateUserAccountTest` |
| 2 | View user account | User Admin | `uaViewAccountPg` | `uaViewAccountC` | `UserAccount.view` | `Us02ViewUserAccountTest` |
| 3 | Update user account | User Admin | `uaUpdateAccountPg` | `uaUpdateAccountC` | `UserAccount.update` | `Us03UpdateUserAccountTest` |
| 4 | Suspend user account | User Admin | `uaSuspendAccountPg` | `uaSuspendAccountC` | `UserAccount.suspend` | `Us04SuspendUserAccountTest` |
| 5 | Search user accounts | User Admin | `uaSearchAccountPg` | `uaSearchAccountC` | `UserAccount.search` | `Us05SearchUserAccountsTest` |
| 6 | Create user profile | User Admin | `uaCreateProfilePg` | `uaCreateProfileC` | `UserProfile.create` | `Us06CreateUserProfileTest` |
| 7 | View user profile | User Admin | `uaViewProfilePg` | `uaViewProfileC` | `UserProfile.view` | `Us07ViewUserProfileTest` |
| 8 | Update user profile | User Admin | `uaUpdateProfilePg` | `uaUpdateProfileC` | `UserProfile.update` | `Us08UpdateUserProfileTest` |
| 9 | Suspend user profile | User Admin | `uaSuspendProfilePg` | `uaSuspendProfileC` | `UserProfile.suspend` | `Us09SuspendUserProfileTest` |
| 10 | Search user profiles | User Admin | `uaSearchProfilePg` | `uaSearchProfileC` | `UserProfile.search` | `Us10SearchUserProfilesTest` |
| 11 | Login | All four roles | `loginPg` | `loginC` | `UserAccount.login` | `Us11LoginTest` |
| 12 | Logout | All four roles | `logoutPg` | `logoutC` | `UserAccount.logout` | `Us12LogoutTest` |
| 13 | Create company profile | Interior Designer | `idCreateCompanyPg` | `idCreateCompanyC` | `CompanyProfile.create` | `Us13CreateCompanyProfileTest` |
| 14 | View company profile | Interior Designer | `idViewCompanyPg` | `idViewCompanyC` | `CompanyProfile.view` | `Us14ViewCompanyProfileTest` |
| 15 | Update company profile | Interior Designer | `idUpdateCompanyPg` | `idUpdateCompanyC` | `CompanyProfile.update` | `Us15UpdateCompanyProfileTest` |
| 16 | Delete company profile | Interior Designer | `idDeleteCompanyPg` | `idDeleteCompanyC` | `CompanyProfile.delete` | `Us16DeleteCompanyProfileTest` |
| 17 | Search company profiles | Interior Designer | `idSearchCompanyPg` | `idSearchCompanyC` | `CompanyProfile.search` | `Us17SearchCompanyProfilesTest` |
| 20 | Create current project | Interior Designer | `idCreateCurrentProjectPg` | `idCreateCurrentProjectC` | `DesignProject.createCurrent` | `Us20CreateCurrentProjectTest` |
| 21 | View current project | Interior Designer | `idViewCurrentProjectPg` | `idViewCurrentProjectC` | `DesignProject.viewCurrent` | `Us21ViewCurrentProjectTest` |
| 22 | Update current project | Interior Designer | `idUpdateCurrentProjectPg` | `idUpdateCurrentProjectC` | `DesignProject.updateCurrent` | `Us22UpdateCurrentProjectTest` |
| 23 | Delete current project | Interior Designer | `idDeleteCurrentProjectPg` | `idDeleteCurrentProjectC` | `DesignProject.deleteCurrent` | `Us23DeleteCurrentProjectTest` |
| 24 | Search current projects | Interior Designer | `idSearchCurrentProjectPg` | `idSearchCurrentProjectC` | `DesignProject.searchCurrent` | `Us24SearchCurrentProjectsTest` |
| 25 | Create past project | Interior Designer | `idCreatePastProjectPg` | `idCreatePastProjectC` | `DesignProject.createPast` | `Us25CreatePastProjectTest` |
| 26 | View past project | Interior Designer | `idViewPastProjectPg` | `idViewPastProjectC` | `DesignProject.viewPast` | `Us26ViewPastProjectTest` |
| 27 | Update past project | Interior Designer | `idUpdatePastProjectPg` | `idUpdatePastProjectC` | `DesignProject.updatePast` | `Us27UpdatePastProjectTest` |
| 28 | Delete past project | Interior Designer | `idDeletePastProjectPg` | `idDeletePastProjectC` | `DesignProject.deletePast` | `Us28DeletePastProjectTest` |
| 29 | Search past projects | Interior Designer | `idSearchPastProjectPg` | `idSearchPastProjectC` | `DesignProject.searchPast` | `Us29SearchPastProjectsTest` |
| 30 | Search interior design projects | Customer | `cSearchProjectPg` | `cSearchProjectC` | `DesignProject.search` | `Us30SearchInteriorDesignProjectsTest` |
| 31 | View interior design project | Customer | `cViewProjectPg` | `cViewProjectC` | `DesignProject.viewPublished` | `Us31ViewInteriorDesignProjectTest` |
| 32 | Save interior design project | Customer | `cSaveProjectPg` | `cSaveProjectC` | `Favorite.save` | `Us32SaveInteriorDesignProjectTest` |
| 33 | Search shortlisted projects | Customer | `cSearchShortlistPg` | `cSearchShortlistC` | `Favorite.search` | `Us33SearchShortlistedProjectsTest` |
| 34 | View shortlisted projects | Customer | `cViewShortlistPg` | `cViewShortlistC` | `Favorite.view` | `Us34ViewShortlistedProjectsTest` |
| 37 | View project view counts | Interior Designer | `idViewProjectViewsPg` | `idViewProjectViewsC` | `DesignProject.viewProjectViews` | `Us37ViewProjectViewCountsTest` |
| 38 | View project shortlist counts | Interior Designer | `idViewProjectShortlistsPg` | `idViewProjectShortlistsC` | `DesignProject.viewProjectShortlists` | `Us38ViewProjectShortlistCountsTest` |
| 39 | Create project category | Platform Management | `pmCreateCategoryPg` | `pmCreateCategoryC` | `ProjectCategory.create` | `Us39CreateProjectCategoryTest` |
| 40 | View project categories | Platform Management | `pmViewCategoryPg` | `pmViewCategoryC` | `ProjectCategory.view` | `Us40ViewProjectCategoriesTest` |
| 41 | Update project category | Platform Management | `pmUpdateCategoryPg` | `pmUpdateCategoryC` | `ProjectCategory.update` | `Us41UpdateProjectCategoryTest` |
| 42 | Delete project category | Platform Management | `pmDeleteCategoryPg` | `pmDeleteCategoryC` | `ProjectCategory.delete` | `Us42DeleteProjectCategoryTest` |
| 43 | Search project categories | Platform Management | `pmSearchCategoryPg` | `pmSearchCategoryC` | `ProjectCategory.search` | `Us43SearchProjectCategoriesTest` |
| 46 | Generate daily report | Platform Management | `pmGenerateDailyReportPg` | `pmGenerateDailyReportC` | `ActivityEvent.generateDaily` | `Us46GenerateDailyReportTest` |
| 47 | Generate weekly report | Platform Management | `pmGenerateWeeklyReportPg` | `pmGenerateWeeklyReportC` | `ActivityEvent.generateWeekly` | `Us47GenerateWeeklyReportTest` |
| 48 | Generate monthly report | Platform Management | `pmGenerateMonthlyReportPg` | `pmGenerateMonthlyReportC` | `ActivityEvent.generateMonthly` | `Us48GenerateMonthlyReportTest` |

The numbering is the Taiga story number, so the table doubles as the sprint backlog mapping.

---

## 6. Notes on the design

* **Sessions.** `SessionManager` keeps the signed-in account, so an entity can ask "who is calling?"
  without the boundary passing the caller around. Suspending an account removes its sessions, which
  is why a suspended user is thrown back to the login screen.
* **Messages.** Every user-facing sentence lives in `idp.util.Messages` and is copied verbatim from
  the use case report, so the tests and the report cannot drift apart.
* **Clock.** `idp.util.Clock` is the only source of "now", which lets the daily, weekly and monthly
  report tests pick a fixed date instead of hoping the suite does not run at midnight on a Sunday.
* **Past and current projects** are one entity with a `ProjectStage`, because the final class
  diagram models them that way; the two sets of use cases differ in validation (a past project needs
  a completion date), not in structure.
* **Shortlist** in the report's BCE diagrams is `Favorite` in the final class diagram and ERD; the
  code follows the final diagram.
* **Counters.** Opening a project increments `viewCount` and saving it increments
  `shortlistCount`, and both record an `ActivityEvent`, which is exactly what the designer's
  interest screen and the platform reports read.

---

## 7. Continuous integration

The GitHub Actions pipeline lives at `.github/workflows/ci.yml` (same folder as `src`, `lib` and
`testdata`, so no second project is needed). It runs on every push and pull request to `main` or
`master`, and can also be started by hand from the **Actions** tab (`Run workflow`).

The job has nine steps: checkout, set up Temurin JDK 17, locate the project root (accepts `.`,
`java code/` or `IDP/`), compile every source with `javac` against the bundled jars in `lib/`,
run `idp.test.AllTests` (191 tests), run `idp.test.TestRunner`, run `idp.test.BoundaryDemo`
(97 boundary cases), write a job summary, and upload the three log files as the `idp-test-logs`
artifact. Any failing test turns the run red; nothing is deployed because there is no server to
deploy.

The only difference between the CI commands and the local ones is the classpath separator:
`:` on Linux, `;` on Windows.
