package dev.andre.ResumeAiAnalysis.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class ExceptionsResponse {
    private String message;
    private int status;
    private OffsetDateTime timestamp;
    private String path; // opcional, remova se não quiser

    public ExceptionsResponse(String message) {
        this(message, 0, OffsetDateTime.now(), null);
    }
}
