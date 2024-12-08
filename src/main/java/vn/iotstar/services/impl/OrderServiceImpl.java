package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

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
    public Page<Order> findOrdersByStatusAndUserId(String status, Long userId, Pageable pageable) {
        return orderRepository.findByStatusAndUserUserId(status, userId, pageable);
    }
    
    @Override
    // Tìm tất cả đơn hàng của một user

	public Page<Order> findOrdersByUserId(Long userId, Pageable pageable) {
	    return orderRepository.findByUserUserId(userId, pageable);
	}
    
    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }
    
    @Override
    public Page<Order> findOrdersByMultipleStatusesAndUserId(List<String> statuses, Long userId, Pageable pageable) {
        return orderRepository.findByStatusInAndUserUserId(statuses, userId, pageable);
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            orderRepository.save(order); // Lưu lại thay đổi
            return true;
        }
        return false;
    }
	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public List<Order> getOrdersByStatus(String status) {
		 switch (status) 
		 {
	         case "pending":
	             return orderRepository.findByStatus("Chờ xác nhận");
	         case "shipping":
	             return orderRepository.findByStatus("Chờ duyệt đi giao");
	         case "delivered":
	             return orderRepository.findByStatus("Chờ duyệt đi giao");
	         case "canceled":
	             return orderRepository.findByStatus("Hủy");
	         case "returned":
	             return orderRepository.findByStatus("Trả hàng");
	         case "confirmed":
	        	 return orderRepository.findByStatus("Đã xác nhận");
	         default:
	             return orderRepository.findAll();
		 }
	}

	@Override
	public Order confirmOrder(Long orderId) {
		// Tìm đơn hàng theo orderId
        Order order = orderRepository.findById(orderId).orElse(null);
        
        // Kiểm tra xem đơn hàng có tồn tại và đang ở trạng thái "Chờ xác nhận"
        if (order != null && "Chờ xác nhận".equals(order.getStatus())) {
            // Cập nhật trạng thái của đơn hàng thành "Đã xác nhận"
            order.setStatus("Đã xác nhận");
            
            // Lưu lại đơn hàng đã cập nhật vào cơ sở dữ liệu
            orderRepository.save(order);
            
            // Trả về đơn hàng đã được xác nhận
            return order;
        }
        
        // Nếu đơn hàng không tồn tại hoặc không ở trạng thái "Chờ xác nhận"
        return null;
	}
}