package vn.iotstar.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "addresses")  // Thay @Entity và @Table bằng @Document
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id  // Sử dụng annotation từ Spring Data MongoDB
    private String id;  // MongoDB sử dụng String ID (thường là ObjectId)

    @DBRef  // Tham chiếu đến document User
    private User user;  // Liên kết với User

    private String streetAddress;

    private String city;

    private String userName;

    private String phoneNumber;

    private String country;

    private boolean isDefault;  // Đánh dấu địa chỉ mặc định (nếu có)
}
