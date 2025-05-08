package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CartRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.CartService;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Cart addToCart(String userId, String productId, int quantity) {
        // Truy vấn người dùng từ UserRepository
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Người dùng không tồn tại.");
        }

        User user = userOpt.get();

        // Kiểm tra xem giỏ hàng của người dùng đã tồn tại chưa
        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            // Nếu giỏ hàng không tồn tại, tạo mới giỏ hàng cho người dùng
            cart = new Cart();
            cart.setUser(user);
            cart.setUserId(userId);
            cart.setCartItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        }

        // Kiểm tra xem sản phẩm có tồn tại không
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại.");
        }

        Product product = productOpt.get();

        // Tìm cartItem trong giỏ hàng
        boolean itemExists = false;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId().equals(productId)) {
                // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
                item.setQuantity(item.getQuantity() + quantity);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới CartItem
            CartItem cartItem = new CartItem();
            cartItem.setId(UUID.randomUUID().toString()); // Tạo ID mới cho CartItem
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getCartItems().add(cartItem);
        }

        // Lưu Cart vào cơ sở dữ liệu
        return cartRepository.save(cart);
    }
    
    @Override
    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }
    
    @Override
    public void changeQuantity(String cartItemId, int change) {
        // Tìm cart chứa cartItem với ID cụ thể
        Query query = new Query(Criteria.where("cartItems._id").is(cartItemId));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        
        if (cart != null) {
            for (CartItem item : cart.getCartItems()) {
                if (item.getId().equals(cartItemId)) {
                    int newQuantity = item.getQuantity() + change;
                    if (newQuantity > 0) {
                        item.setQuantity(newQuantity);
                        cartRepository.save(cart);
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public void removeItemFromCart(String cartItemId) {
        // Tìm cart chứa cartItem với ID cụ thể
        Query query = new Query(Criteria.where("cartItems._id").is(cartItemId));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        
        if (cart != null) {
            cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
            cartRepository.save(cart);
        }
    }
    
    @Override
    public CartItem getItemById(String itemId) {
        Query query = new Query(Criteria.where("cartItems._id").is(itemId));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        
        if (cart != null) {
            for (CartItem item : cart.getCartItems()) {
                if (item.getId().equals(itemId)) {
                    return item;
                }
            }
        }
        
        throw new RuntimeException("Cart item not found");
    }
    
    @Override
    public void save(Cart cart) {
        cartRepository.save(cart);
    }
    
    @Override
    public Cart findCartByUser(User user) {
        return cartRepository.findByUser(user);
    }
    
    @Override
    public int getTotalCartItemCount(Optional<User> user) {
        return user.map(this::findCartByUser)
                .map(Cart::getCartItems)
                .map(items -> items.stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum())
                .orElse(0);
    }

    
    @Override
    public int getCartItemCount(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null && cart.getCartItems() != null) {
            return cart.getCartItems().size();
        }
        return 0;
    }
}
