package vn.iotstar.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;

public interface OrderService {

	Order createOrder(User user, List<CartItem> cartItems);

	List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId);

	Order getOrderById(Long orderId);

	void save(Order order);

	boolean updateOrderStatus(Long orderId, String status);

	Page<Order> findOrdersByStatusAndUserId(String status, Long userId, Pageable pageable);

	Page<Order> findOrdersByUserId(Long userId, Pageable pageable);

	Page<Order> findOrdersByMultipleStatusesAndUserId(List<String> statuses, Long userId, Pageable pageable);
}
