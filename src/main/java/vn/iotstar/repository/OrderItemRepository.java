package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.iotstar.entity.OrderItem;

public interface OrderItemRepository extends MongoRepository<OrderItem, String> {
    // Các truy vấn tùy chỉnh cho OrderItem (nếu cần)
}
