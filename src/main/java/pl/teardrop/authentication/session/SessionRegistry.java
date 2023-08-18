package pl.teardrop.authentication.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class SessionRegistry {

	private static final Duration EXPIRE_TIME = Duration.ofSeconds(600);

	private static final Map<String, Session> SESSIONS = new HashMap<>();

	public String registerSession(final String username) {
		if (username == null) {
			throw new RuntimeException("Username is null");
		}

		final String sessionId = generateSessionId();
		SESSIONS.put(sessionId, new Session(username));
		return sessionId;
	}

	public Session removeSession(final String sessionId) {
		Session session = SESSIONS.remove(sessionId);

		if (session == null) {
			throw new RuntimeException("User for sessionId " + sessionId + " not found");
		}
		log.info("session for {} ended", session.getUsername());

		return session;
	}

	public Session getSessionForSessionId(String sessionId) {
		return SESSIONS.get(sessionId);
	}

	public Optional<Session> getCurrentSession() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return SESSIONS.values().stream().filter(s -> s.getUsername().equals(username)).findFirst();
	}

	public void removeExpiredSessions() {
		LocalDateTime now = LocalDateTime.now();

		List<String> sessionIdToRemove = SESSIONS.keySet().stream()
				.filter(sessionId -> {
					LocalDateTime lastActivity = SESSIONS.get(sessionId).getLastActivity();
					Duration duration = Duration.between(lastActivity, now);
					return !duration.minus(EXPIRE_TIME).isNegative();
				}).toList();

		sessionIdToRemove.forEach(sessionId -> removeSession(sessionId));
	}

	private String generateSessionId() {
		return new String(
				Base64.getEncoder().encode(
						UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)
				)
		);
	}
}
