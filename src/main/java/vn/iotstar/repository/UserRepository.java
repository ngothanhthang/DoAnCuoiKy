package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserId(Long userId);
	boolean existsByUsername(String username);
	Optional<User> findByUsername(String username);
	User findByResetPasswordToken(String token);
	User findByEmail(String email);
}
