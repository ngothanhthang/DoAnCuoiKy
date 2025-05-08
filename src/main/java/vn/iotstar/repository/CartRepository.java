package vn.iotstar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import vn.iotstar.entity.Cart;
import vn.iotstar.entity.User;

public interface CartRepository extends MongoRepository<Cart, String> {
    // Các phương thức tìm kiếm cơ bản
    Cart findByUserId(String userId);
    Cart findByUser(User user);
    
    // Tìm Cart có chứa CartItem với productId cụ thể
    @Query("{'cartItems.product._id': ?0}")
    Cart findByCartItemProductId(String productId);
    
    // Kiểm tra xem CartItem có tồn tại trong Cart không
    @Query(value = "{'userId': ?0, 'cartItems.product._id': ?1}", count = true)
    boolean existsByUserIdAndProductId(String userId, String productId);
    
    @Query("{ 'userId': ?0 }")
    @Update("{ '$push': { 'cartItems': { 'product': { '_id': ?1 }, 'quantity': ?2 } } }")
    void addCartItem(String userId, String productId, String quantity);

    
    @Query("{'userId': ?0, 'cartItems.product._id': ?1}")
    @Update("{'$set': {'cartItems.$.quantity': ?2}}")
    void updateCartItemQuantity(String userId, String productId, int quantity);
    
    @Query("{'cartItems._id': ?0}")
    @Update("{'$pull': {'cartItems': {'_id': ?0}}}")
    void removeCartItemById(String cartItemId);
    
    @Query("{'userId': ?0}")
    @Update("{'$pull': {'cartItems': {'product._id': ?1}}}")
    void removeCartItemByProductId(String userId, String productId);
    
    // Lấy CartItem từ Cart bằng ID của CartItem
    @Query(value = "{'cartItems._id': ?0}", fields = "{'cartItems.$': 1}")
    Cart findCartItemById(String cartItemId);
    
    @Query("{'cartItems._id': ?0}")
    @Update("{'$set': {'cartItems.$.quantity': ?1}}")
    void updateCartItemQuantityById(String cartItemId, int quantity);
}
