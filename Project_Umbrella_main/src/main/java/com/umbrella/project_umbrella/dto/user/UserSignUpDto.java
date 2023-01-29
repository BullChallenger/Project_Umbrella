package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserSignUpDto {

    @Email
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private final String email;
    private final String nickName;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private final String password;
    @NotBlank(message = "실명은 필수 입력 값입니다.")
    private final String mName;
    @NotNull(message = "나이는 필수 입력 값입니다.")
    private final Integer age;
    @Builder
    public UserSignUpDto(String email, String nickName, String password, String mName, Integer age) {
        Assert.hasText(email, "email must not be blank");
        Assert.hasText(password, "password must not be blank");
        Assert.hasText(mName, "mName must not be blank");
        Assert.notNull(age, "age must not be null");

        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.mName = mName;
        this.age = age;
    }

}
