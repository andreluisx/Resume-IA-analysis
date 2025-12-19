package dev.andre.ResumeAiAnalysis.VacancyUser.Dto;

import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

@Component
public class UserVacancyMapper {
    public UserVacancyResponse toUserVacancyResponse(UserVacancyEntity userVacancyEntity) {
        return UserVacancyResponse.builder()
                .id(userVacancyEntity.getId())
                .role(userVacancyEntity.getRole())
                .status(userVacancyEntity.getStatus())
                .created_at(userVacancyEntity.getCreatedAt())
                .updated_at(userVacancyEntity.getUpdatedAt())
                .build();
    }
}
