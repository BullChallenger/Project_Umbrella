package com.umbrella.project_umbrella.dto.user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class UserRequestLoginDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email
    private final String email;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private final String password;

    @Builder
    public UserRequestLoginDto(String email, String password) {
        Assert.hasText(email, "email must not be blank");
        Assert.hasText(password, "password must not be blank");

        this.email = email;
        this.password = password;
    }
}
