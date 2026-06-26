package br.com.ravikyu.barbercontrol.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/usuarios/cadastrar", "/usuarios/login").permitAll()
                        .requestMatchers("/h2-console/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/cadastrar-funcionario").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/clientes").hasAnyRole("ADMIN", "SECRETARIA")
                        .requestMatchers(HttpMethod.PUT, "/clientes/**").hasAnyRole("ADMIN", "SECRETARIA")
                        .requestMatchers(HttpMethod.DELETE, "/clientes/**").hasAnyRole("ADMIN", "SECRETARIA")
                        .requestMatchers(HttpMethod.POST, "/servicos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/servicos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/servicos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/agendamentos").hasAnyRole("ADMIN", "SECRETARIA")
                        .requestMatchers(HttpMethod.PUT, "/agendamentos/**").hasAnyRole("ADMIN", "SECRETARIA")
                        .requestMatchers(HttpMethod.PATCH, "/agendamentos/*/cancelar").hasAnyRole("ADMIN", "SECRETARIA")
                        .requestMatchers(HttpMethod.DELETE, "/agendamentos/**").hasAnyRole("ADMIN", "SECRETARIA")
                        .requestMatchers(HttpMethod.POST, "/barbeiros").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/barbeiros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/barbeiros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/barbeiros/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
