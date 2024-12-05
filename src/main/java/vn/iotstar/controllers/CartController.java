package vn.iotstar.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.iotstar.entity.CartItem;
import vn.iotstar.services.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

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

}
