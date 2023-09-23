package pl.teardrop.authentication.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findUsersByUsername(String username);

	Optional<User> findUsersByEmail(String email);

}
