package com.example.userservice.config;

import com.example.userservice.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")){

            return;
        }

        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevokeed(true);
            tokenRepository.save(storedToken);
        }
        // Set a message in the response to be displayed in Postman
        response.setStatus(HttpServletResponse.SC_OK); // Set the response status to 200 (OK)
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write("User is logging out"); // Write your message to the response
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
