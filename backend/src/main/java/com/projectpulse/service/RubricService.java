package com.projectpulse.service;

import com.projectpulse.dto.request.CreateRubricRequest;
import com.projectpulse.model.Rubric;
import com.projectpulse.model.RubricCriterion;
import com.projectpulse.repository.RubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RubricService {

    private final RubricRepository rubricRepository;

    @Autowired
    public RubricService(RubricRepository rubricRepository) {
        this.rubricRepository = rubricRepository;
    }

    @Transactional
    public Rubric createRubric(CreateRubricRequest request) {
        if (rubricRepository.existsByName(request.getName())) {
            throw new RuntimeException("Rubric name must be unique");
        }
        Rubric rubric = new Rubric();
        rubric.setName(request.getName());
        addCriteria(rubric, request.getCriteria());
        return rubricRepository.save(rubric);
    }

    public List<Rubric> getAllRubrics() {
        return rubricRepository.findAll();
    }

    public Rubric getRubric(Long id) {
        return rubricRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubric not found"));
    }

    @Transactional
    public Rubric duplicateAndEdit(Long rubricId, List<CreateRubricRequest.CriterionDto> overrides, String newName) {
        Rubric original = getRubric(rubricId);
        Rubric copy = new Rubric();
        copy.setName(newName != null ? newName : original.getName() + " (copy)");
        if (overrides != null && !overrides.isEmpty()) {
            addCriteria(copy, overrides);
        } else {
            for (RubricCriterion c : original.getCriteria()) {
                RubricCriterion newC = new RubricCriterion();
                newC.setRubric(copy);
                newC.setName(c.getName());
                newC.setDescription(c.getDescription());
                newC.setMaxScore(c.getMaxScore());
                copy.getCriteria().add(newC);
            }
        }
        return rubricRepository.save(copy);
    }

    private void addCriteria(Rubric rubric, List<CreateRubricRequest.CriterionDto> dtos) {
        for (CreateRubricRequest.CriterionDto dto : dtos) {
            RubricCriterion criterion = new RubricCriterion();
            criterion.setRubric(rubric);
            criterion.setName(dto.getName());
            criterion.setDescription(dto.getDescription());
            criterion.setMaxScore(dto.getMaxScore());
            rubric.getCriteria().add(criterion);
        }
    }
}
