package dev.andre.ResumeAiAnalysis.RabbitMQ.Dto;


import dev.andre.ResumeAiAnalysis.ImplementationAi.Responses.VacancyToAi;

public record ProcessApplicationMessage(
        VacancyToAi vacancy,
        Long userVacancyId,
        String filePath) {
}

