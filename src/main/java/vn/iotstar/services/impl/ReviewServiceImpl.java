package vn.iotstar.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;
import vn.iotstar.repository.ReviewRepository;
import vn.iotstar.services.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Override
    public List<Review> findReviewsByOrderId(String orderId) {
        // Giả sử chúng ta cần tìm tất cả các đánh giá liên quan đến một đơn hàng
        return reviewRepository.findByProductId(orderId);
    }
    
    @Override
    public void saveReview(Review review) {
        reviewRepository.save(review);
    }
    
    @Override
    public Review findByUserAndProduct(User user, Product product) {
        return reviewRepository.findByUserAndProduct(user, product);
    }

    
    @Override
    public Review findByUserIdAndProductId(String userId, String productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId);
    }
}
