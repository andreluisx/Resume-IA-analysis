package dev.andre.ResumeAiAnalysis.VacancyUser;


import dev.andre.ResumeAiAnalysis.Auth.Exceptions.UnauthenticatedUser;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.User.UserService;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserVacancyService {
    final private UserVacancyRepository userVacancyRepository;
    final private UserService userService;
    public UserVacancyService(UserVacancyRepository userVacancyRepository,  UserService userService) {
        this.userVacancyRepository = userVacancyRepository;
        this.userService = userService;
    }

    public Optional<UserVacancyEntity> findById(Long userVacancyId){
        return userVacancyRepository.findById(userVacancyId);
    }

    public UserVacancyEntity save(UserVacancyEntity entity) {
        return userVacancyRepository.save(entity);
    }

    public Page<UserVacancyEntity> findByUser(Authentication authentication, Pageable pageable) {
        Optional<UserEntity> userOpt = userService.getUser(authentication);

        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }

        UserEntity user = userOpt.get();

        return userVacancyRepository.findByUser(user, pageable);

    }

    public boolean existUserVacancyRelation (UserEntity user, VacancyEntity vacancy){
        return userVacancyRepository.existsByUserAndVacancy(user, vacancy);
    }

    public Optional<List<UserVacancyEntity>> findByUserAndVacancy(UserEntity user, VacancyEntity vacancy) {
        return userVacancyRepository.findByUserAndVacancy(user, vacancy);
    }

    public void deleteById(Long userVacancyId){
        userVacancyRepository.deleteById(userVacancyId);
    }
}
