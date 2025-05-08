package vn.iotstar.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "reviews")
@CompoundIndexes({
    @CompoundIndex(name = "user_product_idx", def = "{'user.$id': 1, 'product.$id': 1}", unique = true)
})
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @DBRef
    private User user;  // Liên kết tới người dùng
	
    @DBRef
    private Product product;  // Tham chiếu tới entity product

    private int rating;  // Đánh giá của người dùng

    private String reviewText;  // Nội dung đánh giá

    @CreatedDate
    private Date createdAt;  // Tự động gán thời gian tạo
    
    // Thêm trường media cho ảnh/video
    private String imageUrl;  // Lưu trữ URL của ảnh

    private String videoUrl;  // Lưu trữ URL của video
    
    // Phương thức tương thích ngược nếu cần
    public Long getLegacyId() {
        try {
            return id != null ? Long.parseLong(id) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}


/*package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;  // Liên kết tới người dùng
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Tham chiếu tới entity product

    @Column(name = "rating", nullable = false)
    private int rating;  // Đánh giá của người dùng

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;  // Nội dung đánh giá

    @Column(name = "created_at", nullable = false, updatable = false)
    
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp  // Tự động gán thời gian tạo
    private java.util.Date createdAt;
    
 // Thêm trường media cho ảnh/video
    @Column(name = "image_url")
    private String imageUrl;  // Lưu trữ URL của ảnh

    @Column(name = "video_url")
    private String videoUrl;  // Lưu trữ URL của video
    
    
}*/