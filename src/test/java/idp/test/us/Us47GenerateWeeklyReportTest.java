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

public class Us47GenerateWeeklyReportTest extends IdpTestCase {

    @Test
    public void testWeeklyReportCoversTheWeekToDate() {
        Clock.freezeAt("2026-03-09");
        UserAccount manager = manager();
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject created = project(designer, category, "Riverside Kitchen");
        ActivityEvent.log(designer.getAccountID(), created.getProjectID(), EventType.PROJECT_VIEW);
        Clock.freezeAt("2026-03-11");
        ActivityEvent.log(designer.getAccountID(), created.getProjectID(), EventType.PROJECT_VIEW);
        ActivityEvent.log(designer.getAccountID(), created.getProjectID(), EventType.PROJECT_SHORTLIST);
        signIn(manager);
        pmGenerateWeeklyReportPg page = new pmGenerateWeeklyReportPg();
        page.setDate("2026-03-11");
        ReportResult result = page.generateWeekly();
        assertTrue(result.success());
        assertEquals("WEEKLY", result.period());
        assertEquals(LocalDate.parse("2026-03-09"), result.from());
        assertEquals(LocalDate.parse("2026-03-11"), result.to());
        assertEquals(2, result.newUsers());
        assertEquals(1, result.newProjects());
        assertEquals(2, result.views());
        assertEquals(1, result.shortlists());
    }

    @Test
    public void testWeeklyReportStartsOnMonday() {
        Clock.freezeAt("2026-03-12");
        UserAccount manager = manager();
        signIn(manager);
        pmGenerateWeeklyReportPg page = new pmGenerateWeeklyReportPg();
        page.setDate("2026-03-12");
        ReportResult result = page.generateWeekly();
        assertTrue(result.success());
        assertEquals(LocalDate.parse("2026-03-09"), result.from());
        assertEquals(LocalDate.parse("2026-03-12"), result.to());
    }

    @Test
    public void testWeeklyReportFailsWhenActorIsNotPlatformManagement() {
        signIn(customer("alice"));
        pmGenerateWeeklyReportPg page = new pmGenerateWeeklyReportPg();
        page.setDate("2026-03-11");
        ReportResult result = page.generateWeekly();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }
}
