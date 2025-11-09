package dev.andre.ResumeAiAnalysis.Vacancy.Dtos;

import dev.andre.ResumeAiAnalysis.Enums.ApplicationStatus;
import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserVacancyRelationDto(
        Long id,
        String title,
        String description,
        List<String> essential,
        List<String> differential,
        int applications,
        int approvals,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        VacancyRole userRole,
        ApplicationStatus status,
        LocalDateTime userLinkedAt
) {
}
