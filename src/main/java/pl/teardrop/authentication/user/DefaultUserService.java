package pl.teardrop.authentication.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.teardrop.authentication.exceptions.UserNotFoundException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;

	@Lazy
	private final PasswordEncoder passwordEncoder;

	@Override
	public Optional<User> getById(UserId userId) {
		return userRepository.findById(userId.getId());
	}

	@Override
	public boolean checkIfUserExists(@NonNull String username, @NonNull String email) {
		try {
			loadUserByUsername(username);
			return true;
		} catch (UsernameNotFoundException e) {
			log.info("User not found for username: {}", username);
		}

		try {
			loadUserByEmail(email);
			return true;
		} catch (UserNotFoundException e) {
			log.info("User not found for email: {}", email);
		}

		return false;
	}

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findUsersByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Failed to find user with username: " + username));
	}

	@Override
	public User loadUserByEmail(String email) throws UserNotFoundException {
		return userRepository.findUsersByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("Failed to find user with email: " + email));
	}

	@Override
	public User create(String username, String password, String email) {
		String encodedPassword = passwordEncoder.encode(password);

		User user = new User();
		user.setUsername(username);
		user.setPassword(encodedPassword);
		user.setEmail(email);

		User userAdded = save(user);
		log.info("Created user {}, email={} id={}", userAdded.getUsername(), userAdded.getEmail(), userAdded.getId());

		return userAdded;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}
}
