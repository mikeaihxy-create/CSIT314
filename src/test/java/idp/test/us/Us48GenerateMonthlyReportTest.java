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

public class Us48GenerateMonthlyReportTest extends IdpTestCase {

    @Test
    public void testMonthlyReportCoversTheMonthToDate() {
        Clock.freezeAt("2026-03-02");
        UserAccount manager = manager();
        UserAccount designer = designer();
        ProjectCategory category = category("Kitchen");
        DesignProject created = project(designer, category, "Riverside Kitchen");
        ActivityEvent.log(designer.getAccountID(), created.getProjectID(), EventType.PROJECT_VIEW);
        Clock.freezeAt("2026-03-20");
        ActivityEvent.log(designer.getAccountID(), created.getProjectID(), EventType.PROJECT_VIEW);
        signIn(manager);
        pmGenerateMonthlyReportPg page = new pmGenerateMonthlyReportPg();
        page.setDate("2026-03-20");
        ReportResult result = page.generateMonthly();
        assertTrue(result.success());
        assertEquals("MONTHLY", result.period());
        assertEquals(LocalDate.parse("2026-03-01"), result.from());
        assertEquals(LocalDate.parse("2026-03-20"), result.to());
        assertEquals(2, result.newUsers());
        assertEquals(1, result.newProjects());
        assertEquals(2, result.views());
    }

    @Test
    public void testMonthlyReportStartsOnTheFirstDay() {
        Clock.freezeAt("2026-03-15");
        UserAccount manager = manager();
        signIn(manager);
        pmGenerateMonthlyReportPg page = new pmGenerateMonthlyReportPg();
        page.setDate("2026-03-15");
        ReportResult result = page.generateMonthly();
        assertTrue(result.success());
        assertEquals(LocalDate.parse("2026-03-01"), result.from());
        assertEquals(LocalDate.parse("2026-03-15"), result.to());
    }

    @Test
    public void testMonthlyReportFailsWhenActorIsNotPlatformManagement() {
        signIn(designer());
        pmGenerateMonthlyReportPg page = new pmGenerateMonthlyReportPg();
        page.setDate("2026-03-20");
        ReportResult result = page.generateMonthly();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
    }
}
