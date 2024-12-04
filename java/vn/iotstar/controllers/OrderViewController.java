package vn.iotstar.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Order;
import vn.iotstar.services.AddressService;
import vn.iotstar.services.OrderService;

@Controller
@RequestMapping("/order")
public class OrderViewController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AddressService addressService;

    @GetMapping("/summary/{orderId}")
    public String orderSummary(@PathVariable("orderId") Long orderId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L;  // Giả sử là userId = 1 nếu không tìm thấy
        }

        // Kiểm tra quyền truy cập: đảm bảo đơn hàng thuộc về người dùng
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUser().getUserId().equals(userId)) {
            model.addAttribute("message", "Không tìm thấy đơn hàng hoặc bạn không có quyền truy cập.");
            return "error";  // Trang lỗi nếu không hợp lệ
        }
        
     // Lấy địa chỉ mặc định của người dùng
        Address defaultAddress = addressService.getDefaultAddress(userId);
        
     // Lấy danh sách tất cả địa chỉ của người dùng
        List<Address> allAddresses = addressService.getAddressesByUserId(userId);
        
        // Thêm thông tin đơn hàng và địa chỉ vào model
        model.addAttribute("order", order);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("allAddresses", allAddresses);

        return "order_summary";
    }
    
 // Xử lý yêu cầu GET cho trang danh sách đơn hàng với trạng thái
    @GetMapping("/purchase")
    public String viewOrders(@RequestParam(name = "status", defaultValue = "ALL") String status, Model model, HttpSession session) {
    	Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L; // Mặc định là userId = 1
        }
        // Lấy danh sách đơn hàng theo trạng thái từ service
        // Giả sử OrderService có phương thức findOrdersByStatus để lấy danh sách đơn hàng theo trạng thái
        List<Order> orders;
        
        switch (status) {
            case "1":
                orders = orderService.findOrdersByStatusAndUserId("đã xác nhận", userId);
                break;
            case "2":
                orders = orderService.findOrdersByStatusAndUserId("đang giao", userId);
                break;
            case "3":
                orders = orderService.findOrdersByStatusAndUserId("đã giao", userId);
                break;
            case "4":
                orders = orderService.findOrdersByStatusAndUserId("hủy", userId);
                break;
            case "5":
                orders = orderService.findOrdersByStatusAndUserId("trả hàng", userId);
                break;
            case "0":
            default:
                orders = orderService.findOrdersByUserId(userId);
                break;
        }

        // Thêm danh sách đơn hàng vào model
        model.addAttribute("orders", orders);

        // Trả về tên view
        return "orderPurchase";  // Tên của template Thymeleaf
    }

}
