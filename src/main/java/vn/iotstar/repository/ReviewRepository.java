package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;

public interface ReviewRepository extends MongoRepository<Review, String> {
    
    // Tìm đánh giá theo ID sản phẩm
    List<Review> findByProductId(String productId);
    
    // Tính trung bình rating cho một đơn hàng cụ thể
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'orderItems', localField: 'product.$id', foreignField: 'product.$id', as: 'orderItems' } }",
        "{ $match: { 'orderItems.order.$id': ?0 } }",
        "{ $group: { _id: null, averageRating: { $avg: '$rating' } } }"
    })
    Double findAverageRatingForOrder(String orderId);

    // Tính tổng số review cho một đơn hàng cụ thể
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'orderItems', localField: 'product.$id', foreignField: 'product.$id', as: 'orderItems' } }",
        "{ $match: { 'orderItems.order.$id': ?0 } }",
        "{ $count: 'totalReviews' }"
    })
    Long countReviewsForOrder(String orderId);
    
    // Tìm đánh giá theo user và product
    Review findByUserAndProduct(User user, Product product);
    
    // Kiểm tra đánh giá tồn tại theo userId và productId
    @Query(value = "{ 'user.$id': ObjectId(?0), 'product.$id': ObjectId(?1) }", count = true)
    long countByUserIdAndProductId(String userId, String productId);


    
    // Tìm đánh giá theo userId và productId
    @Query("{ 'user.$id': ObjectId(?0), 'product.$id': ObjectId(?1) }")
    Review findByUserIdAndProductId(String userId, String productId);
}


/*package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;

public interface ReviewRepository extends MongoRepository<Review, String> {
	List<Review> findByProductId(Long orderId);
	
	// Tính trung bình rating cho một đơn hàng cụ thể
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id IN (SELECT oi.product.id FROM OrderItem oi WHERE oi.order.id = :orderId)")
    Double findAverageRatingForOrder(Long orderId);

    // Tính tổng số review cho một đơn hàng cụ thể
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id IN (SELECT oi.product.id FROM OrderItem oi WHERE oi.order.id = :orderId)")
    long countReviewsForOrder(Long orderId);
    
    Review findByUserAndProduct(User user, Product product);
    boolean existsByUserUserIdAndProductId(Long userId, Long productId);
    Review findByUserUserIdAndProductId(Long userId, Long productId);
}*/
