package com.projectpulse.service;

import com.projectpulse.dto.request.CreateSectionRequest;
import com.projectpulse.dto.request.SetupActiveWeeksRequest;
import com.projectpulse.model.ActiveWeek;
import com.projectpulse.model.Rubric;
import com.projectpulse.model.Section;
import com.projectpulse.repository.ActiveWeekRepository;
import com.projectpulse.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final RubricService rubricService;

    @Autowired
    public SectionService(SectionRepository sectionRepository, ActiveWeekRepository activeWeekRepository,
                          RubricService rubricService) {
        this.sectionRepository = sectionRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.rubricService = rubricService;
    }

    @Transactional
    public Section createSection(CreateSectionRequest request) {
        if (sectionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Section name already exists");
        }
        Section section = new Section();
        section.setName(request.getName());
        section.setStartDate(request.getStartDate());
        section.setEndDate(request.getEndDate());

        if (request.getRubricId() != null) {
            Rubric rubric = rubricService.getRubric(request.getRubricId());
            section.setRubric(rubric);
        }
        return sectionRepository.save(section);
    }

    public List<Section> searchSections(String name) {
        return sectionRepository.searchByName(name);
    }

    public Section getSection(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    @Transactional
    public Section updateSection(Long id, CreateSectionRequest request) {
        Section section = getSection(id);
        if (!section.getName().equals(request.getName()) && sectionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Section name already exists");
        }
        section.setName(request.getName());
        section.setStartDate(request.getStartDate());
        section.setEndDate(request.getEndDate());
        if (request.getRubricId() != null) {
            section.setRubric(rubricService.getRubric(request.getRubricId()));
        }
        return sectionRepository.save(section);
    }

    @Transactional
    public void setupActiveWeeks(Long sectionId, SetupActiveWeeksRequest request) {
        Section section = getSection(sectionId);
        activeWeekRepository.deleteBySectionId(sectionId);

        LocalDate current = section.getStartDate();
        while (!current.isAfter(section.getEndDate())) {
            LocalDate weekStart = current.with(DayOfWeek.MONDAY);
            if (weekStart.isBefore(section.getStartDate())) weekStart = section.getStartDate();
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(section.getEndDate())) weekEnd = section.getEndDate();

            boolean inactive = request.getInactiveWeekStarts() != null &&
                    request.getInactiveWeekStarts().contains(weekStart);

            ActiveWeek activeWeek = new ActiveWeek();
            activeWeek.setSection(section);
            activeWeek.setWeekStart(weekStart);
            activeWeek.setWeekEnd(weekEnd);
            activeWeek.setActive(!inactive);
            activeWeekRepository.save(activeWeek);

            current = weekStart.plusWeeks(1);
        }
    }

    public List<ActiveWeek> getActiveWeeks(Long sectionId) {
        return activeWeekRepository.findBySectionIdOrderByWeekStartAsc(sectionId);
    }
}
