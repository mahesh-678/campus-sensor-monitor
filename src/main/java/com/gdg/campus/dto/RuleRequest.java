package com.gdg.campus.dto;

import jakarta.validation.constraints.*;

public record RuleRequest(
    @NotBlank @Size(max=120) String name,
    @NotBlank @Size(max=80) String type,
    @NotBlank String operator,
    @NotNull Double threshold,
    @Min(0) @Max(1440) int windowMinutes
) {
    public RuleRequest {
        if (operator != null && !operator.equals(">") && !operator.equals(">=")
                && !operator.equals("<") && !operator.equals("<=") && !operator.equals("==")) {
            throw new IllegalArgumentException("operator must be one of >, >=, <, <=, ==");
        }
    }
}
