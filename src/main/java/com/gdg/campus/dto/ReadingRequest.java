package com.gdg.campus.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public record ReadingRequest(
    @NotBlank @Size(max=120) String sensorId,
    @NotBlank @Size(max=80) String type,
    @NotNull @PositiveOrZero Double value,
    @NotNull Instant timestamp
) {}
