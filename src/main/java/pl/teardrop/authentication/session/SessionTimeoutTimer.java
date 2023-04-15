package pl.teardrop.authentication.session;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionTimeoutTimer {

    private final SessionRegistry sessionRegistry;


    @Scheduled(fixedRate = 30000)
    public void removeInactiveSessions() {
        sessionRegistry.removeExpiredSessions();
    }
}
