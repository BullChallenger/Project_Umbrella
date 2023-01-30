package com.umbrella.project_umbrella.dto.user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class UserLoginDto {

    @NotBlank
    @Email
    private final String email;
    @NotBlank
    private final String password;

    @Builder
    public UserLoginDto(String email, String password) {
        Assert.hasText(email, "email must not be blank");
        Assert.hasText(password, "password must not be blank");

        this.email = email;
        this.password = password;
    }
}
