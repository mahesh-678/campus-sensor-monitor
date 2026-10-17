package com.gdg.campus.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sensor_readings", indexes = {
    @Index(name = "idx_reading_sensor_type_time", columnList = "sensorId,metricType,recordedAt"),
    @Index(name = "idx_reading_recorded_at", columnList = "recordedAt")
})
public class SensorReading {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String sensorId;
    @Column(name = "metricType", nullable = false, length = 80)
    private String type;
    @Column(nullable = false)
    private double value;
    @Column(name = "recordedAt", nullable = false)
    private Instant timestamp;

    protected SensorReading() {}
    public SensorReading(String sensorId, String type, double value, Instant timestamp) {
        this.sensorId = sensorId; this.type = type; this.value = value; this.timestamp = timestamp;
    }
    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getType() { return type; }
    public double getValue() { return value; }
    public Instant getTimestamp() { return timestamp; }
}
