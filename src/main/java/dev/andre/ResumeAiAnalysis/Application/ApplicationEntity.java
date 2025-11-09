package dev.andre.ResumeAiAnalysis.Application;

import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "application_attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;        // nome salvo (ex: uuid.pdf)
    private String originalName;    // nome original enviado
    private String contentType;
    private Long size;
    private String path;            // caminho relativo onde está salvo

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_vacancy_id", nullable = false, unique = true)
    private UserVacancyEntity userVacancy;
}


