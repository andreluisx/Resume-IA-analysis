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
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyRequestDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.ConflictException;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.FileEmptyException;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.FileTooLargeException;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.InvalidFileTypeException;
import dev.andre.ResumeAiAnalysis.Vacancy.Mapper.VacancyMapper;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyRepository;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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

@Service
public class VacancyService {

    final private UserVacancyService userVacancyService;
    final private UserService userService;
    final private ApplicationService applicationService;
    final private AiService implementationAiService;
    final private AiRepository aiRepository;
    final private VacancyRepository vacancyRepository;

    public VacancyService(AiService implementationAiService, UserService userService, UserVacancyService userVacancyService, ApplicationService applicationService, AiRepository aiRepository, VacancyRepository vacancyRepository) {
        this.userService = userService;
        this.userVacancyService = userVacancyService;
        this.applicationService = applicationService;
        this.implementationAiService = implementationAiService;
        this.aiRepository = aiRepository;
        this.vacancyRepository = vacancyRepository;
    }

    public VacancyEntity createVacancy(VacancyEntity vacancyEntity) {
        return vacancyRepository.save(vacancyEntity);
    }

    public List<VacancyEntity> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public Optional<VacancyEntity> getVacancyById(Long id) {
        return vacancyRepository.findById(id);
    }

    public void saveVacancy(VacancyEntity vacancy) {
        vacancyRepository.save(vacancy);
    }

    public VacancyEntity updateVacancy(VacancyEntity vacancy, VacancyRequestDto dto) {
        if (dto.title() != null) vacancy.setTitle(dto.title());
        if (dto.description() != null) vacancy.setDescription(dto.description());
        if (dto.essential() != null) vacancy.setEssential(dto.essential());
        if (dto.differential() != null) vacancy.setDifferential(dto.differential());

        return vacancyRepository.save(vacancy);
    }

    public void deleteVacancyById(Long id) {
        vacancyRepository.deleteById(id);
    }

    @Transactional
    public void incrementApplications(Long vacancyId) {
        vacancyRepository.incrementApplications(vacancyId);
    }


    @Transactional
    public VacancyEntity vacancyApplication(Long vacancyId, Authentication authentication, MultipartFile file) {

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
        Optional<VacancyEntity> vacancyOpt = this.getVacancyById(vacancyId);
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
            userVacancyService.deleteById(userVacancy.getId());
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
        this.incrementApplications(vacancyId);

        // ----- Retornar vaga atualizada -----
        VacancyEntity updatedVacancy = this.getVacancyById(vacancyId).get();

        return updatedVacancy;
    }
}
