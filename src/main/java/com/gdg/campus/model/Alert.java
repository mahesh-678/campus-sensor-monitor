package com.gdg.campus.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alert_status_created", columnList = "status,createdAt"),
    @Index(name = "idx_alert_sensor_rule_status", columnList = "sensorId,ruleId,status")
})
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long ruleId;
    @Column(nullable = false, length = 120)
    private String ruleName;
    @Column(nullable = false, length = 120)
    private String sensorId;
    @Column(nullable = false, length = 80)
    private String type;
    @Column(nullable = false)
    private double observedValue;
    @Column(nullable = false)
    private double threshold;
    @Column(nullable = false, length = 20)
    private String status = "OPEN";
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    private Instant acknowledgedAt;
    private Instant resolvedAt;

    protected Alert() {}
    public Alert(Long ruleId, String ruleName, String sensorId, String type, double observedValue, double threshold) {
        this.ruleId=ruleId; this.ruleName=ruleName; this.sensorId=sensorId; this.type=type;
        this.observedValue=observedValue; this.threshold=threshold;
    }
    public void acknowledge() {
        if ("OPEN".equals(status)) { status = "ACKNOWLEDGED"; acknowledgedAt = Instant.now(); }
    }
    public void resolve() {
        if (!"RESOLVED".equals(status)) { status = "RESOLVED"; resolvedAt = Instant.now(); }
    }
    public Long getId() { return id; }
    public Long getRuleId() { return ruleId; }
    public String getRuleName() { return ruleName; }
    public String getSensorId() { return sensorId; }
    public String getType() { return type; }
    public double getObservedValue() { return observedValue; }
    public double getThreshold() { return threshold; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getAcknowledgedAt() { return acknowledgedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
}
