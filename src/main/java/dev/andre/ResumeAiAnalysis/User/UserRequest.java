package dev.andre.ResumeAiAnalysis.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRequest(@NotBlank String name, @NotBlank String email, @NotBlank String password) {
}
