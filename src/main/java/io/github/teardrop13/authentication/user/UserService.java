package io.github.teardrop13.authentication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUsersByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to find user with username: " + username));
    }
}
