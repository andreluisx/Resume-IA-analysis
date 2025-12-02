package dev.andre.ResumeAiAnalysis.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Email
        @NotEmpty(message = "Email é obrigatório")
        String email,
        @NotEmpty(message = "Senha é obrigatório")
        String password
) {}
