package dev.andre.ResumeAiAnalysis.Vacancy.Dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VacancyResponseDto(
    Long id,
    String title,
    String description,
    List<String> essential,
    List<String> differential,
    int applications,
    int approvals,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
