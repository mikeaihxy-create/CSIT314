package idp.boundary;

import idp.controller.pmGenerateDailyReportC;
import idp.dto.ReportResult;
import idp.util.Messages;

public class pmGenerateDailyReportPg {

    private final pmGenerateDailyReportC controller = new pmGenerateDailyReportC();

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

    public ReportResult generateDaily() {
        ReportResult result = controller.generateDaily(date);
        return displayReportResult(result);
    }

    public ReportResult displayReportResult(ReportResult result) {
        this.message = result.message();
        return result;
    }
}
