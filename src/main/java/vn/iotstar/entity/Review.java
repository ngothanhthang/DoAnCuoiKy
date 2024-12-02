package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Reviews")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

	/*
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "user_id", nullable = false) private User user; // Tham
	 * chiếu tới entity User
	 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Tham chiếu tới entity Product

    @Column(name = "rating", nullable = false)
    private int rating;  // Đánh giá của người dùng (1-5)

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;  // Nội dung đánh giá

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp  // Tự động gán thời gian tạo
    private java.util.Date createdAt;

}
