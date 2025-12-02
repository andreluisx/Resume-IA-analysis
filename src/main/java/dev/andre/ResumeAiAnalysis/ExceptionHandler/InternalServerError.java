package dev.andre.ResumeAiAnalysis.ExceptionHandler;

public class InternalServerError extends RuntimeException {
    public InternalServerError(String message) {
        super(message);
    }
}
