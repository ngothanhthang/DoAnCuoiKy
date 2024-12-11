package vn.iotstar.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import vn.iotstar.entity.User;

public interface UserService {

	User findById(Long userId);

	Page<User> getUsers(int pageNum);

	User saveUser(User user);

	Optional<User> getUserById(Long id);

	void deleteUserById(Long id);

}
