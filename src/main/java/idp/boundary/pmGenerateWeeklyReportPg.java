package idp.boundary;

import idp.controller.pmGenerateWeeklyReportC;
import idp.dto.ReportResult;
import idp.util.Messages;

public class pmGenerateWeeklyReportPg {

    private final pmGenerateWeeklyReportC controller = new pmGenerateWeeklyReportC();

    private String date;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ReportResult generateWeekly() {
        ReportResult result = controller.generateWeekly(date);
        return displayReportResult(result);
    }

    public ReportResult displayReportResult(ReportResult result) {
        this.message = result.message();
        return result;
    }
}
