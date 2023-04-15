package pl.teardrop.authentication.user;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

	boolean checkIfUserExists(@NonNull String username, @NonNull String email);

	@Override
	User loadUserByUsername(String username) throws UsernameNotFoundException;

	User loadUserByEmail(String email) throws UsernameNotFoundException;

	User create(String username, String password, String email);

	User save(User user);
}
