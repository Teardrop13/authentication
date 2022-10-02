package io.github.teardrop13.authentication.rest;


import io.github.teardrop13.authentication.dto.ResponseDTO;
import io.github.teardrop13.authentication.dto.UserDTO;
import io.github.teardrop13.authentication.session.SessionRegistry;
import io.github.teardrop13.authentication.user.User;
import io.github.teardrop13.authentication.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("http://localhost:4200")
@AllArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private SessionRegistry sessionRegistry;

    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        User user = userService.create(userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail());
        log.info("create user {}, id={}", user.getUsername(), user.getId());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody UserDTO user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        String sessionId = sessionRegistry.registerSession(user.getUsername());
        ResponseDTO response = new ResponseDTO(sessionId);
        log.info("user {} authenticated", user.getUsername());
        return ResponseEntity.ok(response);
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
