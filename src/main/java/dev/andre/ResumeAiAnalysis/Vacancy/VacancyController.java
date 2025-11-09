package dev.andre.ResumeAiAnalysis.Vacancy;

import dev.andre.ResumeAiAnalysis.Application.ApplicationEntity;
import dev.andre.ResumeAiAnalysis.Application.ApplicationService;
import dev.andre.ResumeAiAnalysis.Enums.ApplicationStatus;
import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.User.UserService;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.UserVacancyRelationDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyRequestDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyResponseDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Mapper.VacancyMapper;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;
import org.apache.catalina.mapper.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("vacancy")
public class VacancyController {

    final private VacancyService vacancyService;
    final private UserVacancyService userVacancyService;
    final private UserService userService;
    final private ApplicationService applicationService;

    public VacancyController(VacancyService vacancyService, UserService userService, UserVacancyService userVacancyService,  ApplicationService applicationService) {
        this.vacancyService = vacancyService;
        this.userService = userService;
        this.userVacancyService = userVacancyService;
        this.applicationService = applicationService;
    }

    // Criar Vaga
    @PostMapping
    public ResponseEntity<VacancyResponseDto> createVacancy(
            @RequestBody VacancyRequestDto vacancyRequestDto,
            Authentication authentication) {

        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userOpt.get();

        // Cria a vaga
        VacancyEntity vacancy = VacancyMapper.toVacancyEntity(vacancyRequestDto);
        vacancy.setActive(true);

        // Salva a vaga no banco
        VacancyEntity savedVacancy = vacancyService.createVacancy(vacancy);

        // Cria o vínculo entre o usuário e a vaga
        UserVacancyEntity userVacancy = UserVacancyEntity.builder()
                .user(user)
                .vacancy(savedVacancy)
                .status(ApplicationStatus.APPROVED)
                .role(VacancyRole.RECRUITER)
                .build();

        // Salva o vínculo na tabela intermediária user_vacancies
        userVacancyService.save(userVacancy);

        // Retorna a resposta
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VacancyMapper.toVacancyResponse(savedVacancy));
    }

    // Busca Todas as vagas (pesquisa e paginação)
    @GetMapping
    public ResponseEntity<List<VacancyResponseDto>> Allvacancies() {
        List<VacancyResponseDto> allVacancies = vacancyService.getAllVacancies().stream().map((vacancyEntity) -> VacancyMapper.toVacancyResponse(vacancyEntity)).toList();
        return ResponseEntity.ok(allVacancies);
    }

    // Busca "Minha Vagas"
    @GetMapping("/my")
    public ResponseEntity<List<UserVacancyRelationDto>> myVacancies(Authentication authentication) {
        Optional<UserEntity> userOpt = userService.getUser(authentication);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userOpt.get();

        //  Busca todas as relações (user_vacancies) desse usuário
        List<UserVacancyEntity> userVacancies = userVacancyService.findByUser(user);

        List<UserVacancyRelationDto> RelationUserVacancy = userVacancies.stream()
                .map(vacancy-> VacancyMapper.toRelationResponse(vacancy)).toList();

        return ResponseEntity.ok(RelationUserVacancy);
    }


    // Retorna Vaga Especifica
    @GetMapping("/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> vacancyDetails(@PathVariable Long vacancyId) {

        Optional<VacancyEntity> vacancyById = vacancyService.getVacancyById(vacancyId);
        if (vacancyById.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(vacancyById.map(VacancyMapper::toVacancyResponse).orElse(null));
    }

    // Aplicar a Vaga
    @PostMapping("/application/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> applicationToVacancy(
            @PathVariable Long vacancyId,
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UserEntity user = userOpt.get();

        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().build();

        // validar tamanho e tipo
        if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(null);
        }
        String ct = file.getContentType();
        // permitir apenas pdf
        if (!"application/pdf".equals(ct)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        Optional<VacancyEntity> vacancyOpt = vacancyService.getVacancyById(vacancyId);
        if (vacancyOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        VacancyEntity vacancy = vacancyOpt.get();

        boolean alreadyLinked = userVacancyService.existUserVacancyRelation(user, vacancy);
        if (alreadyLinked) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        // 1) SALVAR O ARQUIVO NO DISCO (safe)
        String uploadDir = "uploads/applications/";  // ou carregue de config
        try {
            Files.createDirectories(Paths.get(uploadDir));

            String original = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String ext = "";
            int i = original.lastIndexOf('.');
            if (i > -1) ext = original.substring(i);

            String savedName = UUID.randomUUID().toString() + ext;
            Path target = Paths.get(uploadDir).resolve(savedName);

            // stream copy (não carrega em memória)
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            // 2) criar relação user_vacancy
            UserVacancyEntity userVacancy = UserVacancyEntity.builder()
                    .vacancy(vacancy)
                    .user(user)
                    .role(VacancyRole.APPLICANT)
                    .status(ApplicationStatus.PENDING)
                    .build();

            userVacancyService.save(userVacancy);

            // 3) salvar metadados do arquivo (attachment)
            ApplicationEntity attachment = ApplicationEntity.builder()
                    .originalName(original)
                    .filename(savedName)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .path(target.toString())
                    .userVacancy(userVacancy)
                    .build();
            applicationService.save(attachment);

            // 4) incrementar contador de forma atômica
            vacancyService.incrementApplications(vacancyId);

            // 5) recarregar vaga e retornar
            VacancyEntity updatedVacancy = vacancyService.getVacancyById(vacancyId).get();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(VacancyMapper.toVacancyResponse(updatedVacancy));

        } catch (IOException e) {
            // logar e retornar erro 500
            System.out.println("Erro ao salvar arquivo: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // Edita Vaga de Emprego
    @PutMapping("/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> editVacancy(@PathVariable Long vacancyId, @RequestBody VacancyRequestDto VacancyDto, Authentication authentication) {
        Optional<UserEntity> userOpt = userService.getUser(authentication);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserEntity user = userOpt.get();

        Optional<VacancyEntity> vacancyById = vacancyService.getVacancyById(vacancyId);

        if (vacancyById.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<UserVacancyEntity> userVacancyRelation = userVacancyService.findByUserAndVacancy(user, vacancyById.get());

        if (userVacancyRelation.isEmpty() || userVacancyRelation.get().getRole() == VacancyRole.APPLICANT) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        VacancyEntity vacancyEdited = vacancyService.updateVacancy(vacancyById.get(), VacancyDto);

        return ResponseEntity.ok(VacancyMapper.toVacancyResponse(vacancyEdited));
    }

    // Deleta Vaga de Emprego
    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<?> deleteVacancy(@PathVariable Long vacancyId, Authentication authentication) {
        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserEntity user = userOpt.get();

        Optional<VacancyEntity> vacancyById = vacancyService.getVacancyById(vacancyId);

        if (vacancyById.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<UserVacancyEntity> userAndVacancyRelation = userVacancyService.findByUserAndVacancy(user, vacancyById.get());

        if (userAndVacancyRelation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        vacancyService.deleteVacancyById(vacancyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}


