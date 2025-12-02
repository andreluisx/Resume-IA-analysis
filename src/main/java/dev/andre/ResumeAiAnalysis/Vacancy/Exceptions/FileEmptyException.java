package dev.andre.ResumeAiAnalysis.Vacancy.Exceptions;

public class FileEmptyException extends RuntimeException {
    public FileEmptyException(String message) {
        super(message);
    }
}
