package dev.andre.ResumeAiAnalysis.ImplementationAi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiRepository extends JpaRepository<AIEntity, Long> {
    Optional<AIEntity> findByUserVacancy_Id(Long userVacancyId);
}
