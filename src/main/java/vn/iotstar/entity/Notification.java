package vn.iotstar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    // Liên kết với shipper (User)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @DBRef
    @Field(name = "user")
    private User user;
    
    // Liên kết với đơn hàng (Order)
    @DBRef
    @Field(name = "order")
    private Order order;
    
    // Thời gian tạo thông báo
    @Field(name = "timestamp")
    private Date timestamp = new Date();
    
    // Nội dung thông báo
    @Field(name = "message")
    private String message;
    
    // Trạng thái đã đọc
    @Field(name = "is_read")
    private boolean isRead = false;
    
    @Field(name = "status")
    private String status;
}
