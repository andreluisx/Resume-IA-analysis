package dev.andre.ResumeAiAnalysis.ImplementationAi.Exceptions;


public class AiProcessingException extends RuntimeException {
    public AiProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}