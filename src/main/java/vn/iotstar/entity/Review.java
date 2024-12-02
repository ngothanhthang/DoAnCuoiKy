package vn.iotstar.entity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

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
	    private User user;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "product_id", nullable = false)
	    private Product product;

	    @Column(name = "rating", nullable = false)
	    private int rating;

	    @Column(name = "review_text", columnDefinition = "TEXT")
	    private String reviewText;

	    @Column(name = "created_at", nullable = false, updatable = false)
	    @CreationTimestamp
	    private LocalDateTime createdAt;

}
