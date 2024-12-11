package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.iotstar.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserId(Long userId);
	
	@Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER'")
    long countUsersByRole();
	
	Page<User> findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(String email, String username, Pageable pageable);

}
