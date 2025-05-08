package vn.iotstar.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Order;

public interface OrderRepository extends MongoRepository<Order, String>
{
    @Query("{'status': ?0, 'user.$id': ?1}")
    Page<Order> findByStatusAndUserUserId(String status, String userId, Pageable pageable);

    @Query("{'status': {$in: ?0}, 'user.$id': ?1}")
    Page<Order> findByStatusInAndUserUserId(List<String> statuses, String userId, Pageable pageable);

    @Query("{'user.$id': ?0}")
    Page<Order> findByUserUserId(String userId, Pageable pageable);
    
    @Query("{'user.$id': ?0, 'status': {$ne: ?1}}")
    Page<Order> findByUserUserIdAndStatusNot(String userId, String status, Pageable pageable);
    
    List<Order> findByStatus(String status);
    
    Optional<Order> findById(String id);
    
    @Query("{'_id': {$in: ?0}, 'status': ?1}")
    List<Order> findByIdInAndStatus(List<String> orderIds, String status);
    
    // Lấy tổng số đơn hàng
    long countByStatus(String status);
    
    // Lấy tổng doanh thu - Thay đổi kiểu trả về từ BigDecimal sang Double
    @Aggregation(pipeline = {
        "{ $match: { status: 'Đã giao' } }",
        "{ $group: { _id: null, total: { $sum: '$totalAmount' } } }"
    })
    Double sumTotalRevenue();
    
    @Query("{ $and: [ " +
           "{ $or: [ " +
           "   { 'user.username': { $regex: ?0, $options: 'i' } }, " +
           "   { '_id': { $regex: ?0, $options: 'i' } } " +
           "] }, " +
           "{ 'status': ?1 } " +
           "] }")
    Page<Order> findBySearchCriteria(String search, String status, Pageable pageable);

    // Thêm phương thức đếm số đơn hàng theo trạng thái
    @Query(value = "{ 'status': ?0 }", count = true)
    long countByStatusAndHavePage(String status);

    // Thêm phương thức lấy tổng doanh thu theo trạng thái - Thay đổi kiểu trả về từ BigDecimal sang Double
    @Aggregation(pipeline = {
        "{ $match: { status: ?0 } }",
        "{ $group: { _id: null, total: { $sum: '$totalAmount' } } }"
    })
    Double sumTotalAmountByStatus(String status);

    // Sửa phương thức lấy đơn hàng mới nhất
    @Query(value = "{}", sort = "{ 'createdAt': -1 }")
    List<Order> findLatestOrders(Pageable pageable);
    
    // Hoặc thay thế bằng phương thức có tên tuân theo quy tắc đặt tên
    // List<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("{ $or: [ " +
           "{ 'user.username': { $regex: ?0, $options: 'i' } }, " +
           "{ '_id': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Order> findBySearchOnly(String search, Pageable pageable);
    
    Page<Order> findByStatus(String status, Pageable pageable);
    
    @Query("{ $or: [ " +
           "{ '_id': ?0 }, " +
           "{ 'user.username': { $regex: ?1, $options: 'i' } } " +
           "] }")
    Page<Order> searchOrders(String id, String username, Pageable pageable);
    
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 }, 'status': ?2 }")
    List<Order> findByCreatedAtBetweenAndStatus(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        String status
    );
}
