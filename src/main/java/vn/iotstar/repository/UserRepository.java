package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import vn.iotstar.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findById(String id);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    User findByResetPasswordToken(String token);
    User findByEmail(String email);
    
    // Sử dụng @Query của MongoDB để tìm kiếm theo regex
    @Query("{'$or':[ {'email': {$regex: ?0, $options: 'i'}}, {'username': {$regex: ?1, $options: 'i'}} ]}")
    Page<User> findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(String email, String username, Pageable pageable);
}
