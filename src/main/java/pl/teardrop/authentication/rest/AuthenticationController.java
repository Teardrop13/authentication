package pl.teardrop.authentication.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.dto.LoginRequest;
import pl.teardrop.authentication.dto.LoginResponse;
import pl.teardrop.authentication.dto.RegisterRequest;
import pl.teardrop.authentication.exceptions.FailedRetrievingAuthorizationToken;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.session.AuthorizationUtil;
import pl.teardrop.authentication.session.SessionRegistry;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private final SessionRegistry sessionRegistry;
	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
		if (!userService.checkIfUserExists(request.getUsername(), request.getEmail())) {
			final User user = userService.create(request.getUsername(), request.getPassword(), request.getEmail());
			log.info("Successful user registration {}, email={} id={}", user.getUsername(), user.getEmail(), user.getId());
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with provided email or username already exists.");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
		try {
			final User user = userService.loadUserByEmail(request.getEmail());
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));
			final String sessionId = sessionRegistry.registerSession(user.getUsername());
			log.info("user {} authenticated", user.getUsername());
			return ResponseEntity.ok(new LoginResponse(sessionId));
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found for email: " + request.getEmail());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<Object> logout(@RequestHeader Map<String, String> headers) {
		final String bearer = headers.get("authorization");
		try {
			final String sessionId = AuthorizationUtil.getToken(bearer);
			sessionRegistry.removeSession(sessionId);
			return ResponseEntity.ok().build();
		} catch (FailedRetrievingAuthorizationToken e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not retrieve token from authorization header");
		}
	}

	@PostMapping("/is-authenticated")
	public ResponseEntity<Object> isAuthenticated() {
		return ResponseEntity.ok().build();
	}
}
