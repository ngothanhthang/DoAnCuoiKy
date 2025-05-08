


package vn.iotstar.services;

import java.util.Optional;

import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.User;

public interface CartService {

    // Phương thức thêm sản phẩm vào giỏ hàng
    Cart addToCart(String userId, String productId, int quantity);

	Cart getCartByUserId(String userId);

	void changeQuantity(String cartItemId, int change);

	void removeItemFromCart(String cartItemId);

	CartItem getItemById(String itemId);

	void save(Cart cart);

	Cart findCartByUser(User user);

	int getTotalCartItemCount(Optional<User> user);

	int getCartItemCount(String userId);
}