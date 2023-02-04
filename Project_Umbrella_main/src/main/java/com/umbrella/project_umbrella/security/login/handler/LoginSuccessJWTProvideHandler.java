package com.umbrella.project_umbrella.security.login.handler;

import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final String APPLICATION_JSON = "application/json";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = extractEmail(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        userRepository.findByEmail(email).ifPresent(
                user -> user.updateRefreshToken(refreshToken)
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write("성공적으로 로그인이 완료되었습니다!");

        log.info( "로그인에 성공합니다. email: {}", email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}", accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);
    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }
}

