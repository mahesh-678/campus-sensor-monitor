package com.gdg.campus.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alert_rules", indexes = @Index(name = "idx_rule_enabled_type", columnList = "enabled,metricType"))
public class AlertRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(name = "metricType", nullable = false, length = 80)
    private String type;
    @Column(nullable = false, length = 8)
    private String operator;
    @Column(nullable = false)
    private double threshold;
    @Column(nullable = false)
    private boolean enabled = true;
    // 0 means evaluate each reading; positive values mean rolling average window in minutes.
    @Column(nullable = false)
    private int windowMinutes = 0;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected AlertRule() {}
    public AlertRule(String name, String type, String operator, double threshold, boolean enabled, int windowMinutes) {
        this.name=name; this.type=type; this.operator=operator; this.threshold=threshold;
        this.enabled=enabled; this.windowMinutes=windowMinutes;
    }
    public void update(String name, String type, String operator, double threshold, int windowMinutes) {
        this.name=name; this.type=type; this.operator=operator; this.threshold=threshold; this.windowMinutes=windowMinutes;
    }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getOperator() { return operator; }
    public double getThreshold() { return threshold; }
    public boolean isEnabled() { return enabled; }
    public int getWindowMinutes() { return windowMinutes; }
    public Instant getCreatedAt() { return createdAt; }
}
