package vn.iotstar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "orders")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private BigDecimal totalAmount;
    
    private String status;
    
    private String paymentMethod;

    private String imageUrl;

    @CreatedDate
    private LocalDateTime createdAt;
    
    // Nhúng OrderItem trực tiếp vào Order
    private List<OrderItem> orderItems;
    
    @DBRef
    private Coupon coupon;
    
    @DBRef(lazy = true)
    private ReturnRequest returnRequest;
}
