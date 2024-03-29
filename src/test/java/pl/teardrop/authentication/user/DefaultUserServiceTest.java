package pl.teardrop.authentication.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.teardrop.authentication.exceptions.UserNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;
	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new DefaultUserService(userRepository, passwordEncoder);
	}

	@Test
	void checkIfUserExists_returnTrueIfExist() {
		String username = "user";
		String email = "user@gmail.com";

		UserService userServiceMock = spy(userService);

		doReturn(new User()).when(userServiceMock).loadUserByUsername(username);

		boolean exists = userServiceMock.checkIfUserExists(username, email);

		assertTrue(exists);
	}

	@Test
	void checkIfUserExists_returnFalseIfUserDoesNotExist() {
		String username = "user";
		String email = "user@gmail.com";

		UserService userServiceMock = spy(userService);

		doThrow(UsernameNotFoundException.class).when(userServiceMock).loadUserByUsername(username);

		boolean exists = userServiceMock.checkIfUserExists(username, email);

		assertFalse(exists);
	}

	@Test
	void loadUserByUsername_whenUserExists() {
		String username = "user";
		User user = User.builder().username(username).build();

		when(userRepository.findUsersByUsername(username)).thenReturn(Optional.of(user));

		User returnedUser = userService.loadUserByUsername(username);
		assertEquals(returnedUser, user);
	}

	@Test
	void loadUserByUsername_whenUserExistsDoesNotExist() {
		String username = "user";

		when(userRepository.findUsersByUsername(username)).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
	}

	@Test
	void loadUserByEmail_whenUserExists() {
		String email = "user@gmail.com";
		User user = User.builder().email(email).build();

		when(userRepository.findUsersByEmail(email)).thenReturn(Optional.of(user));

		User returnedUser = userService.loadUserByEmail(email);
		assertEquals(returnedUser, user);
	}

	@Test
	void loadUserByEmail_whenUserExistsDoesNotExist() {
		String email = "user@gmail.com";

		when(userRepository.findUsersByEmail(email)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.loadUserByEmail(email));
	}

}