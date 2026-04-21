package com.projectpulse.controller;

import com.projectpulse.dto.request.PeerEvalSubmitRequest;
import com.projectpulse.model.ActiveWeek;
import com.projectpulse.model.PeerEvaluation;
import com.projectpulse.repository.UserRepository;
import com.projectpulse.service.PeerEvalService;
import com.projectpulse.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peer-evaluations")
public class PeerEvalController {

    private final PeerEvalService peerEvalService;
    private final ReportService reportService;
    private final UserRepository userRepository;

    @Autowired
    public PeerEvalController(PeerEvalService peerEvalService, ReportService reportService,
                               UserRepository userRepository) {
        this.peerEvalService = peerEvalService;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @GetMapping("/weeks")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ActiveWeek>> getWeeks(@AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        return ResponseEntity.ok(peerEvalService.getPeerEvalWeeksForStudent(studentId));
    }

    @GetMapping("/{weekId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<PeerEvaluation>> getMyEvals(
            @PathVariable Long weekId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        return ResponseEntity.ok(peerEvalService.getEvaluationsForEvaluator(studentId, weekId));
    }

    @PostMapping("/{weekId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> submitPeerEval(
            @PathVariable Long weekId,
            @RequestBody PeerEvalSubmitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        peerEvalService.submitPeerEval(studentId, weekId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-report/{weekId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> getMyReport(
            @PathVariable Long weekId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        return ResponseEntity.ok(reportService.generateMyPeerEvalReport(studentId, weekId));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
