package vn.iotstar.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Liên kết với shipper (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Liên kết với đơn hàng (Order)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    // Thời gian tạo thông báo
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    // Nội dung thông báo (Kiểu TEXT)
    @Lob
    @Column(nullable = false)
    private String message;
    
    // Trạng thái đã đọc
    @Column(nullable = false)
    private boolean isRead = false;
    
    @Column(nullable = false,name = "status", columnDefinition ="nvarchar(255)")
    private String status;
}
