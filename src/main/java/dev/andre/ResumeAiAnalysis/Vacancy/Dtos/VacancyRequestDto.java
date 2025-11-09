package dev.andre.ResumeAiAnalysis.Vacancy.Dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record VacancyRequestDto(
    String title,
    String description,
    List<String> essential,
    List<String> differential
) {}
