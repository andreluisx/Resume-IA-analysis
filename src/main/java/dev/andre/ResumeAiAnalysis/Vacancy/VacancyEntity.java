package dev.andre.ResumeAiAnalysis.Vacancy;

import dev.andre.ResumeAiAnalysis.Application.ApplicationEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vacancy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "vacancy_essential", joinColumns = @JoinColumn(name = "vacancy_id"))
    @Column(name = "essential_item")
    private List<String> essential;

    @ElementCollection
    @CollectionTable(name = "vacancy_differential", joinColumns = @JoinColumn(name = "vacancy_id"))
    @Column(name = "differential_item")
    private List<String> differential;

    private int applications;

    private int approvals;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserVacancyEntity> userVacancies = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;
}
