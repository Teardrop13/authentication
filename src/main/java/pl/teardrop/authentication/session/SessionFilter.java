package pl.teardrop.authentication.session;

import com.google.common.base.Strings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionFilter extends OncePerRequestFilter {

	private final SessionRegistry sessionRegistry;
	@Lazy
	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		final String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
		log.debug("Token: {}", bearer);

		if (Strings.isNullOrEmpty(bearer) || bearer.length() <= 7) {
			log.debug("Invalid token received: {}", bearer);
			SecurityContextHolder.clearContext();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			filterChain.doFilter(request, response);
			log.debug("Session invalidated");
			return;
		}

		String sessionId = bearer.substring(7);

		final Session session = sessionRegistry.getSessionForSessionId(sessionId);

		if (session == null) {
			log.debug("Session not found for sessionId={}", sessionId);
			SecurityContextHolder.clearContext();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			filterChain.doFilter(request, response);
			log.debug("Session invalidated");
			return;
		}

		session.updateLastActivity();

		User user = userService.loadUserByUsername(session.getUsername());

		final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				user,
				null,
				user.getAuthorities()
		);

		auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(auth);
		log.debug("User username={}, id={} authenticated", user.getUsername(), user.getId());
		filterChain.doFilter(request, response);
	}
}
