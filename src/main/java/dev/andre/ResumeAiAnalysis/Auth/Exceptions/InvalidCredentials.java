package dev.andre.ResumeAiAnalysis.Auth.Exceptions;

public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials(String message) {
        super(message);
    }
}
