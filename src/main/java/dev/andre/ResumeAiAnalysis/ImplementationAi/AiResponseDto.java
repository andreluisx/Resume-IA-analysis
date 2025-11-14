package dev.andre.ResumeAiAnalysis.ImplementationAi;

import dev.andre.ResumeAiAnalysis.ImplementationAi.Enums.Status;
import lombok.Data;

import java.util.List;

@Data
public class AiResponseDto {
    private String application_description;
    private String improvements_to_this_vacancy;
    private List<String> essential_matches;
    private List<String> differential_matches;
    private Status status;
}
