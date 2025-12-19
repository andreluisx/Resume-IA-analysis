package dev.andre.ResumeAiAnalysis.Vacancy;

import java.util.List;
import java.util.Optional;

import dev.andre.ResumeAiAnalysis.ImplementationAi.AiResponseDto;
import dev.andre.ResumeAiAnalysis.VacancyUser.Dto.UserVacancyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.andre.ResumeAiAnalysis.ExceptionHandler.NotFoundException;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.UserVacancyRelationDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyRequestDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyResponseDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Mapper.VacancyMapper;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;

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

    // Busca Todas as vagas (paginação)
    @GetMapping
    public ResponseEntity<Page<VacancyResponseDto>> allvacancies(Pageable pageable) {
        Page<VacancyResponseDto> listaPaginada = vacancyService.getAllVacancies(pageable);
        return ResponseEntity.ok(listaPaginada);
    }

    // Busca "Minha Vagas"
    @GetMapping("/my")
    public ResponseEntity<Page<UserVacancyRelationDto>> myVacancies(Authentication authentication, Pageable pageable) {
        Page<UserVacancyEntity> userVacancies = userVacancyService.findByUser(authentication, pageable);

        Page<UserVacancyRelationDto> dtoPage = userVacancies.map(VacancyMapper::toRelationResponse);

        return ResponseEntity.ok(dtoPage);
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

    //aplica a vaga
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

    //relação usuario e vaga
    @GetMapping("/user-vacancy/{userVacancyId}")
    public ResponseEntity<UserVacancyResponse> vacancyResults(@PathVariable Long userVacancyId, Authentication authentication) {
        return ResponseEntity.ok(userVacancyService.getUserVacancyById(userVacancyId, authentication));
    }

}


