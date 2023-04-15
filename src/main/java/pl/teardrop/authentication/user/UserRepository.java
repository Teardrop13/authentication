package pl.teardrop.authentication.user;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserRepository extends Repository<User, Long> {

	Optional<User> findUsersByUsername(String username);

	Optional<User> findUsersByEmail(String email);

	Optional<User> findById(Long id);

	List<User> findAll();

	User save(User user);

	void deleteById(Long id);

}
