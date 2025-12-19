package dev.andre.ResumeAiAnalysis.VacancyUser.Dto;

import dev.andre.ResumeAiAnalysis.Enums.ApplicationStatus;
import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserVacancyResponse(
        Long id,
        VacancyRole role,
        ApplicationStatus status,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
}
