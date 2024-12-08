package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	 Page<Order> findByStatusAndUserUserId(String status, Long userId, Pageable pageable);

	 Page<Order> findByStatusInAndUserUserId(List<String> statuses, Long userId, Pageable pageable);

	 Page<Order> findByUserUserId(Long userId, Pageable pageable);
}
