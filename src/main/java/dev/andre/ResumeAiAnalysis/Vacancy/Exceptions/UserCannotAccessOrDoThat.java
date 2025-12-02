package dev.andre.ResumeAiAnalysis.Vacancy.Exceptions;

public class UserCannotAccessOrDoThat extends RuntimeException {
    public UserCannotAccessOrDoThat(String message) {
        super(message);
    }
}
