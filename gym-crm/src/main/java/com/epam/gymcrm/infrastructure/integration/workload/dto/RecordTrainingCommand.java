package com.epam.gymcrm.infrastructure.integration.workload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RecordTrainingCommand(
        @NotBlank String trainerUsername,
        @NotBlank String trainerFirstName,
        @NotBlank String trainerLastName,
        @NotNull Boolean isActive,
        @NotNull LocalDate trainingDate,
        @Positive int trainingDurationMinutes,
        @NotNull ActionType actionType
) {
    public enum ActionType { ADD, DELETE }
}
