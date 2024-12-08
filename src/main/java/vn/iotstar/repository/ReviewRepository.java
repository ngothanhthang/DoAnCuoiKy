package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByProductId(Long orderId);
}
