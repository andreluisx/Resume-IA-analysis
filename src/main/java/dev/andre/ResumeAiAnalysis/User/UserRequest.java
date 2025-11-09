package dev.andre.ResumeAiAnalysis.User;

import lombok.Builder;

@Builder
public record UserRequest(String name, String email, String password) {
}
