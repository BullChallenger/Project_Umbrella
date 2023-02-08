package com.umbrella.project_umbrella.controller;

import com.umbrella.project_umbrella.dto.user.*;
import com.umbrella.project_umbrella.security.utils.SecurityUtil;
import com.umbrella.project_umbrella.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody UserSignUpDto userSignUpDto) {
        log.info("UserController userSignUpDto = {}", userSignUpDto);
        userService.signUp(userSignUpDto);
    }

    @PutMapping(value = "/user/update/info")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserInfo(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        userService.update(userUpdateDto);
    }

    @PutMapping(value = "/user/update/password")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserPassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        userService.updatePassword(updatePasswordDto.getCheckPassword(), updatePasswordDto.getNewPassword());
    }

    @DeleteMapping(value = "/user/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@Valid @RequestBody WithdrawUserDto withdrawUserDto) {
        userService.withdraw(withdrawUserDto.getPassword());
    }

    @GetMapping(value = "/user/{userId}/info")
    public ResponseEntity getInfo(@Valid @PathVariable("userId") Long id) {
        UserInfoDto userInfoDto = userService.getInfo(id);

        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }

    @GetMapping(value = "/user/info")
    public ResponseEntity getMyInfo() {
        UserInfoDto userInfoDto = userService.getMyInfo();

        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }
}
