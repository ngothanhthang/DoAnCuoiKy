package vn.iotstar.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.CartService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userService;
 // Tạo logger
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    // Thêm sản phẩm vào giỏ hàng và trả về thông báo
    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestParam Long userId, 
                                            @RequestParam Long productId, 
                                            @RequestParam int quantity) {
        // Xử lý thêm sản phẩm vào giỏ hàng
        cartService.addToCart(userId, productId, quantity);

        // Trả về thông báo
        return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng!");
    }
    
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateCart(@RequestParam List<Long> selectedItems) {
        BigDecimal totalPrice = BigDecimal.ZERO;  // Khởi tạo tổng tiền là 0

        if (selectedItems != null && !selectedItems.isEmpty()) {
            // Lấy các sản phẩm được chọn từ cơ sở dữ liệu hoặc session
            for (Long itemId : selectedItems) {
                CartItem item = cartService.getItemById(itemId);  // Giả sử bạn có phương thức lấy sản phẩm
                BigDecimal price = item.getProduct().getPrice();  // Giá sản phẩm là BigDecimal
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());  // Chuyển số lượng int sang BigDecimal

                // Nhân giá và số lượng, và cộng vào tổng tiền
                totalPrice = totalPrice.add(price.multiply(quantity));  
            }
        }

        // Trả về tổng tiền đã tính toán
        Map<String, Object> response = new HashMap<>();
        response.put("totalPrice", totalPrice);  // Nếu không có sản phẩm nào, tổng tiền là 0
        return response;
    }
    
    @GetMapping("/user-cart")
    public ResponseEntity<?> getCartForUser(HttpSession session) {
        // Lấy user từ session
    	 User user = (User) session.getAttribute("user");

         // Nếu không có user trong session, sử dụng user mặc định (userId = 1)
         if (user == null) {
             user = new User();
             user.setUserId(1L);  // Cài đặt id mặc định là 1, hoặc sử dụng giá trị khác nếu cần
             // Bạn có thể cài đặt thêm các thuộc tính mặc định khác cho User nếu cần
         }

        // Lấy tổng số lượng sản phẩm trong giỏ hàng của người dùng
        int totalItems = cartService.getTotalCartItemCount(user);
        logger.info("Tổng số lượng sản phẩm trong giỏ hàng của user (userId = {}): {}", user.getUserId(), totalItems);

        // Trả về số lượng sản phẩm trong giỏ hàng
        return ResponseEntity.ok(totalItems);
    }
}