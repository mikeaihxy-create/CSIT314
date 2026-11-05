package idp.boundary;

import idp.controller.pmGenerateMonthlyReportC;
import idp.dto.ReportResult;
import idp.util.Messages;

public class pmGenerateMonthlyReportPg {

    private final pmGenerateMonthlyReportC controller = new pmGenerateMonthlyReportC();

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

    public ReportResult generateMonthly() {
        ReportResult result = controller.generateMonthly(date);
        return displayReportResult(result);
    }

    public ReportResult displayReportResult(ReportResult result) {
        this.message = result.message();
        return result;
    }
}
