package vn.iotstar.controllers.vendor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Order;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.services.DashBoardService;
import vn.iotstar.services.OrderService;

@Controller
@RequestMapping("/vendor/revenue")
public class VendorRevenueController 
{
	@Autowired
	private DashBoardService dashBoardService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;
	@GetMapping()
	public String revenuePage()
	{
		return "manageRevenue";
	}
	@GetMapping("/overview")
	public String showDashboard(Model model) 
	{
        // Lấy các dữ liệu từ service
        long totalOrders = dashBoardService.getTotalOrders();
        BigDecimal totalRevenue = dashBoardService.getTotalRevenue();
        long totalCustomers = dashBoardService.getTotalCustomers();
        double customerSatisfaction = dashBoardService.getCustomerSatisfaction();

        // Đưa dữ liệu vào model để gửi ra view (index.html)
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("customerSatisfaction", customerSatisfaction);

        return "OverViewRevenue";  // Chuyển đến view index.html
    }
	   @GetMapping("/receipt")
	    public String listOrders(
	            @RequestParam(name = "search", required = false, defaultValue = "") String search,
	            @RequestParam(name = "status", required = false, defaultValue = "") String status,
	            @RequestParam(name = "page", defaultValue = "0") int page,
	            Model model) 
	   {
	        
	        // Số đơn hàng trên mỗi trang
	        int size = 10;
	        
	        // Gọi service để lấy dữ liệu
	        Page<Order> orders = orderService.getOrders(search, status, page, size);
	        
	        // Thêm dữ liệu vào model
	        model.addAttribute("orders", orders);
	        model.addAttribute("search", search);
	        model.addAttribute("status", status);
	        
	        // Tính toán số trang
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", orders.getTotalPages());
	        
	        return "manageReceipt";
	    }
}
	