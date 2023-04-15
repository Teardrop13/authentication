package pl.teardrop.authentication.session;

import com.google.common.base.Strings;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    private final SessionRegistry sessionRegistry;

    private final UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String sessionId = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Strings.isNullOrEmpty(sessionId)) {
            SecurityContextHolder.clearContext();
            HttpSession httpSession = request.getSession(false);
            if (httpSession != null) {
                httpSession.invalidate();
            }
            filterChain.doFilter(request, response);
            return;
        }
        final Session session = sessionRegistry.getSessionForSessionId(sessionId);

        if (session == null) {
            SecurityContextHolder.clearContext();
            HttpSession httpSession = request.getSession(false);
            if (httpSession != null) {
                httpSession.invalidate();
            }
            filterChain.doFilter(request, response);
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
        filterChain.doFilter(request, response);
    }
}