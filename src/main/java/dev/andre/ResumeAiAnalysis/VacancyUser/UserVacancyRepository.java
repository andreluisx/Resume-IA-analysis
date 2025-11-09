package dev.andre.ResumeAiAnalysis.VacancyUser;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserVacancyRepository extends JpaRepository<UserVacancyEntity, Long> {

    List<UserVacancyEntity> findByUser(UserEntity user);

    // Busca todas as vagas que um usuário está relacionado (qualquer role)
    @Query("SELECT uv FROM UserVacancyEntity uv JOIN FETCH uv.vacancy WHERE uv.user.id = :userId")
    List<UserVacancyEntity> findAllByUserId(@Param("userId") Long userId);

    // Busca todas as relações de uma vaga específica
    @Query("SELECT uv FROM UserVacancyEntity uv JOIN FETCH uv.user WHERE uv.vacancy.id = :vacancyId")
    List<UserVacancyEntity> findAllByVacancyId(@Param("vacancyId") Long vacancyId);

    // Verifica se já existe vínculo entre usuário e vaga
    @Query("SELECT uv FROM UserVacancyEntity uv WHERE uv.user.id = :userId AND uv.vacancy.id = :vacancyId")
    Optional<UserVacancyEntity> findByUserIdAndVacancyId(@Param("userId") Long userId, @Param("vacancyId") Long vacancyId);

    boolean existsByUserAndVacancy(UserEntity user, VacancyEntity vacancy);

    Optional<UserVacancyEntity> findByUserAndVacancy(UserEntity user, VacancyEntity vacancy);

    // filtrar por role específica
    @Query("SELECT uv FROM UserVacancyEntity uv JOIN FETCH uv.vacancy WHERE uv.user.id = :userId AND uv.role = :role")
    List<UserVacancyEntity> findAllByUserIdAndRole(@Param("userId") Long userId, @Param("role") Enum role);
}

