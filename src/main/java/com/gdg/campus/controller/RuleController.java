package com.gdg.campus.controller;

import com.gdg.campus.dto.RuleRequest;
import com.gdg.campus.model.AlertRule;
import com.gdg.campus.repo.RuleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    private final RuleRepository repo;
    public RuleController(RuleRepository repo) { this.repo=repo; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertRule create(@Valid @RequestBody RuleRequest r) {
        return repo.save(new AlertRule(r.name(), r.type().trim().toLowerCase(), r.operator(),
                r.threshold(), true, r.windowMinutes()));
    }

    @GetMapping
    public List<AlertRule> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public AlertRule update(@PathVariable Long id, @Valid @RequestBody RuleRequest r) {
        AlertRule rule = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rule not found"));
        rule.update(r.name(), r.type().trim().toLowerCase(), r.operator(), r.threshold(), r.windowMinutes());
        return repo.save(rule);
    }

    @PatchMapping("/{id}/enabled")
    public AlertRule setEnabled(@PathVariable Long id, @RequestParam boolean value) {
        AlertRule rule = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rule not found"));
        rule.setEnabled(value);
        return repo.save(rule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rule not found");
        repo.deleteById(id);
    }
}
