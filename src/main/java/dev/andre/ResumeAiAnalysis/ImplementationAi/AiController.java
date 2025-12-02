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

        // ==== 1. Autenticação ====
        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }
        UserEntity user = userOpt.get();

        Optional<AIEntity> AiResponse = implementationAiService.getOneById(AiResponseId);

        // verifica se existe a analise da ia
        if (AiResponse.isEmpty()) {
            throw new NotFoundException("Analise da IA não existe");
        }

        Optional<UserVacancyEntity> userVacancyOpt = userVacancyService.findById(AiResponse.get().getUserVacancy().getId());

        //verifica se existe a relação user vacancy da IA entity
        if (userVacancyOpt.isEmpty()) {
            throw new NotFoundException("Analise da IA pra essa vaga não existe mais");
        }

        Optional<UserVacancyEntity> userVacancyRelationOpt = userVacancyService.findByUserAndVacancy(user, userVacancyOpt.get().getVacancy());

        if (userVacancyRelationOpt.isEmpty()) {
            throw new UserCannotAccessOrDoThat("Você não tem relação com essa vaga");
        }

        if (!(user.getId().equals(userVacancyOpt.get().getUser().getId()) || userVacancyRelationOpt.get().getRole().equals(VacancyRole.RECRUITER))) {
            throw new UserCannotAccessOrDoThat("Você não tem autorização para ver isso");
        }

        return ResponseEntity.ok(AiResponse.get());
    }
}
