package dev.andre.ResumeAiAnalysis.Vacancy;

import dev.andre.ResumeAiAnalysis.Application.ApplicationEntity;
import dev.andre.ResumeAiAnalysis.Application.ApplicationService;
import dev.andre.ResumeAiAnalysis.Auth.Exceptions.UnauthenticatedUser;
import dev.andre.ResumeAiAnalysis.Enums.ApplicationStatus;
import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import dev.andre.ResumeAiAnalysis.ExceptionHandler.InternalServerError;
import dev.andre.ResumeAiAnalysis.ExceptionHandler.NotFoundException;
import dev.andre.ResumeAiAnalysis.ImplementationAi.AIEntity;
import dev.andre.ResumeAiAnalysis.ImplementationAi.AiRepository;
import dev.andre.ResumeAiAnalysis.ImplementationAi.AiService;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.User.UserService;
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
    final private AiService implementationAiService;
    final private AiRepository aiRepository;

    public VacancyController(AiService implementationAiService, VacancyService vacancyService, UserService userService, UserVacancyService userVacancyService, ApplicationService applicationService, AiRepository aiRepository) {
        this.vacancyService = vacancyService;
        this.userService = userService;
        this.userVacancyService = userVacancyService;
        this.applicationService = applicationService;
        this.implementationAiService = implementationAiService;
        this.aiRepository = aiRepository;
    }

    // Criar Vaga
    @PostMapping
    public ResponseEntity<VacancyResponseDto> createVacancy(
            @RequestBody VacancyRequestDto vacancyRequestDto,
            Authentication authentication) {

        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
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
    public ResponseEntity<List<VacancyResponseDto>> Allvacancies(Authentication authentication) {

        List<VacancyResponseDto> allVacancies = vacancyService.getAllVacancies().stream().map((vacancyEntity) -> VacancyMapper.toVacancyResponse(vacancyEntity)).toList();
        return ResponseEntity.ok(allVacancies);
    }

    // Busca "Minha Vagas"
    @GetMapping("/my")
    public ResponseEntity<List<UserVacancyRelationDto>> myVacancies(Authentication authentication) {
        Optional<UserEntity> userOpt = userService.getUser(authentication);

        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }

        UserEntity user = userOpt.get();

        //  Busca todas as relações (user_vacancies) desse usuário
        List<UserVacancyEntity> userVacancies = userVacancyService.findByUser(user);

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

    // Aplicar a vaga
    @PostMapping("/application/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> applyToVacancy(
            @PathVariable Long vacancyId,
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        // ----- Autenticação -----
        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }
        UserEntity user = userOpt.get();

        // ----- Validação do arquivo -----
        if (file == null || file.isEmpty()) {
            throw new FileEmptyException("Arquivo não pode estar vazio");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileTypeException("Apenas arquivos PDF são aceitos");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new FileTooLargeException("Arquivo excede o limite de 5MB");
        }

        // ----- Validação da vaga -----
        Optional<VacancyEntity> vacancyOpt = vacancyService.getVacancyById(vacancyId);
        if (vacancyOpt.isEmpty()) {
            throw new NotFoundException("Vaga não Encontrada");
        }
        VacancyEntity vacancy = vacancyOpt.get();

        // ----- Verifica se já está aplicada -----
        if (userVacancyService.existUserVacancyRelation(user, vacancy)) {
            throw new ConflictException("Usuário já aplicou a essa vaga");
        }

        // ----- Salvar arquivo fisicamente -----
        String uploadDir = "uploads/applications/";
        String savedName;
        Path target;

        try {
            Files.createDirectories(Paths.get(uploadDir));

            String original = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String ext = original.contains(".")
                    ? original.substring(original.lastIndexOf('.'))
                    : "";

            savedName = UUID.randomUUID().toString() + ext;
            target = Paths.get(uploadDir).resolve(savedName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new InternalServerError("Erro ao salvar arquivo: " + e);
        }

        // ----- Criar relação User-Vacancy -----

        UserVacancyEntity userVacancy = UserVacancyEntity.builder()
                .vacancy(vacancy)
                .user(user)
                .role(VacancyRole.APPLICANT)
                .status(ApplicationStatus.PENDING)
                .build();
        userVacancyService.save(userVacancy);

        // ----- Processar o arquivo AI -----
        try {
            AIEntity aiEntity = implementationAiService.gerarTextoComPdf(file, vacancy, userVacancy);
            aiRepository.save(aiEntity);
        } catch (Exception e) {
            throw new InternalServerError("Erro ao processar o arquivo com IA: " + e.getMessage());
        }

        // ----- Salvar metadados do arquivo -----
        ApplicationEntity attachment = ApplicationEntity.builder()
                .originalName(file.getOriginalFilename())
                .filename(savedName)
                .contentType(file.getContentType())
                .size(file.getSize())
                .path(target.toString())
                .userVacancy(userVacancy)
                .build();
        applicationService.save(attachment);

        // ----- Incrementar contador -----
        vacancyService.incrementApplications(vacancyId);

        // ----- Retornar vaga atualizada -----
        VacancyEntity updatedVacancy = vacancyService.getVacancyById(vacancyId).get();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VacancyMapper.toVacancyResponse(updatedVacancy));
    }


    // Edita Vaga de Emprego
    @PutMapping("/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> editVacancy(@PathVariable Long vacancyId, @RequestBody VacancyRequestDto VacancyDto, Authentication authentication) {
        Optional<UserEntity> userOpt = userService.getUser(authentication);

        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }
        UserEntity user = userOpt.get();

        Optional<VacancyEntity> vacancyById = vacancyService.getVacancyById(vacancyId);

        if (vacancyById.isEmpty()) {
            throw new NotFoundException("Vacancy não encontrada");
        }

        Optional<UserVacancyEntity> userVacancyRelation = userVacancyService.findByUserAndVacancy(user, vacancyById.get());

        if (userVacancyRelation.isEmpty() || userVacancyRelation.get().getRole() == VacancyRole.APPLICANT) {
            throw new UserCannotAccessOrDoThat("Usuario não tem autorização para editar essa vaga");
        }

        VacancyEntity vacancyEdited = vacancyService.updateVacancy(vacancyById.get(), VacancyDto);

        return ResponseEntity.ok(VacancyMapper.toVacancyResponse(vacancyEdited));
    }

    // Deleta Vaga de Emprego
    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<?> deleteVacancy(@PathVariable Long vacancyId, Authentication authentication) {
        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }
        UserEntity user = userOpt.get();

        Optional<VacancyEntity> vacancyById = vacancyService.getVacancyById(vacancyId);

        if (vacancyById.isEmpty()) {
            throw new NotFoundException("Vacancy não encontrada");
        }

        Optional<UserVacancyEntity> userAndVacancyRelation = userVacancyService.findByUserAndVacancy(user, vacancyById.get());

        if (userAndVacancyRelation.isEmpty()) {
            throw new UserCannotAccessOrDoThat("Usuario não tem autorização para excluir essa vaga");
        }

        vacancyService.deleteVacancyById(vacancyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}


