package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, columnDefinition = "nvarchar(255)")
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(255)")
    private String password;

    @Column(name = "email", nullable = false, columnDefinition = "nvarchar(255)")
    private String email;

    @Column(name = "role", nullable = false, columnDefinition = "nvarchar(50)")
    private String role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)  // Một User có thể có nhiều Review
    private List<Review> reviews;  // Danh sách các Review mà người dùng đã viết
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)  // Một User có thể thích nhiều sản phẩm
    private List<ProductLike> productLikes;  // Danh sách các sản phẩm mà người dùng đã thích
    
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Cart cart;
    
 // Mối quan hệ One-to-Many với Address (mỗi user có thể có nhiều địa chỉ)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Address> addresses;

}
