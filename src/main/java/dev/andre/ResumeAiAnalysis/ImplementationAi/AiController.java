package dev.andre.ResumeAiAnalysis.ImplementationAi;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    final private AiService implementationAiService;

    public AiController(AiService implementationAiService) {
        this.implementationAiService = implementationAiService;
    }

    @GetMapping("/{AiResponseId}")
    public ResponseEntity<AIEntity> getOneAiEntity(@PathVariable Long AiResponseId, Authentication authentication) {

        AIEntity oneById = implementationAiService.getOneById(AiResponseId, authentication);

        return ResponseEntity.ok(oneById);
    }

    @GetMapping("/AiResponse/{userVacancyId}")
    public ResponseEntity<AIEntity> getAiResponseByUserVacancyId(@PathVariable Long userVacancyId, Authentication authentication) {
        AIEntity aiEntity = implementationAiService.aiResponsebyUserVacancy(userVacancyId, authentication);
        return ResponseEntity.ok(aiEntity);
    }

}
