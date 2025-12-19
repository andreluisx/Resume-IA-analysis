package dev.andre.ResumeAiAnalysis.VacancyUser;

import dev.andre.ResumeAiAnalysis.ImplementationAi.AIEntity;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserVacancyRepository extends JpaRepository<UserVacancyEntity, Long> {

    Page<UserVacancyEntity> findByUser(UserEntity user, Pageable pageable);

    boolean existsByUserAndVacancy(UserEntity user, VacancyEntity vacancy);

    Optional<List<UserVacancyEntity>> findOneByUserAndVacancy(UserEntity user, VacancyEntity vacancy);

}

