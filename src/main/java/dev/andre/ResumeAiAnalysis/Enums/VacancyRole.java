package dev.andre.ResumeAiAnalysis.Enums;

import lombok.Getter;

@Getter
public enum VacancyRole {

    RECRUITER("Recruiter - Defines the Vacancy Requirements"),

    APPLICANT("Applicant - Submits the Resume/CV");

    private final String description;

    VacancyRole(String description) {
        this.description = description;
    }
}