package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;

import javax.validation.constraints.*;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignUpDto {

    @Email
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickName;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$")
    // 비밀번호는 8 ~ 30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.
    private String password;
    @NotBlank(message = "실명은 필수 입력 값입니다.")
    @Size(min = 2)
    @Pattern(regexp = "^[A-Za-z가-힣]+$")
    // 사용자 이름은 2자 이상이면서 한글 혹은 알파벳으로만 이루어져있어야 합니다.
    private String name;
    @NotNull(message = "나이는 필수 입력 값입니다.")
    @Range(min = 0, max = 110)
    private Integer age;

//    @Builder
    public UserSignUpDto(String email, String nickName, String password, String name, Integer age) {
        Assert.hasText(email, "email must not be blank");
        Assert.hasText(nickName, "nickName must not be blank");
        Assert.hasText(password, "password must not be blank");
        Assert.hasText(name, "mName must not be blank");
        Assert.notNull(age, "age must not be null");

        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.name = name;
        this.age = age;
    }
}
