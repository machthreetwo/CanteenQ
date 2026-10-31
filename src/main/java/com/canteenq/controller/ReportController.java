package com.canteenq.controller;

import com.canteenq.dto.FeedbackStats;
import com.canteenq.service.ReportService;
import com.canteenq.util.FeedbackAnalyzer;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports/summary")
    public Map<String, Object> summary() {
        return reportService.summary();
    }

    @GetMapping("/reports/fortnight")
    public Map<String, Object> fortnight() {
        return reportService.fortnight();
    }

    @PostMapping("/feedback")
    public FeedbackStats feedback(@RequestBody Map<String, String> body) {
        return FeedbackAnalyzer.analyse(body.get("text"));
    }
}
