package pl.teardrop.authentication.user;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class DefaultUserService implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Lazy
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public boolean checkIfUserExists(@NonNull String username, @NonNull String email) {
		try {
			loadUserByUsername(username);
			return true;
		} catch (UsernameNotFoundException e) {
			log.info("User not found for username: " + username);
		}

		try {
			loadUserByEmail(email);
			return true;
		} catch (UsernameNotFoundException e) {
			log.info("User not found for email: " + email);
		}

		return false;
	}

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findUsersByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Failed to find user with username: " + username));
	}

	@Override
	public User loadUserByEmail(String email) throws UsernameNotFoundException {
		return userRepository.findUsersByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Failed to find user with email: " + email));
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
