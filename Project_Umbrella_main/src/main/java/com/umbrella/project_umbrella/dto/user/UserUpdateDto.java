package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserUpdateDto {
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private Optional<String> nickName;
    @NotBlank(message = "실명은 필수 입력 값입니다.")
    private Optional<String> mName;
    @NotNull(message = "나이는 필수 입력 값입니다.")
    private Optional<Integer> age;

    @Builder
    public UserUpdateDto(Optional<String> nickName,
                         Optional<String> mName,
                         Optional<Integer> age) {
        this.nickName = nickName;
        this.mName = mName;
        this.age = age;
    }

}
