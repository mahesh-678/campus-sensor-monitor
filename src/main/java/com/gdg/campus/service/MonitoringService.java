package com.gdg.campus.service;

import com.gdg.campus.dto.ReadingRequest;
import com.gdg.campus.model.*;
import com.gdg.campus.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.Duration;
import java.util.*;

@Service
public class MonitoringService {
    private final ReadingRepository readings;
    private final RuleRepository rules;
    private final AlertRepository alerts;

    public MonitoringService(ReadingRepository readings, RuleRepository rules, AlertRepository alerts) {
        this.readings=readings; this.rules=rules; this.alerts=alerts;
    }

    @Transactional
    public Map<String, Object> ingestOne(ReadingRequest request) {
        SensorReading saved = readings.save(new SensorReading(request.sensorId().trim(), request.type().trim().toLowerCase(),
                request.value(), request.timestamp()));
        int created = evaluate(saved);
        return Map.of("readingId", saved.getId(), "alertsCreated", created);
    }

    @Transactional
    public Map<String, Object> ingestBatch(List<ReadingRequest> batch) {
        if (batch == null || batch.isEmpty()) throw new IllegalArgumentException("Batch must contain at least one reading");
        if (batch.size() > 10000) throw new IllegalArgumentException("Maximum batch size is 10000");
        List<SensorReading> saved = new ArrayList<>(batch.size());
        for (ReadingRequest r : batch) {
            if (r == null) throw new IllegalArgumentException("Batch contains a null reading");
            saved.add(new SensorReading(r.sensorId().trim(), r.type().trim().toLowerCase(), r.value(), r.timestamp()));
        }
        readings.saveAll(saved);
        int created = 0;
        for (SensorReading r : saved) created += evaluate(r);
        return Map.of("accepted", saved.size(), "alertsCreated", created);
    }

    private int evaluate(SensorReading reading) {
        int created = 0;
        List<AlertRule> applicable = rules.findByEnabledTrueAndTypeIgnoreCase(reading.getType());
        for (AlertRule rule : applicable) {
            double observed = reading.getValue();
            if (rule.getWindowMinutes() > 0) {
                Instant from = reading.getTimestamp().minus(Duration.ofMinutes(rule.getWindowMinutes()));
                Double avg = readings.averageInWindow(reading.getSensorId(), reading.getType(), from, reading.getTimestamp());
                if (avg == null) continue;
                observed = avg;
            }
            if (matches(observed, rule.getOperator(), rule.getThreshold())) {
                boolean alreadyActive = alerts.existsByRuleIdAndSensorIdAndStatusIn(
                        rule.getId(), reading.getSensorId(), List.of("OPEN", "ACKNOWLEDGED"));
                if (!alreadyActive) {
                    alerts.save(new Alert(rule.getId(), rule.getName(), reading.getSensorId(),
                            reading.getType(), observed, rule.getThreshold()));
                    created++;
                }
            }
        }
        return created;
    }

    private boolean matches(double value, String op, double threshold) {
        return switch (op) {
            case ">" -> value > threshold;
            case ">=" -> value >= threshold;
            case "<" -> value < threshold;
            case "<=" -> value <= threshold;
            case "==" -> Double.compare(value, threshold) == 0;
            default -> false;
        };
    }
}
