package vn.iotstar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/order")
public class ReviewController {

    @GetMapping("/review/{orderId}")
    public String getReviewPage(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "reviewPage";  // Tên của trang HTML đánh giá
    }

    @PostMapping("/review/{orderId}")
    public String submitReview(@PathVariable Long orderId, @RequestParam int rating, @RequestParam String comment) {
        // Xử lý đánh giá và lưu vào cơ sở dữ liệu
        // Cập nhật trạng thái hoặc thông tin liên quan đến đánh giá
        return "redirect:/order/thank-you";  // Điều hướng đến trang cảm ơn sau khi gửi đánh giá
    }
}