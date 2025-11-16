package dev.andre.ResumeAiAnalysis.ImplementationAi;

import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.User.UserService;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyService;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/ai")
public class AiController {

    private AiService implementationAiService;
    private UserService userService;
    private VacancyService vacancyService;
    private UserVacancyService userVacancyService;

    public AiController(AiService implementationAiService,  UserService userService,  UserVacancyService userVacancyService, VacancyService vacancyService) {
        this.implementationAiService = implementationAiService;
        this.userService = userService;
        this.userVacancyService = userVacancyService;
        this.vacancyService = vacancyService;
    }

    @GetMapping("/{AiResponseId}")
    public ResponseEntity<?> getOneAiEntity(@PathVariable Long AiResponseId, Authentication authentication){

        // ==== 1. Autenticação ====
        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não autenticado");
        }
        UserEntity user = userOpt.get();

        Optional<AIEntity> AiResponse = implementationAiService.getOneById(AiResponseId);

        if(AiResponse.isEmpty()){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(AiResponse.get());
    }
}
