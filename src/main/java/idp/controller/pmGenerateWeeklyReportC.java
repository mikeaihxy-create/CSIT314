package idp.controller;

import idp.dto.ReportResult;
import idp.entity.ActivityEvent;

public class pmGenerateWeeklyReportC {

    private final ActivityEvent entity = new ActivityEvent();

    public ReportResult generateWeekly(String date) {
        return entity.generateWeekly(date);
    }
}
