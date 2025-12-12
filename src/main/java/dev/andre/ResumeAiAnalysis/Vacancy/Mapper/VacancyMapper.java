package dev.andre.ResumeAiAnalysis.Vacancy.Mapper;

import dev.andre.ResumeAiAnalysis.ImplementationAi.Responses.VacancyToAi;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.UserVacancyRelationDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyRequestDto;
import dev.andre.ResumeAiAnalysis.Vacancy.Dtos.VacancyResponseDto;
import dev.andre.ResumeAiAnalysis.Vacancy.VacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class VacancyMapper {

    public VacancyEntity toVacancyEntity(VacancyRequestDto dto) {
        return VacancyEntity.builder()
                .title(dto.title())
                .description(dto.description())
                .essential(dto.essential())
                .differential(dto.differential())
                .build();
    }

    public VacancyResponseDto toVacancyResponse(VacancyEntity entity) {
        return VacancyResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .essential(entity.getEssential())
                .differential(entity.getDifferential())
                .applications(entity.getApplications())
                .active(entity.isActive())
                .approvals(entity.getApprovals())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public VacancyToAi toVacancyToAi(VacancyEntity vacancyEntity) {

        // FORÇA CARREGAMENTO — evita LazyInitializationException
        List<String> essential = vacancyEntity.getEssential() != null
                ? new ArrayList<>(vacancyEntity.getEssential())
                : List.of();

        List<String> differential = vacancyEntity.getDifferential() != null
                ? new ArrayList<>(vacancyEntity.getDifferential())
                : List.of();

        return VacancyToAi.builder()
                .title(vacancyEntity.getTitle())
                .description(vacancyEntity.getDescription())
                .essential(essential)
                .differential(differential)
                .build();
    }


    public UserVacancyRelationDto toRelationResponse(UserVacancyEntity relation) {
        VacancyEntity v = relation.getVacancy();

        return UserVacancyRelationDto.builder()
                .id(v.getId())
                .title(v.getTitle())
                .description(v.getDescription())
                .essential(v.getEssential())
                .differential(v.getDifferential())
                .applications(v.getApplications())
                .approvals(v.getApprovals())
                .active(v.isActive())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())

                // Dados da relação
                .userRole(relation.getRole())
                .status(relation.getStatus())
                .userLinkedAt(relation.getCreatedAt())
                .build();
    }

}
