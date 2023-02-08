package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoDto {

    private final String email;

    private final String name;

    private final String nickName;

    private final Integer age;

    @Builder
    public UserInfoDto(User findUser) {
        this.email = findUser.getEmail();
        this.name = findUser.getName();
        this.nickName = findUser.getNickName();
        this.age = findUser.getAge();
    }
}
