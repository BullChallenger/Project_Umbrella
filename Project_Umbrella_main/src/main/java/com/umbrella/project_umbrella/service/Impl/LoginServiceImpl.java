package com.umbrella.project_umbrella.service.Impl;

import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.security.userDetails.UserContext;
import com.umbrella.project_umbrella.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

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

        String role = user.getRole().name();

        List<GrantedAuthority> authorities = new ArrayList<>();
        Assert.isTrue(!role.startsWith("ROLE_"),
                () -> role + " cannot start with ROLE_ (it is automatically added)");
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return new UserContext(user, authorities);
    }
}
