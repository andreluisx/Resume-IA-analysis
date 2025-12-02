package dev.andre.ResumeAiAnalysis.Vacancy.Exceptions;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException(String message) {
        super(message);
    }
}
