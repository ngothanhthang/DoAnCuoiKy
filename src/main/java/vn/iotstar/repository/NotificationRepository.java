package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> 
{
    // Đếm số thông báo chưa đọc với các trạng thái trong danh sách
    Long countByIsReadFalseAndStatusIn(List<String> statuses);
    
    // Tìm thông báo theo danh sách trạng thái
    List<Notification> findByStatusIn(List<String> statuses);
    
    // Tìm thông báo theo userId và trạng thái
    // Lưu ý: Trong MongoDB, cần sử dụng id của user thay vì userId
    @Query("{'user.$id': ?0, 'status': ?1}")
    List<Notification> findByUserIdAndStatus(String userId, String status);
    
    // Tìm các thông báo phân biệt theo userId và trạng thái đơn hàng
    @Query("{'user.$id': ?0, 'order.status': ?1}")
    List<Notification> findDistinctOrdersByUserIdAndOrderStatus(String userId, String orderStatus);
}
