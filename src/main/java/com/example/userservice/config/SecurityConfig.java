package com.example.userservice.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.example.userservice.User.Permission.*;
import static com.example.userservice.User.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**")
                    .permitAll()
                .requestMatchers("/api/v1/delivery/**").hasAnyRole(ADMIN.name(), DELIVERY_PERSON.name())

                .requestMatchers(GET,"/api/v1/delivery/**").hasAnyAuthority(ADMIN_READ.name(), DELIVER_PERSON_READ.name())
                .requestMatchers(POST,"/api/v1/delivery/**").hasAnyAuthority(ADMIN_CREATE.name(), DELIVER_PERSON_CREATE.name())
                .requestMatchers(PUT,"/api/v1/delivery/**").hasAnyAuthority(ADMIN_UPDATE.name(), DELIVER_PERSON_UPDATE.name())
                .requestMatchers(DELETE,"/api/v1/delivery/**").hasAnyAuthority(ADMIN_DELETE.name(), DELIVER_PERSON_UPDATE.name())

                .requestMatchers("/api/v1/admin/**").hasRole(ADMIN.name())

                .requestMatchers(GET,"/api/v1/admin/**").hasAuthority(ADMIN_READ.name())
                .requestMatchers(POST,"/api/v1/admin/**").hasAuthority(ADMIN_CREATE.name())
                .requestMatchers(PUT,"/api/v1/admin/**").hasAuthority(ADMIN_UPDATE.name())
                .requestMatchers(DELETE,"/api/v1/admin/**").hasAuthority(ADMIN_DELETE.name())

                .requestMatchers("/api/v1/customer/**").hasAnyRole(ADMIN.name(), CUSTOMER.name())

                .requestMatchers(GET,"/api/v1/customer/**").hasAnyAuthority(ADMIN_READ.name(), CUSTOMER_READ.name())
                .requestMatchers(POST,"/api/v1/customer/**").hasAnyAuthority(ADMIN_CREATE.name(), CUSTOMER_CREATE.name())
                .requestMatchers(PUT,"/api/v1/customer/**").hasAnyAuthority(ADMIN_UPDATE.name(), CUSTOMER_UPDATE.name())
                .requestMatchers(DELETE,"/api/v1/customer/**").hasAnyAuthority(ADMIN_DELETE.name(), CUSTOMER_UPDATE.name())
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) ->
                        SecurityContextHolder.clearContext()
                );



        return http.build();
    }
}
