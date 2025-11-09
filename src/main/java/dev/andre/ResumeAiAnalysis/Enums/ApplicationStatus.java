package dev.andre.ResumeAiAnalysis.Enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }
}
