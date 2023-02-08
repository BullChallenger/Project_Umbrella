package com.umbrella.project_umbrella.service.Impl;

import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.dto.user.UserInfoDto;
import com.umbrella.project_umbrella.dto.user.UserSignUpDto;
import com.umbrella.project_umbrella.dto.user.UserUpdateDto;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.security.utils.SecurityUtil;
import com.umbrella.project_umbrella.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtil securityUtil;

    private User userSignUpDtoToEntity(UserSignUpDto userSignUpDto) {
        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .nickName(userSignUpDto.getNickName())
                .password(userSignUpDto.getPassword())
                .name(userSignUpDto.getName())
                .age(userSignUpDto.getAge())
                .build();

        return user;
    }

    private User getUserByEmail() {
        return userRepository.findByEmail(securityUtil.getLoginUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 가진 사용자가 존재하지 않습니다")
        );
    }

    @Override
    public void signUp(UserSignUpDto userSignUpDto) {
        User signUpUser = userSignUpDtoToEntity(userSignUpDto);

        signUpUser.addUserAuthorities();
        signUpUser.encodePassword(passwordEncoder);

//        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
//            throw new IllegalStateException("동일한 이메일을 사용하는 계정이 이미 존재합니다.");
//        }
        if (userRepository.findByNickName(userSignUpDto.getNickName()).isPresent()) {
            throw new IllegalStateException("동일한 닉네임을 사용하는 계정이 이미 존재합니다.");
        }

        userRepository.save(signUpUser);
    }

    @Override
    public void update(UserUpdateDto userUpdateDto) {
        User wantUpdateUser = getUserByEmail();

        wantUpdateUser.updateUser(userUpdateDto);
    }

    @Override
    public void updatePassword(String checkPassword, String newPassword) {
        User updatePasswordUser = getUserByEmail();

        if (!updatePasswordUser.matchPassword(passwordEncoder, checkPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        updatePasswordUser.updatePassword(passwordEncoder, newPassword);
    }

    @Override
    public void withdraw(String checkPassword) {
        User withdrawUser = userRepository.findByEmail(securityUtil.getLoginUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );

        if (!withdrawUser.matchPassword(passwordEncoder, checkPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(withdrawUser);
    }

    @Override
    public UserInfoDto getInfo(Long id) {
        User findUser = userRepository.findById(id).orElseThrow(
                () ->  new IllegalArgumentException("해당 정보를 가진 회원이 존재하지 않습니다.")
        );

        return new UserInfoDto(findUser);
    }

    @Override
    public UserInfoDto getMyInfo() {
        User findUser = getUserByEmail();

        return new UserInfoDto(findUser);
    }
}
