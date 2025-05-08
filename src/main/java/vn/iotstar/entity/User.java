package vn.iotstar.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "users")
@Builder
public class User implements UserDetails {
    @Id
    private String id;

    private String username;

    private String password;

    private String email;

    private Boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String resetPasswordToken;
    
    @DBRef(lazy = true)
    private List<Review> reviews;
    
    @DBRef(lazy = true)
    private List<ProductLike> productLikes;
    
    @DBRef(lazy = true)
    private Cart cart;
    
    @DBRef(lazy = true)
    private List<Address> addresses;
    
    @DBRef(lazy = true)
    private List<UserCoupon> userCoupons;
    
    private String avatarUrl;

    private String phoneNumber;

    private Gender gender;

    private LocalDate dateOfBirth;

    // Enum cho giới tính
    public enum Gender {
        MALE, 
        FEMALE, 
        OTHER
    }
    
    // Sử dụng DBRef để tham chiếu đến Role
    @DBRef
    private Role role;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        if (role != null) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
        }
        return authorityList;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
