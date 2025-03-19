package com.example.dosirakbe.global.config;

import com.example.dosirakbe.domain.auth.jwt.JwtFilter;
import com.example.dosirakbe.domain.auth.service.CustomOAuth2UserService;
import com.example.dosirakbe.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * packageName    : com.example.dosirakbe.global.config<br>
 * fileName       : SecurityConfig<br>
 * author         : femmefatalehaein<br>
 * date           : 11/09/24<br>
 * description    : Spring Security 설정 클래스입니다.<br>
 * ===========================================================<br>
 * DATE              AUTHOR             NOTE<br>
 * -----------------------------------------------------------<br>
 * 11/09/24        femmefatalehaein                최초 생성<br>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtUtil;

    /**
     * Spring Security 의 필터 체인을 구성합니다.
     *
     * <p>
     * 이 메서드는 CORS, CSRF, 세션 관리, OAuth2 인증, JWT 필터 등의 설정을 포함한
     * Spring Security 의 전체 필터 체인을 정의합니다.
     * </p>
     *
     * @param http {@link HttpSecurity} 객체를 통해 보안 설정을 구성합니다.
     * @return {@link SecurityFilterChain} 객체
     * @throws Exception 보안 설정 과정에서 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

//                        configuration.setAllowedOrigins(Collections.singletonList("*"));
                        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(false);

//                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "chatRoomId"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return configuration;
                    }
                }));

        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);


        http
                .oauth2Login((oauth2) -> oauth2
                        .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig
                                .accessTokenResponseClient(tokenResponseClient))
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)));


        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/users/check", "/api/tokens ", "/api/users", "/api/tokens/validate",
                                "/api/guide/stores",
                                "/api/guide/stores/nearby",
                                "/api/guide/stores/all", "/api/guide/stores?keyword=",
                                "/api/guide/stores?category=",
                                "/api/users/logout",
                                "/dosirak",
                                "/app/**",
                                "/test/**").permitAll()
                        .anyRequest().authenticated());


        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


}