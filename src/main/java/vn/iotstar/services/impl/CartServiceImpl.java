package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CartRepository;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.CartService;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository; // Thêm UserRepository để lấy thông tin người dùng

    // Triển khai phương thức addToCart
    @Override
    public Cart addToCart(Long userId, Long productId, int quantity) {
        // Truy vấn người dùng từ UserRepository
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Người dùng không tồn tại.");
        }

        User user = userOpt.get();

        // Kiểm tra xem giỏ hàng của người dùng đã tồn tại chưa
        Cart cart = cartRepository.findByUserUserId(userId);

        if (cart == null) {
            // Nếu giỏ hàng không tồn tại, tạo mới giỏ hàng cho người dùng
            cart = new Cart();
            cart.setUser(user);  // Gán người dùng vào giỏ hàng
            cart = cartRepository.save(cart);
        }

        // Kiểm tra xem sản phẩm có tồn tại không
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại.");
        }

        Product product = productOpt.get();

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (cartItem != null) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        // Lưu CartItem vào cơ sở dữ liệu
        cartItemRepository.save(cartItem);

        return cart;
    }
}
