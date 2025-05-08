package vn.iotstar.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "coupons")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    private String id;

    @Indexed(unique = true)
    @Field(name = "code")
    private String code;

    @Field(name = "discount_amount")
    private BigDecimal discountAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @Field(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Field(name = "quantity")
    private Integer quantity;

    // Sử dụng DBRef để tham chiếu đến các đơn hàng
    // Hoặc bạn có thể sử dụng cách lưu ID thay vì DBRef tùy theo thiết kế
    @DBRef(lazy = true)
    private List<Order> orders;
}
