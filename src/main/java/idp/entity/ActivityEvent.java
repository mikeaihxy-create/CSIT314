package idp.entity;

import idp.database.Database;
import idp.dto.ReportResult;
import idp.enumeration.EventType;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import idp.util.Identifiable;
import idp.util.Messages;
import idp.util.Texts;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class ActivityEvent implements Identifiable {

    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";
    public static final String MONTHLY = "MONTHLY";

    private long eventID;
    private long actorAccountID;
    private long projectID;
    private EventType eventType;
    private LocalDateTime occurredAt;

    public ActivityEvent() {
    }

    public ActivityEvent(long actorAccountID, long projectID, EventType eventType) {
        this.actorAccountID = actorAccountID;
        this.projectID = projectID;
        this.eventType = eventType;
        this.occurredAt = Clock.now();
    }

    @Override
    public long getId() {
        return eventID;
    }

    @Override
    public void setId(long id) {
        this.eventID = id;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public long getActorAccountID() {
        return actorAccountID;
    }

    public void setActorAccountID(long actorAccountID) {
        this.actorAccountID = actorAccountID;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public void record() {
        if (occurredAt == null) {
            occurredAt = Clock.now();
        }
        Database.get().events().save(this);
    }

    public static void log(long actorAccountID, long projectID, EventType eventType) {
        if (eventType == null) {
            return;
        }
        new ActivityEvent(actorAccountID, projectID, eventType).record();
    }

    public ReportResult generateDaily(String date) {
        LocalDate day = resolveDate(date);
        return generate(DAILY, day, day);
    }

    public ReportResult generateWeekly(String date) {
        LocalDate day = resolveDate(date);
        LocalDate weekStart = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return generate(WEEKLY, weekStart, day);
    }

    public ReportResult generateMonthly(String date) {
        LocalDate day = resolveDate(date);
        LocalDate monthStart = day.withDayOfMonth(1);
        return generate(MONTHLY, monthStart, day);
    }

    private ReportResult generate(String period, LocalDate from, LocalDate to) {
        if (SessionManager.get().currentRole() != Role.PLATFORM_MANAGEMENT) {
            return new ReportResult(false, Messages.NOT_AUTHORISED, period, from, to, 0, 0, 0, 0);
        }
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);
        int newUsers = 0;
        for (UserAccount account : Database.get().accounts().all()) {
            if (within(account.getCreatedAt(), start, end)) {
                newUsers++;
            }
        }
        int newProjects = 0;
        for (DesignProject project : Database.get().projects().all()) {
            if (within(project.getCreatedAt(), start, end)) {
                newProjects++;
            }
        }
        int views = 0;
        int shortlists = 0;
        for (ActivityEvent event : Database.get().events().all()) {
            if (!within(event.getOccurredAt(), start, end)) {
                continue;
            }
            if (event.getEventType() == EventType.PROJECT_VIEW) {
                views++;
            } else if (event.getEventType() == EventType.PROJECT_SHORTLIST) {
                shortlists++;
            }
        }
        boolean empty = newUsers == 0 && newProjects == 0 && views == 0 && shortlists == 0;
        String message = empty ? Messages.NO_ACTIVITY : "";
        return new ReportResult(true, message, period, from, to, newUsers, newProjects, views, shortlists);
    }

    private boolean within(LocalDateTime moment, LocalDateTime start, LocalDateTime end) {
        return moment != null && !moment.isBefore(start) && !moment.isAfter(end);
    }

    private LocalDate resolveDate(String date) {
        if (Texts.isBlank(date)) {
            return Clock.today();
        }
        LocalDate parsed = Clock.parseDate(date.trim());
        return parsed != null ? parsed : Clock.today();
    }
}
