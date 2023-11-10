package com.example.userservice.auth;

import com.example.userservice.User.Role;
import com.example.userservice.User.User;
import com.example.userservice.config.JwtService;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.EmailService;
import com.example.userservice.token.Token;
import com.example.userservice.token.TokenRepository;
import com.example.userservice.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;


    public AuthenticationResponse register(RegisterRequest request) {


        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Check if the registered user is a DELIVERY_PERSON or CUSTOMER
        if (user.getRole() == Role.DELIVERY_PERSON ) {
            notifyAdminAboutNewRegistration(user);

            // Generate an activation token.
            String activationToken = generateActivationToken();

            // Send the activation token to the delivery person's email.
            emailService.sendActivationTokentoDeliveryPerson(user.getEmail(), activationToken);
        }


        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void revokeAllUserToken(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevokeed(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revokeed(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserToken(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserToken(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public String generateActivationToken() {
        // Generate a random activation token (you can customize the token generation logic).
        return UUID.randomUUID().toString();
    }

    private void notifyAdminAboutNewRegistration(User user) {
        // Define the ADMIN's email address (replace with the actual email address).
        String adminEmail = "umairwaseef812@gmail.com"; // Replace with the actual admin's email.

        // Create an email message to notify the ADMIN.
        String subject = "New Deliver Person Registration Registration";
        String message = String.format(
                "A new user has registered with the role: %s.%n"
                        + "User Details:%n"
                        + "Name: %s %s%n"
                        + "Email: %s%n"
                        + "Registration Status: PENDING%n"
                        + "To accept or reject the registration, click here: http://api/v1/admin/accept-?userId=%s",
                user.getRole(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getId()
        );

        // Send the email notification.
        emailService.sendNotificationToAdmin(adminEmail, subject, message);

    }

}
