package com.umbrella.project_umbrella.service.Impl;

import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(
                            () -> new UsernameNotFoundException("해당 이메일을 가진 계정이 존재하지 않습니다.")
                        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
