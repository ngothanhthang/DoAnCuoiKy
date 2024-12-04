package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.*;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.UserService;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.OrderItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
	@Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    // Phương thức tạo đơn hàng từ giỏ hàng
    public Order createOrder(User user, List<CartItem> cartItems) {
        // Tính toán tổng tiền của đơn hàng
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            totalAmount = totalAmount.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus("Chờ xử lý");

        // Lưu đơn hàng vào database
        order = orderRepository.save(order);

        // Lưu các sản phẩm trong đơn hàng (OrderItem)
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }

        return order;
    }
    
    @Override
    public List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId) {
        // Khởi tạo Logger
        Logger logger = LoggerFactory.getLogger(getClass());

        // Kiểm tra nếu userId không hợp lệ hoặc danh sách selectedItems trống
        if (selectedItems == null || selectedItems.isEmpty()) {
            logger.warn("Danh sách selectedItems rỗng hoặc null.");
            return null;
        }

        // Lấy người dùng dựa trên userId
        User user = userService.findById(userId);

        if (user == null) {
            logger.error("Không tìm thấy người dùng với userId: {}", userId);
            return null;
        }

        // Log thông tin user
        logger.info("User tìm thấy: {} (userId: {})", user.getUsername(), userId);

        // Truy vấn CartItem từ cơ sở dữ liệu dựa trên danh sách selectedItems và userId
        List<CartItem> cartItems = cartItemRepository.findByIdInAndCartUserUserId(selectedItems, userId);

        // Log kết quả tìm được
        if (cartItems.isEmpty()) {
            logger.warn("Không tìm thấy CartItems cho userId: {}", userId);
        } else {
            logger.info("Tìm thấy {} CartItems.", cartItems.size());
        }

        return cartItems;
    }
    
    @Override
    // Phương thức để lấy đơn hàng theo ID
    public Order getOrderById(Long orderId) {
        // Kiểm tra nếu đơn hàng không tồn tại
        return orderRepository.findById(orderId).orElse(null);
    }
    
    @Override
 // Tìm danh sách đơn hàng của một user với trạng thái cụ thể
    public List<Order> findOrdersByStatusAndUserId(String status, Long userId) {
        return orderRepository.findByStatusAndUserUserId(status, userId);
    }
    
    @Override
    // Tìm tất cả đơn hàng của một user
    public List<Order> findOrdersByUserId(Long userId) {
        return orderRepository.findByUserUserId(userId);
    }

}
