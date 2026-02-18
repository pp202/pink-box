package io.home.app.corckboard.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> {
                CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
                csrfTokenRepository.setHeaderName("X-CSRF-TOKEN");
                csrf.csrfTokenRepository(csrfTokenRepository);
            })
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html", "/favicon.ico", "/assets/**",
                    "/*.js", "/*.css", "/*.ico"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/health", "/api/v1/csrf").permitAll()
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/logout").permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .formLogin(form -> form.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, ex2) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
