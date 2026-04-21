package com.projectpulse.service;

import com.projectpulse.model.*;
import com.projectpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final PeerEvaluationRepository peerEvalRepository;
    private final WarRepository warRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportService(PeerEvaluationRepository peerEvalRepository, WarRepository warRepository,
                         ActiveWeekRepository activeWeekRepository, TeamRepository teamRepository,
                         UserRepository userRepository) {
        this.peerEvalRepository = peerEvalRepository;
        this.warRepository = warRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public String generateSectionPeerEvalReport(Long sectionId, Long weekId) {
        ActiveWeek week = activeWeekRepository.findById(weekId)
                .orElseThrow(() -> new RuntimeException("Week not found"));

        List<Team> teams = teamRepository.search(sectionId, null);
        Set<User> allStudents = new LinkedHashSet<>();
        for (Team t : teams) allStudents.addAll(t.getStudents());

        List<Long> studentIds = allStudents.stream().map(User::getId).collect(Collectors.toList());
        List<Long> submitted = peerEvalRepository.findSubmittedEvaluatorIdsByWeekIdAndStudentIds(weekId, studentIds);

        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>Peer Evaluation Report</h2>");
        html.append("<h3>Week: ").append(week.getWeekStart()).append(" - ").append(week.getWeekEnd()).append("</h3>");
        html.append("<table border='1' cellpadding='8'><thead><tr>")
            .append("<th>Student</th><th>Grade</th><th>Commented By</th><th>Public Comments</th><th>Private Comments</th></tr></thead><tbody>");

        for (User student : allStudents) {
            List<PeerEvaluation> evals = peerEvalRepository.findByEvaluateeIdAndActiveWeekId(student.getId(), weekId);
            double grade = computeGrade(evals);
            boolean firstRow = true;
            if (evals.isEmpty()) {
                html.append("<tr><td>").append(student.getFirstName()).append(" ").append(student.getLastName())
                    .append("</td><td>N/A</td><td colspan='3'>No evaluations</td></tr>");
            } else {
                for (PeerEvaluation eval : evals) {
                    html.append("<tr>");
                    if (firstRow) {
                        html.append("<td rowspan='").append(evals.size()).append("'>")
                            .append(student.getFirstName()).append(" ").append(student.getLastName()).append("</td>")
                            .append("<td rowspan='").append(evals.size()).append("'>").append(String.format("%.1f", grade)).append("</td>");
                        firstRow = false;
                    }
                    String evaluatorName = eval.getEvaluator().getFirstName() + " " + eval.getEvaluator().getLastName();
                    html.append("<td>").append(evaluatorName).append("</td>")
                        .append("<td>").append(eval.getPublicComment() != null ? eval.getPublicComment() : "").append("</td>")
                        .append("<td>").append(eval.getPrivateComment() != null ? eval.getPrivateComment() : "").append("</td>")
                        .append("</tr>");
                }
            }
        }

        html.append("</tbody></table>");
        html.append("<h4>Did not submit:</h4><ul>");
        for (User student : allStudents) {
            if (!submitted.contains(student.getId())) {
                html.append("<li>").append(student.getFirstName()).append(" ").append(student.getLastName()).append("</li>");
            }
        }
        html.append("</ul></body></html>");
        return html.toString();
    }

    public String generateStudentPeerEvalReport(Long studentId, Long startWeekId, Long endWeekId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<ActiveWeek> allWeeks = activeWeekRepository.findAll().stream()
                .filter(w -> w.getId() >= startWeekId && w.getId() <= endWeekId)
                .sorted(Comparator.comparing(ActiveWeek::getWeekStart))
                .collect(Collectors.toList());

        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>Peer Evaluation Report: ")
            .append(student.getFirstName()).append(" ").append(student.getLastName()).append("</h2>");
        html.append("<table border='1' cellpadding='8'><thead><tr>")
            .append("<th>Week</th><th>Grade</th><th>Commented By</th><th>Public Comments</th><th>Private Comments</th></tr></thead><tbody>");

        for (ActiveWeek week : allWeeks) {
            List<PeerEvaluation> evals = peerEvalRepository.findByEvaluateeIdAndActiveWeekId(studentId, week.getId());
            double grade = computeGrade(evals);
            String weekLabel = week.getWeekStart() + " - " + week.getWeekEnd();
            boolean firstRow = true;
            if (evals.isEmpty()) {
                html.append("<tr><td>").append(weekLabel).append("</td><td>N/A</td><td colspan='3'>No evaluations</td></tr>");
            } else {
                for (PeerEvaluation eval : evals) {
                    html.append("<tr>");
                    if (firstRow) {
                        html.append("<td rowspan='").append(evals.size()).append("'>").append(weekLabel).append("</td>")
                            .append("<td rowspan='").append(evals.size()).append("'>").append(String.format("%.1f", grade)).append("</td>");
                        firstRow = false;
                    }
                    html.append("<td>").append(eval.getEvaluator().getFirstName()).append(" ").append(eval.getEvaluator().getLastName()).append("</td>")
                        .append("<td>").append(eval.getPublicComment() != null ? eval.getPublicComment() : "").append("</td>")
                        .append("<td>").append(eval.getPrivateComment() != null ? eval.getPrivateComment() : "").append("</td>")
                        .append("</tr>");
                }
            }
        }
        html.append("</tbody></table></body></html>");
        return html.toString();
    }

    public String generateTeamWarReport(Long teamId, Long weekId) {
        ActiveWeek week = activeWeekRepository.findById(weekId)
                .orElseThrow(() -> new RuntimeException("Week not found"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>WAR Report - Team: ").append(team.getName()).append("</h2>");
        html.append("<h3>Week: ").append(week.getWeekStart()).append(" - ").append(week.getWeekEnd()).append("</h3>");
        html.append("<table border='1' cellpadding='8'><thead><tr>")
            .append("<th>Student</th><th>Activity Category</th><th>Planned Activity</th>")
            .append("<th>Description</th><th>Planned Hours</th><th>Actual Hours</th><th>Status</th></tr></thead><tbody>");

        List<User> students = new ArrayList<>(team.getStudents());
        students.sort(Comparator.comparing(User::getLastName));
        Set<Long> submitted = new HashSet<>();

        for (User student : students) {
            War war = warRepository.findByStudentIdAndActiveWeekId(student.getId(), weekId).orElse(null);
            if (war == null || war.getActivities().isEmpty()) {
                html.append("<tr><td>").append(student.getFirstName()).append(" ").append(student.getLastName())
                    .append("</td><td colspan='6'>No WAR submitted</td></tr>");
            } else {
                submitted.add(student.getId());
                boolean first = true;
                for (WarActivity act : war.getActivities()) {
                    html.append("<tr>");
                    if (first) {
                        html.append("<td rowspan='").append(war.getActivities().size()).append("'>")
                            .append(student.getFirstName()).append(" ").append(student.getLastName()).append("</td>");
                        first = false;
                    }
                    html.append("<td>").append(act.getCategory()).append("</td>")
                        .append("<td>").append(act.getActivity()).append("</td>")
                        .append("<td>").append(act.getDescription() != null ? act.getDescription() : "").append("</td>")
                        .append("<td>").append(act.getPlannedHours() != null ? act.getPlannedHours() : "").append("</td>")
                        .append("<td>").append(act.getActualHours() != null ? act.getActualHours() : "").append("</td>")
                        .append("<td>").append(act.getStatus()).append("</td>")
                        .append("</tr>");
                }
            }
        }
        html.append("</tbody></table>");
        html.append("<h4>Did not submit WAR:</h4><ul>");
        for (User student : students) {
            if (!submitted.contains(student.getId())) {
                html.append("<li>").append(student.getFirstName()).append(" ").append(student.getLastName()).append("</li>");
            }
        }
        html.append("</ul></body></html>");
        return html.toString();
    }

    public String generateStudentWarReport(Long studentId, Long startWeekId, Long endWeekId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<ActiveWeek> weeks = activeWeekRepository.findAll().stream()
                .filter(w -> w.getId() >= startWeekId && w.getId() <= endWeekId)
                .sorted(Comparator.comparing(ActiveWeek::getWeekStart))
                .collect(Collectors.toList());

        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>WAR Report: ").append(student.getFirstName()).append(" ").append(student.getLastName()).append("</h2>");

        for (ActiveWeek week : weeks) {
            html.append("<h3>Active week: ").append(week.getWeekStart()).append(" to ").append(week.getWeekEnd()).append("</h3>");
            War war = warRepository.findByStudentIdAndActiveWeekId(studentId, week.getId()).orElse(null);
            if (war == null || war.getActivities().isEmpty()) {
                html.append("<p>No WAR submitted.</p>");
            } else {
                html.append("<table border='1' cellpadding='8'><thead><tr>")
                    .append("<th>Activity Category</th><th>Planned Activity</th><th>Description</th>")
                    .append("<th>Planned Hours</th><th>Actual Hours</th><th>Status</th></tr></thead><tbody>");
                for (WarActivity act : war.getActivities()) {
                    html.append("<tr>")
                        .append("<td>").append(act.getCategory()).append("</td>")
                        .append("<td>").append(act.getActivity()).append("</td>")
                        .append("<td>").append(act.getDescription() != null ? act.getDescription() : "").append("</td>")
                        .append("<td>").append(act.getPlannedHours() != null ? act.getPlannedHours() : "").append("</td>")
                        .append("<td>").append(act.getActualHours() != null ? act.getActualHours() : "").append("</td>")
                        .append("<td>").append(act.getStatus()).append("</td>")
                        .append("</tr>");
                }
                html.append("</tbody></table>");
            }
        }
        html.append("</body></html>");
        return html.toString();
    }

    public String generateMyPeerEvalReport(Long studentId, Long weekId) {
        User student = userRepository.findById(studentId).orElseThrow();
        ActiveWeek week = activeWeekRepository.findById(weekId).orElseThrow();
        List<PeerEvaluation> evals = peerEvalRepository.findByEvaluateeIdAndActiveWeekId(studentId, weekId);

        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>My Peer Evaluation Report</h2>");
        html.append("<h3>Week: ").append(week.getWeekStart()).append(" - ").append(week.getWeekEnd()).append("</h3>");

        if (evals.isEmpty()) {
            html.append("<p>No peer evaluations received for this week.</p></body></html>");
            return html.toString();
        }

        // BR-5: student sees avg scores, public comments, and grade only
        Map<Long, List<Integer>> criterionScores = new LinkedHashMap<>();
        Map<Long, String> criterionNames = new LinkedHashMap<>();
        for (PeerEvaluation eval : evals) {
            for (PeerEvaluationScore score : eval.getScores()) {
                criterionScores.computeIfAbsent(score.getCriterion().getId(), k -> new ArrayList<>()).add(score.getScore());
                criterionNames.put(score.getCriterion().getId(), score.getCriterion().getName());
            }
        }

        double overallGrade = computeGrade(evals);
        html.append("<table border='1' cellpadding='8'><thead><tr><th>Criterion</th><th>Average Score</th></tr></thead><tbody>");
        for (Map.Entry<Long, List<Integer>> entry : criterionScores.entrySet()) {
            double avg = entry.getValue().stream().mapToInt(i -> i).average().orElse(0);
            html.append("<tr><td>").append(criterionNames.get(entry.getKey())).append("</td><td>")
                .append(String.format("%.1f", avg)).append("</td></tr>");
        }
        html.append("</tbody></table>");
        html.append("<p><strong>Grade: ").append(String.format("%.1f", overallGrade)).append("</strong></p>");
        html.append("<h4>Public Comments:</h4><ul>");
        for (PeerEvaluation eval : evals) {
            if (eval.getPublicComment() != null && !eval.getPublicComment().isBlank()) {
                html.append("<li>").append(eval.getPublicComment()).append("</li>");
            }
        }
        html.append("</ul></body></html>");
        return html.toString();
    }

    private double computeGrade(List<PeerEvaluation> evals) {
        if (evals.isEmpty()) return 0;
        double total = 0;
        int count = 0;
        for (PeerEvaluation eval : evals) {
            int evalTotal = eval.getScores().stream().mapToInt(PeerEvaluationScore::getScore).sum();
            total += evalTotal;
            count++;
        }
        return count > 0 ? total / count : 0;
    }
}
