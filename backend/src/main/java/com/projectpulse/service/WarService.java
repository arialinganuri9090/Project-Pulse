package com.projectpulse.service;

import com.projectpulse.dto.request.WarActivityRequest;
import com.projectpulse.model.ActiveWeek;
import com.projectpulse.model.User;
import com.projectpulse.model.War;
import com.projectpulse.model.WarActivity;
import com.projectpulse.repository.ActiveWeekRepository;
import com.projectpulse.repository.UserRepository;
import com.projectpulse.repository.WarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarService {

    private final WarRepository warRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final UserRepository userRepository;

    @Autowired
    public WarService(WarRepository warRepository, ActiveWeekRepository activeWeekRepository,
                      UserRepository userRepository) {
        this.warRepository = warRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.userRepository = userRepository;
    }

    public List<ActiveWeek> getWeeksForStudent(Long studentId) {
        return activeWeekRepository.findActiveWeeksByStudentId(studentId);
    }

    public War getOrCreateWar(Long studentId, Long weekId) {
        return warRepository.findByStudentIdAndActiveWeekId(studentId, weekId)
                .orElseGet(() -> {
                    User student = userRepository.findById(studentId)
                            .orElseThrow(() -> new RuntimeException("Student not found"));
                    ActiveWeek week = activeWeekRepository.findById(weekId)
                            .orElseThrow(() -> new RuntimeException("Week not found"));
                    War war = new War();
                    war.setStudent(student);
                    war.setActiveWeek(week);
                    return warRepository.save(war);
                });
    }

    @Transactional
    public WarActivity addActivity(Long studentId, Long weekId, WarActivityRequest request) {
        War war = getOrCreateWar(studentId, weekId);
        WarActivity activity = new WarActivity();
        activity.setWar(war);
        activity.setCategory(request.getCategory());
        activity.setActivity(request.getActivity());
        activity.setDescription(request.getDescription());
        activity.setPlannedHours(request.getPlannedHours());
        activity.setActualHours(request.getActualHours());
        activity.setStatus(request.getStatus());
        war.getActivities().add(activity);
        War saved = warRepository.save(war);
        return saved.getActivities().get(saved.getActivities().size() - 1);
    }

    @Transactional
    public WarActivity updateActivity(Long studentId, Long weekId, Long activityId, WarActivityRequest request) {
        War war = warRepository.findByStudentIdAndActiveWeekId(studentId, weekId)
                .orElseThrow(() -> new RuntimeException("WAR not found"));
        WarActivity activity = war.getActivities().stream()
                .filter(a -> a.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        activity.setCategory(request.getCategory());
        activity.setActivity(request.getActivity());
        activity.setDescription(request.getDescription());
        activity.setPlannedHours(request.getPlannedHours());
        activity.setActualHours(request.getActualHours());
        activity.setStatus(request.getStatus());
        warRepository.save(war);
        return activity;
    }

    @Transactional
    public void deleteActivity(Long studentId, Long weekId, Long activityId) {
        War war = warRepository.findByStudentIdAndActiveWeekId(studentId, weekId)
                .orElseThrow(() -> new RuntimeException("WAR not found"));
        war.getActivities().removeIf(a -> a.getId().equals(activityId));
        warRepository.save(war);
    }

    public War getWarForStudentAndWeek(Long studentId, Long weekId) {
        return warRepository.findByStudentIdAndActiveWeekId(studentId, weekId).orElse(null);
    }

    public List<War> getWarsByWeek(Long weekId) {
        return warRepository.findByActiveWeekId(weekId);
    }
}
