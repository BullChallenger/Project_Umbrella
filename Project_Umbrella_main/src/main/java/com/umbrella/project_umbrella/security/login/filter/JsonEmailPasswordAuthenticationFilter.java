package com.umbrella.project_umbrella.security.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.project_umbrella.dto.user.UserRequestLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JsonEmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login";

    private static final String HTTP_METHOD = "POST";

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER
            = new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);

    private static final String CONTENT_TYPE = "application/json";

    public JsonEmailPasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException,
                                                                                        IOException, ServletException
    {
//        log.info("JsonEmailPasswordAuthenticationFilter HttpServletRequest requestMethod = {}", request.getMethod());
//        log.info("JsonEmailPasswordAuthenticationFilter HttpServletRequest requestContentType = {}", request.getContentType());
//
//        log.info("JsonEmailPasswordAuthenticationFilter HttpServletRequest request = {}", request);
//        log.info("JsonEmailPasswordAuthenticationFilter HttpServletResponse response = {}", response);

        if (!request.getMethod().equals(HTTP_METHOD)) {
            log.error("POST 요청이 아닙니다.");
            throw new AuthenticationServiceException("Authentication method not supported : " + request.getMethod());
        } else if (!request.getContentType().equals(CONTENT_TYPE)) {
            log.error("JSON 형식의 요청이 아닙니다.");
            log.error("Content Type : " + request.getContentType());
            throw new AuthenticationServiceException("Authentication method not supported : " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        UserRequestLoginDto userLoginDto = objectMapper.readValue(messageBody, UserRequestLoginDto.class);

        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();

//        log.info("attemptAuthentication email  = {}", email);
//        log.info("attemptAuthentication password = {}", password);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
