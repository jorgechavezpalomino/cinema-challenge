package com.challenge.gateway.api;

import com.challenge.gateway.api.dto.AuthResponse;
import com.challenge.gateway.api.dto.GoogleTokenRequest;
import com.challenge.gateway.api.dto.UserSummary;
import com.challenge.gateway.security.JwtService;
import com.challenge.gateway.service.GoogleIdentityService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final JwtService jwtService;
    private final GoogleIdentityService googleIdentityService;

    public AuthController(JwtService jwtService, GoogleIdentityService googleIdentityService) {
        this.jwtService = jwtService;
        this.googleIdentityService = googleIdentityService;
    }

    @PostMapping("/google")
    public AuthResponse google(@RequestBody GoogleTokenRequest request) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = googleIdentityService.verify(request.credential());
        String email = payload.getEmail();
        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new GeneralSecurityException("Google email is not verified");
        }
        String name = payload.containsKey("name") ? String.valueOf(payload.get("name")) : email;
        log.info("Google login validated for {}", email);
        String token = jwtService.generateToken(email, name, "CUSTOMER");
        return new AuthResponse(token, new UserSummary(email, name, false));
    }

    @PostMapping("/guest")
    public AuthResponse guest() {
        log.info("Guest checkout requested");
        String token = jwtService.generateToken("guest@cinema.local", "Invitado", "GUEST");
        return new AuthResponse(token, new UserSummary("guest@cinema.local", "Invitado", true));
    }

    @ExceptionHandler({GeneralSecurityException.class, IOException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleGoogleError(Exception exception) {
        return exception.getMessage();
    }
}
