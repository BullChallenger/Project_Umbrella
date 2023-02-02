package com.umbrella.project_umbrella.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.project_umbrella.constant.Role;
import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtAuthenticationProcessingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static final String LOGIN_URL = "/login";
    private static final String URL_ADDRESS = "/url/address";

    private String email = "test@test.com";
    private String password = "codePirates0202";

    @BeforeEach
    private void init() {
        userRepository.save(
                User.builder()
                        .email(email)
                        .password(delegatingPasswordEncoder.encode(password))
                        .mName("홍길동")
                        .nickName("테스트 계정입니다.")
                        .role(Role.USER)
                        .age(22)
                        .build()
        );

        em.flush();
        em.clear();
    }

    private Map getUsernamePasswordMap(String email, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_EMAIL, email);
        map.put(KEY_PASSWORD, password);
        return map;
    }


    private Map getAccessAndRefreshToken() throws Exception {

        Map<String, String> map = getUsernamePasswordMap(email, password);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader,accessToken);
        tokenMap.put(refreshHeader,refreshToken);

        return tokenMap;
    }

    @Test
    @DisplayName("[FAILED]_엑세스_토큰_리프레쉬_토큰_모두_존재하지_않음")
    public void forbiddenTest() throws Exception {
        // when, then
        mockMvc.perform(get(URL_ADDRESS))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[FAILED]_유효한_엑세스_토큰만_존재")
    public void validAccessTokenTest() throws Exception {
        // given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        System.out.println("ACCESS TOKEN : " + accessToken);

        // when, then
        mockMvc.perform(get(URL_ADDRESS).header(accessHeader, BEARER + accessToken))
                .andExpectAll(status().isNotFound());
    }

    @Test
    @DisplayName("[FAILED]_유효하지_않은_엑세스_토큰만_존재")
    public void nonValidAccessTokenTest() throws Exception {
        // given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        System.out.println("ACCESS TOKEN : " + accessToken);

        // when, then
        mockMvc.perform(get(URL_ADDRESS).header(accessHeader, BEARER + accessToken))
                .andExpectAll(status().isNotFound());
    }

    @Test
    @DisplayName("[SUCCESS]_유효한_리프레쉬_토큰으로_엑세스_토큰_재발급")
    public void reIssueAccessTokenWithValidRefreshTokenTest() throws Exception {
        // given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        // when, then
        MvcResult result = mockMvc.perform(get(URL_ADDRESS)
                        .header(refreshHeader, BEARER + refreshToken))
                        .andExpect(status().isOk()).andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);

        String subject = jwtService.extractSubject(accessToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        assertThat(subject).isEqualTo(email);
    }

    @Test
    @DisplayName("[FAILED]_유효하지_않은_리프레쉬_토큰으로_엑세스_토큰_재발급")
    public void reIssueAccessTokenWithNonValidRefreshTokenTest() throws Exception {
        // given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        // when
        mockMvc.perform(get(URL_ADDRESS).header(refreshHeader, refreshToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get(URL_ADDRESS).header(refreshHeader, BEARER + refreshToken + "WrongData"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[SUCCESS]_유효한_리프레쉬_토큰으로_유효한_엑세스_토큰_갱신")
    public void refreshValidAccessTokenWithValidRefreshTokenTest() throws Exception {
        // given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        // when
        MvcResult result = mockMvc.perform(get(URL_ADDRESS)
                                    .header(refreshHeader, BEARER + refreshToken)
                                    .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = jwtService.extractSubject(responseAccessToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        assertThat(subject).isEqualTo(email);
        assertThat(responseRefreshToken).isNull();
    }

    @Test
    @DisplayName("[SUCCESS]_유효한_리프레쉬_토큰으로_유효하지_않은_엑세스_토큰_갱신")
    public void refreshNonValidAccessTokenWithValidRefreshTokenTest() throws Exception {
        // given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        // when
        MvcResult result = mockMvc.perform(get(URL_ADDRESS)
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = jwtService.extractSubject(responseAccessToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        assertThat(subject).isEqualTo(email);
        assertThat(responseRefreshToken).isNull();
    }

    @Test
    @DisplayName("[FAILED]_유효하지_않은_리프레쉬_토큰과_유효한_엑세스_토큰_전송")
    public void sendAccessTokenAndNonValidRefreshTokenTest() throws Exception {
        // given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        // when
        MvcResult result = mockMvc.perform(get(URL_ADDRESS)
                        .header(refreshHeader, refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull();
        assertThat(responseRefreshToken).isNull();
    }

    @Test
    @DisplayName("[FAILED]_유효하지_않은_리프레쉬_토큰과_엑세스_토큰_전송")
    public void sendNonValidAccessTokenAndNonValidRefreshTokenTest() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(URL_ADDRESS)
                        .header(refreshHeader, BEARER + refreshToken + "wrongData")
                        .header(accessHeader, BEARER + accessToken + "wrongData"))
                .andExpect(status().isForbidden())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull();
        assertThat(responseRefreshToken).isNull();
    }

    @Test
    @DisplayName("[SUCCESS]_로그인_페이지는_토큰_없이_통과")
    public void loginUrlNonPassTest() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(email, password);

        //when, then
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                        .andExpect(status().isOk())
                        .andReturn();
    }
}
