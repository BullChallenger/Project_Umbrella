package com.umbrella.project_umbrella.repository;

import com.umbrella.project_umbrella.constant.Role;
import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.dto.user.UserUpdateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @AfterEach
    private void after() {
        em.clear();
    }

    private User createUser() {
        User user = User.builder()
                .email("test@test.com")
                .password("12345")
                .mName("홍길동")
                .nickName("테스트 계정입니다")
                .role(Role.USER)
                .age(22)
                .build();

        return user;
    }

    @Test
    @DisplayName("[SUCCESS]_회원_저장")
    public void saveUserTest() {
        // given
        User user = createUser();

        // when
        User savedUser = userRepository.save(user);

        // then
        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new RuntimeException("해당 이메일을 가진 회원이 존재하지 않습니다.")
        );

        assertThat(findUser).isSameAs(savedUser);
        assertThat(findUser).isSameAs(user);
    }

    @Test
    @DisplayName("[FAIL]_회원_저장_이메일_없음")
    public void saveUserException01() throws Exception {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> User.builder()
                .email(null)
                .nickName("테스트 계정")
                .password("1111")
                .mName("홍길동")
                .age(22)
                .role(Role.USER)
                .build());
    }

    @Test
    @DisplayName("[FAIL]_회원_저장_닉네임_없음")
    public void saveUserException02() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> User.builder()
                .email("test@test.com")
                .password("1111")
                .mName("홍길동")
                .age(22)
                .role(Role.USER)
                .build());
    }

    @Test
    @DisplayName("[FAIL]_회원_저장_비밀번호_없음")
    public void saveUserException03() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> User.builder()
                .email("test@test.com")
                .nickName("테스트 계정")
                .mName("홍길동")
                .age(22)
                .role(Role.USER)
                .build());
    }

    @Test
    @DisplayName("[FAIL]_회원_저장_이름_없음")
    public void saveUserException04() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> User.builder()
                .email("test@test.com")
                .nickName("테스트 계정")
                .password("1111")
                .age(22)
                .role(Role.USER)
                .build());
    }

    @Test
    @DisplayName("[FAIL]_회원_저장_나이_없음")
    public void saveUserException05() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> User.builder()
                .email("test@test.com")
                .nickName("테스트 계정")
                .password("1111")
                .mName("홍길동")
                .role(Role.USER)
                .build());
    }

    @Test
    @DisplayName("[FAIL]_회원_저장_권한_없음")
    public void saveUserException06() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> User.builder()
                .email("test@test.com")
                .nickName("테스트 계정")
                .password("1111")
                .mName("홍길동")
                .age(22)
                .build());
    }

    @Test
    @DisplayName("[FAIL]_회원_저장_중복된_이메일_존재")
    public void saveUserException07() {
        //given
        User member1 = User.builder()
                .email("test@test01.com")
                .nickName("테스트 계정1")
                .password("1111")
                .mName("홍길동")
                .age(22)
                .role(Role.USER)
                .build();

        User member2 = User.builder()
                .email("test@test01.com")
                .nickName("테스트 계정2")
                .password("1111")
                .mName("임꺽정")
                .age(22)
                .role(Role.USER)
                .build();

        userRepository.save(member1);

        userRepository.save(member2);

        //when, then
        assertThrows(Exception.class, () -> em.flush());
    }

    @Test
    @DisplayName("[SUCCESS]_회원_수정")
    public void updateUserTest() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);
        em.flush();
        em.clear();

        String updatePassword = "updatePassword";
        String updateName = "updateName";
        String updateNickName = "updateNickName";
        int updateAge = 23;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .password(updatePassword)
                .nickName(updateNickName)
                .mName(updateName)
                .age(updateAge)
                .build();

        User updateUser = userRepository.findByEmail(user.getEmail()).orElseThrow(EntityNotFoundException::new);

        // when
        updateUser.updateUser(userUpdateDto);
        userRepository.save(updateUser);

        // then
        User findUpdatedUser = userRepository.findByEmail(updateUser.getEmail()).
                orElseThrow(EntityNotFoundException::new);

        assertThat(findUpdatedUser.getPassword()).isEqualTo(userUpdateDto.getPassword());
        assertThat(findUpdatedUser.getNickName()).isEqualTo(userUpdateDto.getNickName());
        assertThat(findUpdatedUser.getMName()).isEqualTo(userUpdateDto.getMName());
        assertThat(findUpdatedUser.getAge()).isEqualTo(userUpdateDto.getAge());
    }

    @Test
    @DisplayName("[SUCCESS]_회원_삭제")
    public void deleteUserTest() {

        // given
        User user = createUser();

        // when
        userRepository.delete(user);

        // then
        assertThrows(Exception.class, () -> userRepository.findByEmail(user.getEmail())
                .orElseThrow(EntityNotFoundException::new));
    }

    @Test
    @DisplayName("[SUCCESS]_회원_찾기_이메일")
    public void existByEmailTest() throws Exception {
        //given
        User user = createUser();
        userRepository.save(user);
        em.flush();
        em.clear();

        String email = user.getEmail();

        //when, then
        assertThat(userRepository.existsByEmail(email)).isTrue();
        assertThat(userRepository.existsByEmail(email +"123")).isFalse();

    }
}
