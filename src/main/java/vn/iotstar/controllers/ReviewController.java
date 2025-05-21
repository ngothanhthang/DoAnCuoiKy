package vn.iotstar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.services.CategoryService;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.ProductService;
import vn.iotstar.services.ReviewService;

@Controller
@RequestMapping("/order")
public class ReviewController {
	@Autowired
    private ProductService productService;
	@Autowired
    private ReviewService reviewService;
	@GetMapping("/review/{productId}")
	public String showReviewForm(@PathVariable("productId") String productId, Model model, HttpSession session) {
	    // Log thông tin đầu vào
	    
	    // Lấy userId từ session
	    String userId = (String) session.getAttribute("user0");
	    
	    // Kiểm tra nếu userId null
	    if (userId == null) {
	        // Có thể chuyển hướng đến trang đăng nhập hoặc xử lý khác
	        // return "redirect:/login";
	    }
	    
	    // Lấy thông tin sản phẩm
	    try {
	        Product product = productService.getProductById(productId);
	        
	        if (product == null) {
	            // Có thể xử lý trường hợp không tìm thấy sản phẩm
	            // return "error";
	        }
	        
	        model.addAttribute("product", product);
	    } catch (Exception e) {
	        e.printStackTrace();
	        // return "error";
	    }
	    
	    // Tìm review hiện có
	    try {
	        if (userId != null) {
	            Review existingReview = reviewService.findByUserIdAndProductId(userId, productId);
	            
	            model.addAttribute("existingReview", existingReview);
	        } else {
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Vẫn tiếp tục vì người dùng có thể chưa có review
	    }
	    
	    return "reviewPage";
	}

	/*
	 * @PostMapping("/review/{orderId}") public String submitReview(@PathVariable
	 * Long orderId, @RequestParam int rating, @RequestParam String comment) { // Xử
	 * lý đánh giá và lưu vào cơ sở dữ liệu // Cập nhật trạng thái hoặc thông tin
	 * liên quan đến đánh giá return "redirect:/order/thank-you"; // Điều hướng đến
	 * trang cảm ơn sau khi gửi đánh giá }
	 */
}