package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserUpdateDto {
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    private String nickName;
    @NotBlank(message = "실명은 필수 입력 값입니다.")
    private String mName;
    @NotNull(message = "나이는 필수 입력 값입니다.")
    private Integer age;
    @Builder
    public UserUpdateDto(String password, String nickName, String mName, Integer age) {
        this.password = password;
        this.nickName = nickName;
        this.mName = mName;
        this.age = age;
    }

}
