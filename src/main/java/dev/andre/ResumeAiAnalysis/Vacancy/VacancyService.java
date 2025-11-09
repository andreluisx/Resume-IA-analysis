package dev.andre.ResumeAiAnalysis.Vacancy;


import dev.andre.ResumeAiAnalysis.User.UserService;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyRequestDto;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {

    final private VacancyRepository vacancyRepository;
    final private UserService userService;
    final private UserVacancyRepository userVacancyRepository;

    public VacancyService(VacancyRepository vacancyRepository, UserService userService, UserVacancyRepository userVacancyRepository) {
        this.vacancyRepository = vacancyRepository;
        this.userService = userService;
        this.userVacancyRepository = userVacancyRepository;
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

}
