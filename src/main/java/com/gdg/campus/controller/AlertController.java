package com.gdg.campus.controller;

import com.gdg.campus.model.Alert;
import com.gdg.campus.repo.AlertRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertRepository repo;
    public AlertController(AlertRepository repo) { this.repo=repo; }

    @GetMapping
    public List<Alert> list(@RequestParam(required=false) String status) {
        List<Alert> all = repo.findAllByOrderByCreatedAtDesc();
        if (status == null || status.isBlank()) return all;
        return all.stream().filter(a -> a.getStatus().equalsIgnoreCase(status)).toList();
    }

    @PatchMapping("/{id}/acknowledge")
    public Alert acknowledge(@PathVariable Long id) {
        Alert alert = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alert.acknowledge();
        return repo.save(alert);
    }

    @PatchMapping("/{id}/resolve")
    public Alert resolve(@PathVariable Long id) {
        Alert alert = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alert.resolve();
        return repo.save(alert);
    }
}
