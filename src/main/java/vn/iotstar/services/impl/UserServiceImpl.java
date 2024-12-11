package vn.iotstar.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
    private UserRepository userRepository;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);  // Trả về null nếu không tìm thấy User
    }

	@Override
	public Page<User> getUsers(int pageNum) {
		Pageable pageable = PageRequest.of(pageNum, 5);  // 5 sản phẩm mỗi trang
        return userRepository.findAll(pageable);
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
		
	}

	@Override
	public Optional<User> getUserById(Long id) {
		 return userRepository.findById(id);
	}

	@Override
	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
		
	}

}
