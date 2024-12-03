package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
}
