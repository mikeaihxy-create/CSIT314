package idp.controller;

import idp.dto.ReportResult;
import idp.entity.ActivityEvent;

public class pmGenerateMonthlyReportC {

    private final ActivityEvent entity = new ActivityEvent();

    public ReportResult generateMonthly(String date) {
        return entity.generateMonthly(date);
    }
}
