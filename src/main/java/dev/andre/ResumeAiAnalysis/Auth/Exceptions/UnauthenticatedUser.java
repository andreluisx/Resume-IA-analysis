package dev.andre.ResumeAiAnalysis.Auth.Exceptions;

public class UnauthenticatedUser extends RuntimeException {
    public UnauthenticatedUser(String message) {
        super(message);
    }
}
