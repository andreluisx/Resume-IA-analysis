package dev.andre.ResumeAiAnalysis.Enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    REJECTED("Rejected"),
    CREATED("Created");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }
}
