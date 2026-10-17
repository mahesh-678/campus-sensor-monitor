package com.gdg.campus.repo;

import com.gdg.campus.model.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByEnabledTrueAndTypeIgnoreCase(String type);
}
