package dev.andre.ResumeAiAnalysis.Vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacancyRepository extends JpaRepository<VacancyEntity, Long> {
    List<VacancyEntity> findByTitleContainingIgnoreCase(String title);

    // Exemplo: buscar apenas vagas ativas
    List<VacancyEntity> findByActiveTrue();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE VacancyEntity v SET v.applications = v.applications + 1 WHERE v.id = :id")
    void incrementApplications(@Param("id") Long id);

}