package vn.iotstar.services;

import java.util.List;

import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;

public interface OrderService {

	Order createOrder(User user, List<CartItem> cartItems);

	List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId);

	Order getOrderById(Long orderId);

	List<Order> findOrdersByStatusAndUserId(String status, Long userId);

	List<Order> findOrdersByUserId(Long userId);

	void save(Order order);

	List<Order> findOrdersByMultipleStatusesAndUserId(List<String> statuses, Long userId);

	boolean updateOrderStatus(Long orderId, String status);
}
