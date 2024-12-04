package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	// Tìm danh sách đơn hàng theo trạng thái và userId
    List<Order> findByStatusAndUserUserId(String status, Long userId);

    // Tìm tất cả đơn hàng của userId
    List<Order> findByUserUserId(Long userId);
}
