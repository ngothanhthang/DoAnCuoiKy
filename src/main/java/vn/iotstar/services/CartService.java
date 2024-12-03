package vn.iotstar.services;

import vn.iotstar.entity.Cart;

public interface CartService {

    // Phương thức thêm sản phẩm vào giỏ hàng
    Cart addToCart(Long userId, Long productId, int quantity);
}
