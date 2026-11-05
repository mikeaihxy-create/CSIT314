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

public class Us46GenerateDailyReportTest extends IdpTestCase {

    @Test
    public void testDailyReport() {
        Clock.freezeAt("2026-03-09");
        UserAccount manager = manager();
        Clock.freezeAt("2026-03-10");
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject created = project(designer, category, "Riverside Kitchen");
        UserAccount alice = customer("alice");
        signIn(alice);
        cViewProjectPg view = new cViewProjectPg();
        view.setProjectID(String.valueOf(created.getProjectID()));
        assertNotNull(view.view());
        ActivityEvent.log(alice.getAccountID(), created.getProjectID(), EventType.PROJECT_SHORTLIST);
        signIn(manager);
        pmGenerateDailyReportPg page = new pmGenerateDailyReportPg();
        page.setDate("2026-03-10");
        ReportResult result = page.generateDaily();
        assertTrue(result.success());
        assertEquals("DAILY", result.period());
        assertEquals(LocalDate.parse("2026-03-10"), result.from());
        assertEquals(LocalDate.parse("2026-03-10"), result.to());
        assertEquals(2, result.newUsers());
        assertEquals(1, result.newProjects());
        assertEquals(1, result.views());
        assertEquals(1, result.shortlists());
        assertEquals("", page.getMessage());
    }

    @Test
    public void testDailyReportIgnoresOtherDays() {
        Clock.freezeAt("2026-03-09");
        UserAccount manager = manager();
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject created = project(designer, category, "Riverside Kitchen");
        ActivityEvent.log(designer.getAccountID(), created.getProjectID(), EventType.PROJECT_VIEW);
        signIn(manager);
        pmGenerateDailyReportPg page = new pmGenerateDailyReportPg();
        page.setDate("2026-03-10");
        ReportResult result = page.generateDaily();
        assertTrue(result.success());
        assertEquals(0, result.newUsers());
        assertEquals(0, result.newProjects());
        assertEquals(0, result.views());
        assertEquals(0, result.shortlists());
        assertEquals(Messages.NO_ACTIVITY, result.message());
    }

    @Test
    public void testDailyReportDefaultsToToday() {
        Clock.freezeAt("2026-03-10");
        UserAccount manager = manager();
        signIn(manager);
        pmGenerateDailyReportPg page = new pmGenerateDailyReportPg();
        page.setDate("");
        ReportResult result = page.generateDaily();
        assertTrue(result.success());
        assertEquals(LocalDate.parse("2026-03-10"), result.from());
        assertEquals(LocalDate.parse("2026-03-10"), result.to());
        assertEquals(1, result.newUsers());
    }

    @Test
    public void testDailyReportFailsWhenActorIsNotPlatformManagement() {
        signIn(designer());
        pmGenerateDailyReportPg page = new pmGenerateDailyReportPg();
        page.setDate("2026-03-10");
        ReportResult result = page.generateDaily();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertEquals(0, result.views());
    }
}
