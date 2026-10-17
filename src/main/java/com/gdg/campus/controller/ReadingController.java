package com.gdg.campus.controller;

import com.gdg.campus.dto.ReadingRequest;
import com.gdg.campus.service.MonitoringService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {
    private final MonitoringService service;
    public ReadingController(MonitoringService service) { this.service=service; }

    @PostMapping
    public Map<String,Object> ingest(@Valid @RequestBody ReadingRequest request) {
        return service.ingestOne(request);
    }

    @PostMapping("/batch")
    public Map<String,Object> ingestBatch(@RequestBody List<@Valid ReadingRequest> batch) {
        return service.ingestBatch(batch);
    }
}
