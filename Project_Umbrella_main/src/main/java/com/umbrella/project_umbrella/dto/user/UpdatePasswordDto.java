package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdatePasswordDto {

    @NotBlank(message = "기존에 사용하고 있던 비밀번호는 필수 입력 값입니다.")
    private final String checkPassword;

    @NotBlank(message = "변경하고자 하는 비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$")
    // 비밀번호는 8 ~ 30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.
    private final String newPassword;

    @Builder
    public UpdatePasswordDto(String checkPassword, String newPassword) {
        Assert.hasText(checkPassword, "checkPassword must not be blank");
        Assert.hasText(newPassword, "checkPassword must not be blank");

        this.checkPassword = checkPassword;
        this.newPassword = newPassword;
    }
}
