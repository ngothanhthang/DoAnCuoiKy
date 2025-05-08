package vn.iotstar.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.dto.OrderDetailDTO;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;

public interface OrderService {

    Order createOrder(User user, List<CartItem> cartItems);

    List<CartItem> getCartItemsByIds(List<String> selectedItems, String userId);

    Order getOrderById(String orderId);

    Page<Order> findOrdersByStatusAndUserId(String status, String userId, Pageable pageable);

    Page<Order> findOrdersByMultipleStatusesAndUserId(List<String> statuses, String userId, Pageable pageable);
    
    Page<Order> findOrdersByUserId(String userId, Pageable pageable);

    void save(Order order);

    boolean updateOrderStatus(String orderId, String status);
    
    List<Order> getOrdersByStatus(String status);
    
    List<Order> getAllOrders();
    
    Order confirmOrder(String orderId);
    
    Order approveDelivery(String orderId);
    
    List<Order> getOrdersByShipperAndStatus(String shipperId, String status);
    
    Page<Order> getOrders(String search, String status, int page, int size);

    Order acceptOrder(String orderId);
    
    Order rejectOrder(String orderId);
    
    Order confirmDeliveredOrder(String orderId);
    
    Page<Order> getAllOrdersWithPagination(Pageable pageable);
    
    Page<Order> getOrdersByStatusWithPagination(String status, Pageable pageable);
    
    Page<Order> searchOrdersWithPagination(String keyword, Pageable pageable);
    
    OrderDetailDTO getOrderDetail(String orderId);
}
