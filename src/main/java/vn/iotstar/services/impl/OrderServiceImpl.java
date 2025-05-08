package vn.iotstar.services.impl;

import org.springframework.data.domain.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.dto.OrderDetailDTO;
import vn.iotstar.dto.OrderItemDTO;
import vn.iotstar.entity.*;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.ReturnRequestRepository;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.UserService;
import vn.iotstar.repository.CartRepository;
import vn.iotstar.repository.NotificationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired 
    private NotificationRepository notificationRepository;
    
    @Autowired
    private ReturnRequestRepository returnRequestRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
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
        
        // Tạo danh sách OrderItem được nhúng
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(UUID.randomUUID().toString());  // Tạo ID cho OrderItem
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setReviewed(false);
            
            orderItems.add(orderItem);
        }
        
        // Gán danh sách OrderItem vào Order
        order.setOrderItems(orderItems);

        // Lưu đơn hàng vào database (bao gồm cả các OrderItem đã nhúng)
        return orderRepository.save(order);
    }
    
    @Override
    public List<CartItem> getCartItemsByIds(List<String> selectedItems, String userId) {
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

        // Lấy giỏ hàng của người dùng
        Cart cart = cartRepository.findByUserId(userId);
        
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            logger.warn("Không tìm thấy giỏ hàng hoặc giỏ hàng trống cho userId: {}", userId);
            return new ArrayList<>();
        }
        
        // Lọc các CartItem theo ID
        List<CartItem> filteredCartItems = cart.getCartItems().stream()
                .filter(item -> selectedItems.contains(item.getId()))
                .collect(Collectors.toList());

        // Log kết quả tìm được
        if (filteredCartItems.isEmpty()) {
            logger.warn("Không tìm thấy CartItems cho userId: {}", userId);
        } else {
            logger.info("Tìm thấy {} CartItems.", filteredCartItems.size());
        }

        return filteredCartItems;
    }

    
    @Override
    // Phương thức để lấy đơn hàng theo ID
    public Order getOrderById(String orderId) {
        // Kiểm tra nếu đơn hàng không tồn tại
        return orderRepository.findById(orderId).orElse(null);
    }
    
    @Override
    public Page<Order> findOrdersByMultipleStatusesAndUserId(List<String> statuses, String userId, Pageable pageable) {
        Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
        return orderRepository.findByStatusInAndUserUserId(statuses, userId, sortedByDate);
    }
    
    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }
    
    public Page<Order> findOrdersByUserId(String userId, Pageable pageable) {
        Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
        return orderRepository.findByUserUserIdAndStatusNot(userId, "chờ xử lý", sortedByDate);
    }
    
    @Override
    // Tìm danh sách đơn hàng của một user với trạng thái cụ thể
    public Page<Order> findOrdersByStatusAndUserId(String status, String userId, Pageable pageable) {
        Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
        return orderRepository.findByStatusAndUserUserId(status, userId, sortedByDate);
    }
    
    @Override
    public boolean updateOrderStatus(String orderId, String status) {
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
        switch (status) {
            case "pending":
                return orderRepository.findByStatus("Chờ xác nhận");
            case "shipping":
                return orderRepository.findByStatus("Chờ duyệt đi giao");
            case "delivered":
                return orderRepository.findByStatus("Đã nhận hàng");
            case "canceled":
                return orderRepository.findByStatus("Hủy");
            case "returned":
                return orderRepository.findByStatus("Đang duyệt");
            case "confirmed":
                return orderRepository.findByStatus("Đã xác nhận");
            default:
                return orderRepository.findAll();
        }
    }

    @Override
    public Order confirmOrder(String orderId) {
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

    @Override
    public Order approveDelivery(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && "Chờ duyệt đi giao".equals(order.getStatus())) {
            order.setStatus("Đang giao"); // Hoặc trạng thái phù hợp trong hệ thống của bạn
            return orderRepository.save(order);
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByShipperAndStatus(String shipperId, String status) {
        // Tìm các notification của shipper và status đã nhận giao
        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(shipperId, "đã nhận giao");
        
        // Lấy các orderId từ notifications
        List<String> orderIds = notifications.stream()
                .map(notification -> notification.getOrder().getId())
                .collect(Collectors.toList());
        
        // Trả về các order có id nằm trong danh sách và có status được chỉ định
        return orderRepository.findByIdInAndStatus(orderIds, status);
    }

    @Override
    public Page<Order> getOrders(String search, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Nếu không có bất kỳ điều kiện lọc nào
        if (search.isEmpty() && status.isEmpty()) {
            return orderRepository.findAll(pageable);
        }
        
        // Nếu chỉ có search
        if (!search.isEmpty() && status.isEmpty()) {
            return orderRepository.findBySearchOnly(search, pageable);
        }
        
        // Nếu có cả search và status
        if (!search.isEmpty() && !status.isEmpty()) {
            return orderRepository.findBySearchCriteria(search, status, pageable);
        }
        
        // Nếu chỉ có status
        if (search.isEmpty() && !status.isEmpty()) {
            return orderRepository.findBySearchCriteria("", status, pageable);
        }
        
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Order acceptOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order != null && order.getStatus().equals("Đang duyệt")) {
            returnRequestRepository.deleteByOrderId(order.getId());
            order.setStatus("Trả hàng");  // Hoặc trạng thái xác nhận phù hợp
            return orderRepository.save(order);
        }
        return null;  // Trả về null nếu không thể xác nhận
    }

    @Override
    @Transactional
    public Order rejectOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getStatus().equals("Đang duyệt")) {
            returnRequestRepository.deleteByOrderId(order.getId());
            order.setStatus("Từ chối trả");  // Hoặc trạng thái từ chối phù hợp
            return orderRepository.save(order);
        }
        return null;  // Trả về null nếu không thể từ chối
    }

    @Override
    public Order confirmDeliveredOrder(String orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            
            if (order.getStatus().equals("Đã nhận hàng")) {
                order.setStatus("Đã giao");
                return orderRepository.save(order);
            }
        }
        return null;
    }

    @Override
    public Page<Order> getAllOrdersWithPagination(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> getOrdersByStatusWithPagination(String status, Pageable pageable) {
        switch (status) {
            case "pending":
                return orderRepository.findByStatus("Chờ xác nhận", pageable);
            case "shipping":
                return orderRepository.findByStatus("Chờ duyệt đi giao", pageable);
            case "delivered":
                return orderRepository.findByStatus("Đã nhận hàng", pageable);
            case "canceled":
                return orderRepository.findByStatus("Hủy", pageable);
            case "returned":
                return orderRepository.findByStatus("Đang duyệt", pageable);
            case "confirmed":
                return orderRepository.findByStatus("Đã xác nhận", pageable);
            default:
                return orderRepository.findAll(pageable);
        }
    }

    @Override
    public Page<Order> searchOrdersWithPagination(String keyword, Pageable pageable) {
        return orderRepository.searchOrders(keyword, keyword, pageable);
    }

    @Override
    public OrderDetailDTO getOrderDetail(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
                
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCustomerName(order.getUser().getUsername());
        
        // Chuyển đổi từ OrderItem được nhúng sang OrderItemDTO
        List<OrderItemDTO> items = order.getOrderItems().stream()
            .map(item -> new OrderItemDTO(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getProduct().getImageUrl()
            ))
            .collect(Collectors.toList());
        
        dto.setItems(items);
        return dto;
    }
    
    // Phương thức mới để tìm OrderItem bằng ID trong một Order
    public OrderItem findOrderItemById(String orderId, String orderItemId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getOrderItems() != null) {
            return order.getOrderItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElse(null);
        }
        return null;
    }
    
    // Phương thức mới để cập nhật trạng thái reviewed của OrderItem
    public void updateOrderItemReviewedStatus(String orderItemId, boolean reviewed) {
        Query query = new Query(Criteria.where("orderItems.id").is(orderItemId));
        Update update = new Update().set("orderItems.$.reviewed", reviewed);
        mongoTemplate.updateFirst(query, update, Order.class);
    }
}
