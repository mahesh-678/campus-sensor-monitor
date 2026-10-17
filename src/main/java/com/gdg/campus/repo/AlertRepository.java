package com.gdg.campus.repo;

import com.gdg.campus.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByOrderByCreatedAtDesc();
    boolean existsByRuleIdAndSensorIdAndStatusIn(Long ruleId, String sensorId, List<String> statuses);
}
