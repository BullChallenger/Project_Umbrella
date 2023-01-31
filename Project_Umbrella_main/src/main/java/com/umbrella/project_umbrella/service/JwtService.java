package com.umbrella.project_umbrella.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public interface JwtService {

    String createAccessToken(String email, String alphaKey);
    String createRefreshToken(String email, String alphaKey);

    void updateRefreshToken(String email, String refreshToken);

    void destroyRefreshToken(String email);

    void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException;

    void sendAccessToken(HttpServletResponse response, String accessToken);

    Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException;

    Optional<String> extractRefreshToken(HttpServletRequest request) throws IOException, ServletException;

    Optional<String> extractEmail(String accessToken, String alphaKey);

    Optional<String> extractSubject(String accessToken, String alphaKey);

    void setAccessTokenHeader(HttpServletResponse response, String accessToken);
    void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

    boolean isTokenValid(String alphaKey, String token);
}
