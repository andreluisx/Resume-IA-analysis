package dev.andre.ResumeAiAnalysis.RabbitMQ.Dto;

public record ProcessApplicationMessage(
        Long userVacancyId,
        Long vacancyId,
        String filePath,
        String originalFilename
) {}
