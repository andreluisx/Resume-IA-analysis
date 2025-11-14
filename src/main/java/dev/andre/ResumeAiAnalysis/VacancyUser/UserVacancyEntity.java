package dev.andre.ResumeAiAnalysis.VacancyUser;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.andre.ResumeAiAnalysis.Application.ApplicationEntity;
import dev.andre.ResumeAiAnalysis.Enums.ApplicationStatus;
import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_vacancies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVacancyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id", nullable = false)
    private VacancyEntity vacancy;

    @Enumerated(EnumType.STRING)
    @Column(length = 40, nullable = false)
    private VacancyRole role;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonManagedReference
    @OneToOne(mappedBy = "userVacancy", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicationEntity applicationResume;

}
