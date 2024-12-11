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
import vn.iotstar.entity.Review;

import vn.iotstar.services.OrderService;
import vn.iotstar.services.ReviewService;

@Controller
@RequestMapping("/order")
public class ReviewController {
    @GetMapping("/review/{productId}")
    public String showReviewPage(@PathVariable Long productId, Model model, HttpSession session) {
        // Lấy userId từ session, nếu không có thì mặc định là 1
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            userId = 1L;  // Gán mặc định là 1 nếu không có userId trong session
        }
        
        // Truyền userId và productId qua model
        model.addAttribute("userId", userId);
        model.addAttribute("productId", productId);

        // Trả về trang đánh giá
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