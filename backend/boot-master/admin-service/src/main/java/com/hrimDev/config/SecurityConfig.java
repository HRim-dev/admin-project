package com.hrimDev.config;

import com.hrimDev.jwt.filter.JWTAuthenticationFilter;
import com.hrimDev.jwt.service.JwtService;
import com.hrimDev.jwt.util.CookieUtil;
import com.hrimDev.login.filter.LoginRateLimitFilter;
import com.hrimDev.login.service.LoginService;
import com.hrimDev.login.serviceImpl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginRateLimitFilter loginRateLimitFilter(LoginService loginService) {
        return new LoginRateLimitFilter(loginService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LoginRateLimitFilter loginRateLimitFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .defaultsDisabled()
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_PATHS).permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(new JWTAuthenticationFilter(jwtService, cookieUtil, PERMIT_ALL_PATHS), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(loginRateLimitFilter, JWTAuthenticationFilter.class);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private static final String[] PERMIT_ALL_PATHS = {
            "/signup", "/signup/**", "/login", "/logout",
            "/login/find/id", "/login/find/password",
            "/logout-success", "/h2-console/**"
    };
}
