package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndProductId(Long cartId, Long productId);
    // Tạo truy vấn để tìm các CartItem của người dùng dựa trên danh sách sản phẩm được chọn
    List<CartItem> findByIdInAndCartUserUserId(List<Long> productIds, Long userId);  
    void deleteById(Long id);
}
