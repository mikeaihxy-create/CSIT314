package idp.controller;

import idp.dto.ReportResult;
import idp.entity.ActivityEvent;

public class pmGenerateDailyReportC {

    private final ActivityEvent entity = new ActivityEvent();

    public ReportResult generateDaily(String date) {
        return entity.generateDaily(date);
    }
}
