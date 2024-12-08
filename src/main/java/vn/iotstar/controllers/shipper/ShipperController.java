package vn.iotstar.controllers.shipper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.iotstar.entity.Address;
import vn.iotstar.entity.Notification;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;
import vn.iotstar.services.NotificationService;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/shipper")
public class ShipperController {

	@Autowired OrderService orderService;
	@Autowired NotificationService notificationService;
	@Autowired UserService userService;
	@GetMapping()
	public String shipperHomePage()
	{
		
		return "Shipper";
	}
	
	@GetMapping("/remaining_orders")
	public String shipperHomePage(Model model) 
	{
	    // Lấy danh sách đơn hàng có trạng thái "confirmed"
	    List<Order> confirmedOrders = orderService.getOrdersByStatus("confirmed");
	    
	    // Tạo các danh sách để chứa thông tin cần thiết cho view
	    List<String> formattedDates = new ArrayList<>();
	    List<String> phoneNumbers = new ArrayList<>();
	    List<Address> defaultAddresses = new ArrayList<>(); // Lưu các địa chỉ mặc định cho mỗi đơn hàng

	    // Duyệt qua danh sách các đơn hàng
	    for (Order order : confirmedOrders) {
	        // Định dạng ngày tạo đơn hàng
	        String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        formattedDates.add(formattedDate);
	        
	        // Lấy số điện thoại từ địa chỉ mặc định
	        String phoneNumber = null;
	        Address defaultAddress = null; // Lưu địa chỉ mặc định

	        if (order.getUser() != null) {
	            List<Address> addresses = order.getUser().getAddresses();
	            for (Address address : addresses) {
	                if (address.isDefault()) {
	                    defaultAddress = address;  // Lưu địa chỉ mặc định
	                    phoneNumber = address.getPhoneNumber();  // Lấy số điện thoại
	                    break;  // Dừng vòng lặp khi đã tìm thấy địa chỉ mặc định
	                }
	            }
	        }
	        // Thêm địa chỉ mặc định và số điện thoại vào các danh sách
	        defaultAddresses.add(defaultAddress);
	        phoneNumbers.add(phoneNumber);
	    }
	    // Thêm các thuộc tính vào model để Thymeleaf có thể sử dụng
	    model.addAttribute("orders", confirmedOrders);
	    model.addAttribute("formattedDates", formattedDates);
	    model.addAttribute("phoneNumbers", phoneNumbers);
	    model.addAttribute("defaultAddresses", defaultAddresses); // Gửi địa chỉ mặc định vào view

	    // Trả về tên của trang view
	    return "Shipper";
	}
	// API nhận giao đơn hàng và gửi thông báo cho vendor
	@PostMapping("/sendToVendor")
	public ResponseEntity<?> sendToVendor(@RequestBody ShipperNotificationRequest request) {
	    // Kiểm tra đơn hàng có hợp lệ không
	    Order order = orderService.getOrderById(request.getOrderId());
	    if (order == null) {
	        return ResponseEntity.status(400).body("Đơn hàng không tồn tại.");
	    }

	    // Kiểm tra shipper có hợp lệ không
	    User shipper = userService.findById(request.getShipperId());
	    if (shipper == null) {
	        return ResponseEntity.status(400).body("Shipper không tồn tại.");
	    }

	    // Tạo thông báo
	    String message = "Shipper " + request.getShipperId() + " đã nhận đơn hàng số " + request.getOrderId() +
	                     " vào lúc " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	    // Tạo thông báo cho vendor
	    Notification notification = new Notification();
	    notification.setShipper(shipper);  // Set shipper vào thông báo
	    notification.setOrder(order);      // Set order vào thông báo
	    notification.setMessage(message);  // Set nội dung thông báo
	    notification.setTimestamp(LocalDateTime.now());  // Set thời gian tạo thông báo

	    notificationService.save(notification);  // Lưu thông báo vào cơ sở dữ liệu

	    // Trả về phản hồi thành công
	    return ResponseEntity.ok().body("Thông báo đã được gửi đến vendor.");
	}
}
