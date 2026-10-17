package com.gdg.campus.repo;

import com.gdg.campus.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.OptionalDouble;

public interface ReadingRepository extends JpaRepository<SensorReading, Long> {
    @Query("select avg(r.value) from SensorReading r where r.sensorId = :sensorId and r.type = :type and r.timestamp >= :fromTime and r.timestamp <= :toTime")
    Double averageInWindow(@Param("sensorId") String sensorId, @Param("type") String type,
                           @Param("fromTime") Instant fromTime, @Param("toTime") Instant toTime);
}
