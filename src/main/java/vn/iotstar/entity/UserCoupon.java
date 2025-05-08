package vn.iotstar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Document(collection = "user_coupons")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCoupon {
    @Id
    private String id;

    @DBRef
    @Field(name = "user")
    private User user;

    @DBRef
    @Field(name = "coupon")
    private Coupon coupon;

    @Field(name = "used_at")
    private LocalDateTime usedAt;
}
