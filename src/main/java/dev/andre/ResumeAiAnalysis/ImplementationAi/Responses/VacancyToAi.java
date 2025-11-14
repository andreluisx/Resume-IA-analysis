package dev.andre.ResumeAiAnalysis.ImplementationAi.Responses;

import dev.andre.ResumeAiAnalysis.ImplementationAi.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyToAi {

    private String title;

    private String description;

    private List<String> essential;

    private List<String> differential;


}
