package dev.andre.ResumeAiAnalysis.ImplementationAi;

import dev.andre.ResumeAiAnalysis.Auth.Exceptions.UnauthenticatedUser;
import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import dev.andre.ResumeAiAnalysis.ExceptionHandler.NotFoundException;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.User.UserService;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.UserCannotAccessOrDoThat;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyService;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ai")
public class AiController {

    private AiService implementationAiService;
    private UserService userService;
    private VacancyService vacancyService;
    private UserVacancyService userVacancyService;

    public AiController(AiService implementationAiService, UserService userService, UserVacancyService userVacancyService, VacancyService vacancyService) {
        this.implementationAiService = implementationAiService;
        this.userService = userService;
        this.userVacancyService = userVacancyService;
        this.vacancyService = vacancyService;
    }

    @GetMapping("/{AiResponseId}")
    public ResponseEntity<AIEntity> getOneAiEntity(@PathVariable Long AiResponseId, Authentication authentication) {

        AIEntity oneById = implementationAiService.getOneById(AiResponseId, authentication);

        return ResponseEntity.ok(oneById);
    }
}
