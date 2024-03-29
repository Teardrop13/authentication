package pl.teardrop.authentication.rest;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import pl.teardrop.authentication.dto.RegisterResponse;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
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
	public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
		if (!userService.checkIfUserExists(request.getUsername(), request.getEmail())) {
			User user = userService.create(request.getUsername(), request.getPassword(), request.getEmail());
			log.info("Successful user registration {}, email={} id={}", user.getUsername(), user.getEmail(), user.getId());
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
		} catch (UserNotFoundException e) {
			return ResponseEntity.ok(LoginResponse.fail("User not found for email: " + request.getEmail()));
		}
	}

	@PostMapping("/logout")
	public void logout(@RequestHeader Map<String, String> headers) {
		final String bearer = headers.get("authorization");
		String sessionId = null;
		if (!Strings.isNullOrEmpty(bearer) && bearer.length() >= 7) {
			sessionId = bearer.substring(7);
		}

		if (sessionId != null && sessionRegistry.removeSession(sessionId) == null) {
			throw new UserNotFoundException("Session not found for id: " + sessionId);
		}
	}

	@PostMapping("/is-authenticated")
	public void isAuthenticated() {
		log.info("is-authenticated request");
	}
}
