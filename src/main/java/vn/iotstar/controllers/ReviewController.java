package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Order;
import vn.iotstar.services.AddressService;
import vn.iotstar.services.OrderService;

@Controller
@RequestMapping("/order")
public class ReviewController {
	@Autowired
    private OrderService orderService;
    @GetMapping("/review/{orderId}")
    public String showReviewPage(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "review";  // Trả về trang đánh giá
    }

    @PostMapping("/review/{orderId}")
    public String submitReview(@PathVariable Long orderId, @RequestParam int rating, @RequestParam String comment) {
        // Xử lý đánh giá và lưu vào cơ sở dữ liệu
        // Cập nhật trạng thái hoặc thông tin liên quan đến đánh giá
        return "redirect:/order/thank-you";  // Điều hướng đến trang cảm ơn sau khi gửi đánh giá
    }
}