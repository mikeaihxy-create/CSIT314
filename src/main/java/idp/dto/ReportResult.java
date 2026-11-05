package idp.dto;

import java.time.LocalDate;

public record ReportResult(boolean success, String message, String period, LocalDate from, LocalDate to, int newUsers, int newProjects, int views, int shortlists) implements Result {
}
