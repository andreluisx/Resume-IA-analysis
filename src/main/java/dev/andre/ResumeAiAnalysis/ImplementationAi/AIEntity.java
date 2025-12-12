package dev.andre.ResumeAiAnalysis.ImplementationAi;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.andre.ResumeAiAnalysis.ImplementationAi.Enums.Status;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Entity
@Table(name="ai_response")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"userVacancy"})
public class AIEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // descrição para o recrutador resumida do curriculo
    @Column(nullable = false)
    private String application_description;

    // o uq o aplicante da vaga precisa ter no curriculo para conseguir ser aprovado
    @Column(nullable = false)
    private String improvements_to_this_vacancy;

    // situação da analise da vaga
    @Column(nullable = false)
    private Status status = Status.PENDING;

    // coisas que tem no curriculo e tem no essencial da vaga
    @Column(nullable = true)
    private List<String> essential_matches;

    // coisas que tem no curriculo e tem no diferencial da vaga
    @Column(nullable = true)
    private List<String> differential_matches;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_vacancy_id", nullable = false, unique = true)
    private UserVacancyEntity userVacancy;

}
