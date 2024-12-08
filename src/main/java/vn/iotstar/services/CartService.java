package vn.iotstar.services;

import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;

public interface CartService {

    // Phương thức thêm sản phẩm vào giỏ hàng
    Cart addToCart(Long userId, Long productId, int quantity);

	Cart getCartByUserId(Long userId);

	void changeQuantity(Long cartItemId, int change);

	void removeItemFromCart(Long cartItemId);

	CartItem getItemById(Long itemId);

	void save(Cart cart);
}