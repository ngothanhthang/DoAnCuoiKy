package vn.iotstar.controllers.shipper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.iotstar.dto.NotificationRequest;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Notification;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;
import vn.iotstar.repository.NotificationRepository;
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
	@Autowired NotificationRepository notificationRepository;
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
	@PostMapping("/createNotification")
	public ResponseEntity<Map<String, Object>> createNotification(@RequestBody NotificationRequest request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Notification notification = new Notification();
	        User shipper= userService.findById(request.getShipperId());
	        Order order=orderService.getOrderById(request.getOrderId());
	        notification.setUser(shipper);
	        notification.setOrder(order);
	        notification.setMessage("Đơn hàng " + order.getId() + " đã được shipper " + shipper.getUsername() + " nhận giao.");
	        notification.setTimestamp(new Date());  // Gán thời gian hiện tại
	        notification.setStatus("đã nhận giao");
	        notificationService.save(notification);
	        // Cập nhật lại trạng thái đơn hàng là "chờ duyệt đi giao" 
	        orderService.updateOrderStatus(request.getOrderId(), "Chờ duyệt đi giao");
	        response.put("success", true);
	        response.put("message", "Thông báo đã được tạo thành công.");
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "Có lỗi xảy ra khi tạo thông báo.");
	    }
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/confirmed-orders")
	public String getConfirmedOrders(Model model) {
		Long shipperId= 1L;
		List<Order> confirmedOrders = orderService.getOrdersByShipperAndStatus(shipperId, "Đã nhận hàng");
		List<String> formattedDates = new ArrayList<>();
        List<String> phoneNumbers = new ArrayList<>();
        List<Address> defaultAddresses = new ArrayList<>();

        for (Order order : confirmedOrders) {
            // Format order creation date
            String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            formattedDates.add(formattedDate);
            
            // Get phone number from default address
            String phoneNumber = null;
            Address defaultAddress = null;

            if (order.getUser() != null) {
                for (Address address : order.getUser().getAddresses()) {
                    if (address.isDefault()) {
                        defaultAddress = address;
                        phoneNumber = address.getPhoneNumber();
                        break;
                    }
                }
            }

            defaultAddresses.add(defaultAddress);
            phoneNumbers.add(phoneNumber);
        }

        model.addAttribute("orders", confirmedOrders);
        model.addAttribute("formattedDates", formattedDates);
        model.addAttribute("phoneNumbers", phoneNumbers);
        model.addAttribute("defaultAddresses", defaultAddresses);
        
        return "Shipper";
	}
	
	@PostMapping("/complete-order/{orderId}")
	public ResponseEntity<Map<String, Object>> completeOrder(@PathVariable Long orderId) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Order order = orderService.getOrderById(orderId);
	        if (order != null && "Đã nhận hàng".equals(order.getStatus())) {
	            // Cập nhật trạng thái đơn hàng
	            
	            // Tạo thông báo đã giao
	            Notification notification = new Notification();
	            notification.setOrder(order);
	            notification.setUser(order.getUser());
	            String shipperName = order.getUser() != null ? order.getUser().getUsername() : "Không xác định";
	            String message = "Đơn hàng " + orderId + " đã được giao thành công bởi shipper " + shipperName + ".";
	            notification.setMessage(message);
	            notification.setTimestamp(new Date());
	            notification.setStatus("đã giao xong");
	            notificationService.save(notification);
	            
	            response.put("success", true);
	            response.put("message", "Thông báo đơn hàng giao thành công đã được gửi đến chủ shop");
	        } else {
	            response.put("success", false);
	            response.put("message", "Không tìm thấy đơn hàng hoặc trạng thái không hợp lệ.");
	        }
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "Có lỗi xảy ra khi hoàn thành đơn hàng.");
	        e.printStackTrace();
	    }
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/completed-orders")
	public String getCompletedOrders(Model model) {
	    Long shipperId = 1L; // Sau này lấy từ session hoặc authentication
	    
	    // Lấy các đơn hàng đã hoàn thành của shipper
	    List<Order> completedOrders = notificationRepository
	            .findDistinctOrdersByUser_UserIdAndOrderStatus(shipperId, "Đã hoàn thành")
	            .stream()
	            .map(Notification::getOrder)
	            .distinct()
	            .collect(Collectors.toList());
	    
	    // Chuẩn bị dữ liệu cho view
	    List<String> formattedDates = new ArrayList<>();
	    List<String> phoneNumbers = new ArrayList<>();
	    List<Address> defaultAddresses = new ArrayList<>();

	    for (Order order : completedOrders) {
	        // Format order creation date
	        String formattedDate = order.getCreatedAt()
	            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        formattedDates.add(formattedDate);
	        
	        // Get phone number from default address
	        String phoneNumber = null;
	        Address defaultAddress = null;

	        if (order.getUser() != null) {
	            for (Address address : order.getUser().getAddresses()) {
	                if (address.isDefault()) {
	                    defaultAddress = address;
	                    phoneNumber = address.getPhoneNumber();
	                    break;
	                }
	            }
	        }

	        defaultAddresses.add(defaultAddress);
	        phoneNumbers.add(phoneNumber);
	    }

	    // Add attributes to model
	    model.addAttribute("orders", completedOrders);
	    model.addAttribute("formattedDates", formattedDates);
	    model.addAttribute("phoneNumbers", phoneNumbers);
	    model.addAttribute("defaultAddresses", defaultAddresses);
	    
	    return "Shipper";
	}
}
