package vn.iotstar.services;

import java.util.List;

import vn.iotstar.entity.Review;

public interface ReviewService {

	void saveReview(Review review);

	List<Review> findReviewsByOrderId(Long orderId);

}
