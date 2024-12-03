
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

	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;  // Liên kết tới người dùng
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Tham chi?u t?i entity Product

    @Column(name = "rating", nullable = false)
    private int rating;  // ��nh gi� c?a ngu?i d�ng (1-5)

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;  // N?i dung d�nh gi�

    @Column(name = "created_at", nullable = false, updatable = false)
    
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp  // T? d?ng g�n th?i gian t?o
    private java.util.Date createdAt;

}