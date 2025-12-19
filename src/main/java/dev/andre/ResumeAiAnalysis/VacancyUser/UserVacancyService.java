package dev.andre.ResumeAiAnalysis.VacancyUser;


import dev.andre.ResumeAiAnalysis.Auth.Exceptions.UnauthenticatedUser;
import dev.andre.ResumeAiAnalysis.ExceptionHandler.NotFoundException;
import dev.andre.ResumeAiAnalysis.ImplementationAi.AIEntity;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.User.UserService;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyService;
import dev.andre.ResumeAiAnalysis.VacancyUser.Dto.UserVacancyMapper;
import dev.andre.ResumeAiAnalysis.VacancyUser.Dto.UserVacancyResponse;
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
    final private UserVacancyMapper userVacancyMapper;

    public UserVacancyService(UserVacancyRepository userVacancyRepository,  UserService userService, UserVacancyMapper userVacancyMapper) {
        this.userVacancyRepository = userVacancyRepository;
        this.userService = userService;
        this.userVacancyMapper = userVacancyMapper;
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
        return userVacancyRepository.findOneByUserAndVacancy(user, vacancy);
    }

    public UserVacancyResponse getUserVacancyById(Long userVacancyId, Authentication authentication){
        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }
        UserEntity user = userOpt.get();

        Optional<UserVacancyEntity> userVacancy = userVacancyRepository.findById(userVacancyId);

        if(userVacancy.isEmpty()){
            throw new NotFoundException("Relação de Vaga e Usuário não encontrada");
        }

        return userVacancyMapper.toUserVacancyResponse(userVacancy.get());

    }

    public void deleteById(Long userVacancyId){
        userVacancyRepository.deleteById(userVacancyId);
    }
}
