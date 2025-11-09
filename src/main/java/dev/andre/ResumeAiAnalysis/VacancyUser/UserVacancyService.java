package dev.andre.ResumeAiAnalysis.VacancyUser;


import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserVacancyService {
    final private UserVacancyRepository userVacancyRepository;
    public UserVacancyService(UserVacancyRepository userVacancyRepository){
        this.userVacancyRepository = userVacancyRepository;
    }

    public UserVacancyEntity save(UserVacancyEntity entity) {
        return userVacancyRepository.save(entity);
    }

    public List<UserVacancyEntity> findByUser(UserEntity email){
        return userVacancyRepository.findByUser(email);
    }

    public boolean existUserVacancyRelation (UserEntity user, VacancyEntity vacancy){
        return userVacancyRepository.existsByUserAndVacancy(user, vacancy);
    }

    public Optional<UserVacancyEntity> findByUserAndVacancy(UserEntity user, VacancyEntity vacancy) {
        return userVacancyRepository.findByUserAndVacancy(user, vacancy);
    }


}
