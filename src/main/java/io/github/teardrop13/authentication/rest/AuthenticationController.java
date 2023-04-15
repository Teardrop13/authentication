package io.github.teardrop13.authentication.rest;

import io.github.teardrop13.authentication.dto.LoginRequest;
import io.github.teardrop13.authentication.dto.RegisterRequest;
import io.github.teardrop13.authentication.dto.RegisterResponse;
import io.github.teardrop13.authentication.dto.LoginResponse;
import io.github.teardrop13.authentication.session.SessionRegistry;
import io.github.teardrop13.authentication.user.User;
import io.github.teardrop13.authentication.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private SessionRegistry sessionRegistry;
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        if (!Objects.equals(request.getPassword(), request.getPassword2())) {
            return ResponseEntity.ok(RegisterResponse.fail("Confirmation password did not match."));
        }

        if (!userService.checkIfUserExists(request.getUsername(), request.getEmail())) {
            User user = userService.create(request.getUsername(), request.getPassword(), request.getEmail());
            log.info("Created user {}, email={} id={}", user.getUsername(), user.getEmail(), user.getId());
            return ResponseEntity.ok(RegisterResponse.success());
        } else {
            return ResponseEntity.ok(RegisterResponse.fail("User with provided email or username already exists."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.loadUserByEmail(request.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));
            String sessionId = sessionRegistry.registerSession(user.getUsername());
            LoginResponse response = LoginResponse.success(sessionId);
            log.info("user {} authenticated", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.ok(LoginResponse.fail("User not found for email: " + request.getEmail()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader Map<String, String> headers) {
        final String sessionId = headers.get("authorization");
        if (sessionRegistry.removeSession(sessionId) != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/is-authenticated")
    public ResponseEntity<?> isAuthenticated(@RequestHeader Map<String, String> headers) {
        log.info("is-authenticated request");
        return ResponseEntity.ok().build();
    }
}
