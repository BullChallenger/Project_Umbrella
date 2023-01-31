package com.umbrella.project_umbrella.service.Impl;

import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.service.JwtService;

import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.util.*;

@Transactional
@Service
@Setter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    private static final String ISSUER = "Umbrella";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    @Override
    public String createAccessToken(String email, String alphaKey) {
        String secretKey = createSecretKey(alphaKey);

        Key signingKey = createSigningKey(secretKey, SIGNATURE_ALGORITHM);

        return Jwts.builder()
                .signWith(signingKey, SIGNATURE_ALGORITHM)
                .setSubject(email)
                .setIssuer(ISSUER)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
                .claim(EMAIL_CLAIM, email)
                .compact();
    }

    @Override
    public String createRefreshToken(String email, String alphaKey) {
        String secretKey = createSecretKey(alphaKey);

        Key signingKey = createSigningKey(secretKey, SIGNATURE_ALGORITHM);

        return Jwts.builder()
                .signWith(signingKey, SIGNATURE_ALGORITHM)
                .setSubject(email)
                .setIssuer(ISSUER)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000))
                .compact();
    }

    @Override
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> user.updateRefreshToken(refreshToken),
                () -> new EntityNotFoundException("해당 이메일을 가진 계정이 존재하지 않습니다.")
        );
    }

    @Override
    public void destroyRefreshToken(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(
                User::destroyRefreshToken,
                () -> new EntityNotFoundException("해당 이메일을 가진 계정이 존재하지 않습니다.")
        );
    }

    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String>  extractRefreshToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));    }

    @Override
    public Optional<String> extractEmail(String token, String alphaKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(createSecretKey(alphaKey)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.ofNullable(claims.get(EMAIL_CLAIM, String.class));
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다.", e);
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public Optional<String> extractSubject(String token, String alphaKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(createSecretKey(alphaKey)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.ofNullable(claims.getSubject());
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다.", e);
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    @Override
        public boolean isTokenValid(String alphaKey, String token) {
        try {
            Jwts.parserBuilder().setSigningKey(createSigningKey(createSecretKey(alphaKey), SIGNATURE_ALGORITHM))
                    .build().parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");

            throw e;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다.");
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    private String createSecretKey(String alphaKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(secret).append(alphaKey);

        String secretKey = new String(sb);

        return secretKey;
    }

    private Key createSigningKey(String secretKey, SignatureAlgorithm signatureAlgorithm) {

        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKey);

        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        return signingKey;
    }
}
