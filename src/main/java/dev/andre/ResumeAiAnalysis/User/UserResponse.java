package dev.andre.ResumeAiAnalysis.User;


import lombok.Builder;

@Builder
public record UserResponse(Long id, String name, String email) {
}
