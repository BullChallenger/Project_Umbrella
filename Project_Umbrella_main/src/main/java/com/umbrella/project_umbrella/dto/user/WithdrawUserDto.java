package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawUserDto {

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @Builder
    public WithdrawUserDto(String password) {
        Assert.hasText(password, "password must not be null");

        this.password = password;
    }
}
