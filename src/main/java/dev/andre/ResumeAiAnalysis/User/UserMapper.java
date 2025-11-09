package dev.andre.ResumeAiAnalysis.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserEntity toUser(UserRequest userRequest){
        return UserEntity.builder()
                .email(userRequest.email())
                .name(userRequest.name())
                .password(userRequest.password())
                .build();
    }

    public static UserResponse toUserResponse(UserEntity user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

}
