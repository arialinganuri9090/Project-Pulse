package com.projectpulse.controller;

import com.projectpulse.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/peer-eval/section/{sectionId}", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<String> sectionPeerEvalReport(
            @PathVariable Long sectionId,
            @RequestParam Long weekId) {
        return ResponseEntity.ok(reportService.generateSectionPeerEvalReport(sectionId, weekId));
    }

    @GetMapping(value = "/peer-eval/student/{studentId}", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<String> studentPeerEvalReport(
            @PathVariable Long studentId,
            @RequestParam Long startWeekId,
            @RequestParam Long endWeekId) {
        return ResponseEntity.ok(reportService.generateStudentPeerEvalReport(studentId, startWeekId, endWeekId));
    }

    @GetMapping(value = "/war/team/{teamId}", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<String> teamWarReport(
            @PathVariable Long teamId,
            @RequestParam Long weekId) {
        return ResponseEntity.ok(reportService.generateTeamWarReport(teamId, weekId));
    }

    @GetMapping(value = "/war/student/{studentId}", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<String> studentWarReport(
            @PathVariable Long studentId,
            @RequestParam Long startWeekId,
            @RequestParam Long endWeekId) {
        return ResponseEntity.ok(reportService.generateStudentWarReport(studentId, startWeekId, endWeekId));
    }
}
