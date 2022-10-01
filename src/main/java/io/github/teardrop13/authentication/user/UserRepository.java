package io.github.teardrop13.authentication.user;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserRepository {

    private static final Map<String, User> REGISTERED_USERS = new HashMap<>(); // username na User

    @PostConstruct
    private void setupUsers() {
        REGISTERED_USERS.put("user1", new User(0, "user1", "$2a$10$Xus67f/BaschOyQ1Zr/CGOOYdXzGmajchvduDHbSmxzABwzOtcH4u"));
        REGISTERED_USERS.put("user2", new User(1, "user2", "$2a$10$Xus67f/BaschOyQ1Zr/CGOOYdXzGmajchvduDHbSmxzABwzOtcH4u"));
    }

    public Optional<User> findUsersByUsername(final String username) {
        return Optional.ofNullable(REGISTERED_USERS.get(username));
    }


}
