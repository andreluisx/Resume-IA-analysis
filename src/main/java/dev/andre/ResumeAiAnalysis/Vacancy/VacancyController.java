package dev.andre.ResumeAiAnalysis.Vacancy;

import dev.andre.ResumeAiAnalysis.ExceptionHandler.NotFoundException;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.UserVacancyRelationDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyRequestDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyResponseDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.*;
import dev.andre.ResumeAiAnalysis.Vacancy.Mapper.VacancyMapper;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("vacancy")
public class VacancyController {

    final private VacancyService vacancyService;
    final private UserVacancyService userVacancyService;

    public VacancyController(VacancyService vacancyService, UserVacancyService userVacancyService) {
        this.vacancyService = vacancyService;
        this.userVacancyService = userVacancyService;
    }

    // Criar Vaga
    @PostMapping
    public ResponseEntity<VacancyResponseDto> createVacancy(
            @RequestBody VacancyRequestDto vacancyRequestDto,
            Authentication authentication) {

        VacancyEntity savedVacancy = vacancyService.createVacancy(vacancyRequestDto, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VacancyMapper.toVacancyResponse(savedVacancy));
    }

    // Busca Todas as vagas (pesquisa e paginação)
    @GetMapping
    public ResponseEntity<List<VacancyResponseDto>> Allvacancies(Authentication authentication) {

        List<VacancyResponseDto> allVacancies = vacancyService.getAllVacancies().stream().map((vacancyEntity) -> VacancyMapper.toVacancyResponse(vacancyEntity)).toList();
        return ResponseEntity.ok(allVacancies);
    }

    // Busca "Minha Vagas"
    @GetMapping("/my")
    public ResponseEntity<List<UserVacancyRelationDto>> myVacancies(Authentication authentication) {
        List<UserVacancyEntity> userVacancies = userVacancyService.findByUser(authentication);
        //  Busca todas as relações (user_vacancies) desse usuário
        List<UserVacancyRelationDto> RelationUserVacancy = userVacancies.stream()
                .map(vacancy -> VacancyMapper.toRelationResponse(vacancy)).toList();

        return ResponseEntity.ok(RelationUserVacancy);
    }

    // Retorna Vaga Especifica
    @GetMapping("/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> vacancyDetails(@PathVariable Long vacancyId) {

        Optional<VacancyEntity> vacancyById = vacancyService.getVacancyById(vacancyId);
        if (vacancyById.isEmpty()) {
            throw new NotFoundException("Vaga não encontrada");
        }
        return ResponseEntity.ok(vacancyById.map(VacancyMapper::toVacancyResponse).orElse(null));
    }

    //aplica a vaga 2
    @PostMapping("/application/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> applyToVacancy2(
            @PathVariable Long vacancyId,
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        VacancyEntity vacancyEntity = vacancyService.vacancyApplication(vacancyId, authentication, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(VacancyMapper.toVacancyResponse(vacancyEntity));
    }

    // Edita Vaga de Emprego
    @PutMapping("/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> editVacancy(@PathVariable Long vacancyId, @RequestBody VacancyRequestDto VacancyDto, Authentication authentication) {
        VacancyEntity vacancyEditedEntity = vacancyService.updateVacancy(vacancyId, VacancyDto, authentication);
        return ResponseEntity.ok(VacancyMapper.toVacancyResponse(vacancyEditedEntity));
    }

    // Deleta Vaga de Emprego
    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<?> deleteVacancy(@PathVariable Long vacancyId, Authentication authentication) {
        this.vacancyService.deleteVacancyById(authentication, vacancyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}


