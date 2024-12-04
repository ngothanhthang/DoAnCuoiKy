package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUserUserId(Long userId);
}
