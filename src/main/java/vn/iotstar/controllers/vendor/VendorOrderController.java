package vn.iotstar.controllers.vendor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Address;
import vn.iotstar.entity.Order;
import vn.iotstar.services.OrderService;
import vn.iotstar.utils.ApiResponse;

@Controller
public class VendorOrderController 
{
	@Autowired OrderService orderService;
	@GetMapping("/vendor/orders")
	public String viewOrders(@RequestParam(value = "status", required = false) String status, Model model) {
	    // Khai báo danh sách đơn hàng
	    List<Order> orders;

	    // Nếu không có tham số status, lấy tất cả các đơn hàng
	    if (status == null || status.isEmpty()) {
	        orders = orderService.getAllOrders();
	    } else {
	        // Lọc đơn hàng theo trạng thái
	        orders = orderService.getOrdersByStatus(status);
	    }

	    // Định dạng ngày tháng cho từng đơn hàng
	    List<String> formattedDates = new ArrayList<>();
	    List<String> phoneNumbers = new ArrayList<>();
	    List<Address> defaultAddresses = new ArrayList<>();

	    // Duyệt qua danh sách đơn hàng để lấy thông tin cần thiết
	    for (Order order : orders) {
	        // Định dạng ngày tạo cho từng đơn hàng
	        String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        formattedDates.add(formattedDate);
	        
	        // Khởi tạo giá trị mặc định cho số điện thoại và địa chỉ mặc định
	        String phoneNumber = null;
	        Address defaultAddress = null;

	        // Kiểm tra nếu đơn hàng có người dùng và địa chỉ
	        if (order.getUser() != null && order.getUser().getAddresses() != null) {
	            List<Address> addresses = order.getUser().getAddresses();
	            // Lặp qua các địa chỉ của người dùng để tìm địa chỉ mặc định
	            for (Address address : addresses) {
	                if (address.isDefault()) {
	                    defaultAddress = address;  // Lưu địa chỉ mặc định
	                    phoneNumber = address.getPhoneNumber();  // Lấy số điện thoại
	                    break;  // Dừng vòng lặp khi đã tìm thấy địa chỉ mặc định
	                }
	            }
	        }

	        // Thêm địa chỉ mặc định và số điện thoại vào danh sách
	        defaultAddresses.add(defaultAddress);
	        phoneNumbers.add(phoneNumber);
	    }

	    // Thêm thông tin vào model để truyền vào view
	    model.addAttribute("orders", orders);
	    model.addAttribute("formattedDates", formattedDates);
	    model.addAttribute("phoneNumbers", phoneNumbers);
	    model.addAttribute("defaultAddresses", defaultAddresses);

	    // Trả về trang quản lý đơn hàng
	    return "ManageOrder";
	}
	
	@PostMapping("/vendor/order/confirm/{orderId}")
    public ResponseEntity<ApiResponse> confirmOrder(@PathVariable("orderId") Long orderId)
	{
		
        Order order = orderService.confirmOrder(orderId); // Gọi service để xác nhận đơn hàng

        if (order != null)
        {
            // Trả về ApiResponse thành công với thông tin đơn hàng đã xác nhận
            return ResponseEntity.ok().body(new ApiResponse(true, "Đơn hàng đã được xác nhận", null));
        } 
        else 
        {
            // Trả về ApiResponse thất bại nếu không thể xác nhận đơn hàng
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Có lỗi xảy ra khi xác nhận đơn hàng", null));
        }
    }
}
