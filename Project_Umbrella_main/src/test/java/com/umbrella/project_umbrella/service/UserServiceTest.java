package com.umbrella.project_umbrella.service;

import com.umbrella.project_umbrella.constant.Role;
import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.dto.user.UserInfoDto;
import com.umbrella.project_umbrella.dto.user.UserSignUpDto;
import com.umbrella.project_umbrella.dto.user.UserUpdateDto;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.security.login.utils.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.lang.annotation.Documented;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    String password = "codePirates0204";

    private UserSignUpDto createUserSignUpDto() {
        return new UserSignUpDto("test@test.com", "테스트계정", password, "홍길동", 22);
    }

    private UserSignUpDto setAuthenticationInContext() {
        UserSignUpDto userSignUpDto = createUserSignUpDto();

        userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                org.springframework.security.core.userdetails.User.builder()
                                                                    .username(userSignUpDto.getNickName())
                                                                    .password(userSignUpDto.getPassword())
                                                                    .roles(Role.USER.name())
                .build(), null, null));

        SecurityContextHolder.setContext(securityContext);

        return userSignUpDto;
    }

    @AfterEach
    private void clearAuthentication() {
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    @Test
    @DisplayName("[SUCCESS]_회원가입_성공")
    public void signUpTest() {
        // given
        UserSignUpDto userSignUpDto = createUserSignUpDto();

        // when
        userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        // then
        User savedUser = userRepository.findByNickName(userSignUpDto.getNickName()).orElseThrow(
                () -> new EntityNotFoundException("해당 닉네임을 사용하는 계정을 찾을 수 없습니다.")
        );

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(savedUser.getNickName()).isEqualTo(userSignUpDto.getNickName());
        assertThat(savedUser.getMName()).isEqualTo(userSignUpDto.getMName());
        assertThat(savedUser.getAge()).isEqualTo(userSignUpDto.getAge());
        assertThat(savedUser.getRole()).isSameAs(Role.USER);
    }

    @Test
    @DisplayName("[FAILED]_회원가입_실패_이메일_중복")
    public void signUpExceptionTest01() {
        // given
        UserSignUpDto userSignUpDto = createUserSignUpDto();

        userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        assertThat(assertThrows(IllegalStateException.class, () -> userService.signUp(userSignUpDto)).getMessage())
                .isEqualTo("동일한 이메일을 사용하는 계정이 이미 존재합니다.");
    }

    @Test
    @DisplayName("[FAILED]_회원가입_실패_닉네임_중복")
    public void signUpExceptionTest02() {
        // given
        UserSignUpDto userSignUpDto = createUserSignUpDto();

        userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        // when, then
        assertThat(assertThrows(IllegalStateException.class, () -> userService.signUp(userSignUpDto)).getMessage())
                .isEqualTo("동일한 닉네임을 사용하는 계정이 이미 존재합니다.");
    }

    @Test
    @DisplayName("[FAILED]_회원가입_실패_존재하지_않는_필드")
    public void signUpExceptionTest03() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> new UserSignUpDto(null,
                "테스트계정01",
                passwordEncoder.encode(password),
                "홍길동",
                22));
        assertThrows(IllegalArgumentException.class, () -> new UserSignUpDto("test02@test.com",
                null,
                passwordEncoder.encode(password),
                "홍길동",
                22));
        assertThrows(IllegalArgumentException.class, () -> new UserSignUpDto("test03@test.com",
                "테스트계정03",
                null,
                "홍길동",
                22));
        assertThrows(IllegalArgumentException.class, () -> new UserSignUpDto("test04@test.com",
                "테스트계정04",
                passwordEncoder.encode(password),
                null,
                22));
        assertThrows(IllegalArgumentException.class, () -> new UserSignUpDto("test05@test.com",
                "테스트계정05",
                passwordEncoder.encode(password),
                "홍길동",
                null));
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_비밀번호_변경")
    public void updatePasswordTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        String changePassword = "codePirates0205";
        userService.updatePassword(password, changePassword);
        em.flush();
        em.clear();

        // then
        User findUser = userRepository.findByNickName(userSignUpDto.getNickName()).orElseThrow(
                () -> new EntityNotFoundException("해당 닉네임을 사용하지 계정이 존재하지 않습니다.")
        );

        assertThat(findUser.matchPassword(passwordEncoder, changePassword)).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_이름만_변경")
    public void updateMNameTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        String changeMName = "임꺽정";
        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.empty(),
                                                                Optional.of(changeMName),
                                                        Optional.empty());
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByNickName(SecurityUtil.getLoginUserNickName()).ifPresent(
                user -> {
                    assertThat(user.getMName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(userSignUpDto.getNickName());
                    assertThat(user.getAge()).isEqualTo(userSignUpDto.getAge());
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_닉네임만_변경")
    public void updateUserNickNameTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        String changeNickName = "변경테스트";
        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.of(changeNickName),
                Optional.empty(),
                Optional.empty());
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByNickName(SecurityUtil.getLoginUserNickName()).ifPresent(
                user -> {
                    assertThat(user.getMName()).isEqualTo(userSignUpDto.getMName());
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(userSignUpDto.getAge());
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_나이만_변경")
    public void updateUserAgeTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        int changeAge = 100;
        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.empty(),
                Optional.empty(),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByNickName(SecurityUtil.getLoginUserNickName()).ifPresent(
                user -> {
                    assertThat(user.getMName()).isEqualTo(userSignUpDto.getMName());
                    assertThat(user.getNickName()).isEqualTo(userSignUpDto.getNickName());
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_닉네임과_실명만_변경")
    public void updateUserNickNameAndMNameTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        String changeNickName = "변경테스트";
        String changeMName = "임꺽정";
        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.of(changeNickName),
                Optional.of(changeMName),
                Optional.empty());
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByNickName(SecurityUtil.getLoginUserNickName()).ifPresent(
                user -> {
                    assertThat(user.getMName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(userSignUpDto.getAge());
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_닉네임과_나이만_변경")
    public void updateUserNickNameAndAgeTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        String changeNickName = "변경테스트";
        int changeAge = 100;
        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.of(changeNickName),
                Optional.empty(),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByNickName(SecurityUtil.getLoginUserNickName()).ifPresent(
                user -> {
                    assertThat(user.getMName()).isEqualTo(userSignUpDto.getMName());
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_실명과_나이만_변경")
    public void updateUserAgeAndMNameTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        int changeAge = 100;
        String changeMName = "임꺽정";
        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.empty(),
                Optional.of(changeMName),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByNickName(SecurityUtil.getLoginUserNickName()).ifPresent(
                user -> {
                    assertThat(user.getMName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(userSignUpDto.getNickName());
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_전부_변경")
    public void updateAllTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        String changeNickName = "변경테스트";
        int changeAge = 100;
        String changeMName = "임꺽정";
        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.of(changeNickName),
                Optional.of(changeMName),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByNickName(SecurityUtil.getLoginUserNickName()).ifPresent(
                user -> {
                    assertThat(user.getMName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원탈퇴")
    public void withdrawTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        userService.withdraw(password);

        // then
        assertThat(assertThrows(EntityNotFoundException.class,
                () -> userRepository.findByEmail(userSignUpDto.getEmail()).orElseThrow(
                        () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
                )
                ).getMessage()).isEqualTo("해당 이메일을 사용하는 계정이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("[FAILED]_회원탈퇴_비밀번호_불일치")
    public void withdrawExceptionTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when, then
        assertThat(assertThrows(IllegalArgumentException.class,
                () -> userService.withdraw(password + 123)
        ).getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_조회")
    public void getUserInfoTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();
        User user = userRepository.findByEmail(userSignUpDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정을 찾을 수 없습니다.")
        );

        // when
        UserInfoDto userInfoDto = userService.getInfo(user.getId());

        // then
        assertThat(userInfoDto.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(userInfoDto.getMName()).isEqualTo(userSignUpDto.getMName());
        assertThat(userInfoDto.getAge()).isEqualTo(userSignUpDto.getAge());
        assertThat(userInfoDto.getNickName()).isEqualTo(userSignUpDto.getNickName());
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_내_정보_조회")
    public void getMyInfoTest() {
        // given
        UserSignUpDto userSignUpDto = setAuthenticationInContext();

        // when
        UserInfoDto userInfoDto = userService.getMyInfo();

        // then
        assertThat(userInfoDto.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(userInfoDto.getMName()).isEqualTo(userSignUpDto.getMName());
        assertThat(userInfoDto.getAge()).isEqualTo(userSignUpDto.getAge());
        assertThat(userInfoDto.getNickName()).isEqualTo(userSignUpDto.getNickName());
    }
}

