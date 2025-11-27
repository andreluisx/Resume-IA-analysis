package dev.andre.ResumeAiAnalysis.Auth.Exceptions;

public class EmailAlreadyExist extends RuntimeException {
    public EmailAlreadyExist(String message) {
        super(message);
    }
}
