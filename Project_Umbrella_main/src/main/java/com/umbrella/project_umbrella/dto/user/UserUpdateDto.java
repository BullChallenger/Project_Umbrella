package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Optional<String> nickName;

    private Optional<String> name;

    private Optional<Integer> age;

    @Builder
    public UserUpdateDto(Optional<String> nickName,
                         Optional<String> name,
                         Optional<Integer> age) {
        this.nickName = nickName;
        this.name = name;
        this.age = age;
    }
}
