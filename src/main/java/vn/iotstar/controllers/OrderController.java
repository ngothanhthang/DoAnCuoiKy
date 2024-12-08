package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.UserService;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    // Tạo đơn hàng từ giỏ hàng
    @PostMapping("/create")
    public ResponseEntity<Long> createOrder(@RequestParam("selectedItems") List<Long> selectedItems, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L;
        }

        User user = userService.findById(userId);
        List<CartItem> cartItems = getCartItemsByIds(selectedItems, userId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Tạo đơn hàng
        Order order = orderService.createOrder(user, cartItems);

        // Trả về ID của đơn hàng vừa tạo
        return ResponseEntity.ok(order.getId());
    }


    // Lấy các CartItem từ giỏ hàng dựa trên ids đã chọn
    private List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId) {
        // Cần viết logic để lấy các CartItem theo ids và userId từ cơ sở dữ liệu
        return orderService.getCartItemsByIds(selectedItems, userId);
    }
    
    @PostMapping("/update-status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, Object> request) {
    	final Logger logger = LoggerFactory.getLogger(OrderController.class);
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String status = request.get("status").toString();
        logger.info("Updating orderId: {} with status: {}", orderId, status);
        // Cập nhật trạng thái đơn hàng trong cơ sở dữ liệu
        boolean success = orderService.updateOrderStatus(orderId, status);
        
        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
        }
    }
    // Từ đoạn này là thêm các phương thức cho order:
   
    
}
