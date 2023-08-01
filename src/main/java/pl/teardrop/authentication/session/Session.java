package pl.teardrop.authentication.session;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Session {

	@NonNull
	private String username;
	private final Map<String, Object> cache = new HashMap<>();
	private LocalDateTime lastActivity = LocalDateTime.now();

	public void updateLastActivity() {
		lastActivity = LocalDateTime.now();
	}
}
