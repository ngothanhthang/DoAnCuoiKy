package vn.iotstar.controllers.vendor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.*;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.services.NotificationService;
import vn.iotstar.services.OrderService;
import vn.iotstar.utils.ApiResponse;

@Controller
public class VendorOrderController 
{
	@Autowired OrderService orderService;
	@Autowired NotificationService notificationService;
	@Autowired
	ProductRepository productRepository;
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
	    List<String> statuses = Arrays.asList("mới", "đã nhận giao","trả hàng","đã giao xong","đã nhận hàng");

        // Lấy số lượng thông báo chưa đọc với trạng thái là "Mới" hoặc "Đã nhận giao"
        Long unreadNewNotificationsCount = notificationService.countNotificationsByStatusAndNotRead(statuses);
        List<Notification> notifications = notificationService.getNotificationsByStatus(statuses);
        // Thêm số lượng thông báo vào model
        model.addAttribute("unreadNotificationCount", unreadNewNotificationsCount);
        model.addAttribute("notifications", notifications);

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
	
	// controller chuyển duyệt đơn hàng đi giao:
	@PostMapping("/vendor/order/approve-delivery/{orderId}")
	public ResponseEntity<ApiResponse> approveDelivery(@PathVariable("orderId") Long orderId) {
	    try {
	        Order order = orderService.approveDelivery(orderId); // Gọi service để duyệt đơn hàng đi giao

	        if (order != null) {
	            // Trả về ApiResponse thành công với thông tin đơn hàng đã duyệt
	            return ResponseEntity.ok()
	                .body(new ApiResponse(true, "Đơn hàng đã được duyệt đi giao thành công", null));
	        } else {
	            // Trả về ApiResponse thất bại nếu không thể duyệt đơn hàng
	            return ResponseEntity.badRequest()
	                .body(new ApiResponse(false, "Không tìm thấy đơn hàng hoặc đơn hàng không ở trạng thái chờ duyệt", null));
	        }
	    } catch (Exception e) {
	        // Xử lý các exception và trả về thông báo lỗi phù hợp
	        return ResponseEntity.badRequest()
	            .body(new ApiResponse(false, "Có lỗi xảy ra khi duyệt đơn hàng: " + e.getMessage(), null));
	    }
	}
	
	//controller chấp nhận đơn hàng trả lại:
	 @PostMapping("/vendor/order/accept/{orderId}")
	    public ResponseEntity<ApiResponse> acceptOrder(@PathVariable("orderId") Long orderId) 
	 {
	        try {
	            Order order = orderService.acceptOrder(orderId);  // Gọi service để chấp nhận đơn hàng

	            if (order != null) {
	                // Trả về ApiResponse thành công với thông tin đơn hàng đã chấp nhận
	                return ResponseEntity.ok()
	                        .body(new ApiResponse(true, "Đơn hàng đã được chấp nhận", null));
	            } else {
	                // Trả về ApiResponse thất bại nếu không thể chấp nhận đơn hàng
	                return ResponseEntity.badRequest()
	                        .body(new ApiResponse(false, "Không thể chấp nhận đơn hàng", null));
	            }
	        } catch (Exception e) {
	            // Xử lý các exception và trả về thông báo lỗi phù hợp
	            return ResponseEntity.badRequest()
	                    .body(new ApiResponse(false, "Có lỗi xảy ra khi chấp nhận đơn hàng: " + e.getMessage(), null));
	        }
	  }
	 
	 //controller từ chối đơn hàng trả lại:
	 @PostMapping("/vendor/order/reject/{orderId}")
	    public ResponseEntity<ApiResponse> rejectOrder(@PathVariable("orderId") Long orderId) 
	 {
	        try {
	            Order order = orderService.rejectOrder(orderId);  // Gọi service để từ chối đơn hàng

	            if (order != null) {
	                // Trả về ApiResponse thành công với thông tin đơn hàng đã bị từ chối
	                return ResponseEntity.ok()
	                        .body(new ApiResponse(true, "Đơn hàng đã bị từ chối", null));
	            } else {
	                // Trả về ApiResponse thất bại nếu không thể từ chối đơn hàng
	                return ResponseEntity.badRequest()
	                        .body(new ApiResponse(false, "Không thể từ chối đơn hàng", null));
	            }
	        } catch (Exception e) {
	            // Xử lý các exception và trả về thông báo lỗi phù hợp
	            return ResponseEntity.badRequest()
	                    .body(new ApiResponse(false, "Có lỗi xảy ra khi từ chối đơn hàng: " + e.getMessage(), null));
	        }
	    }
	 // Xác nhận đơn hàng đã giao:
	 @PostMapping("/vendor/order/complete/{orderId}")
	 public ResponseEntity<ApiResponse> confirmDeliveredOrder(@PathVariable("orderId") Long orderId) {
		 try {
			 Order order = orderService.confirmDeliveredOrder(orderId);

			 if (order != null)
			 {
				 for (OrderItem orderItem : order.getOrderItems()) {
					 Product product = orderItem.getProduct();
					 int orderedQuantity = orderItem.getQuantity();

					 // Kiểm tra và cập nhật số lượng trong kho
					 int remainingQuantity = product.getQuantity() - orderedQuantity;
					 if (remainingQuantity < 0) {
						 throw new RuntimeException("Số lượng sản phẩm " + product.getName() + " không đủ trong kho");
					 }

					 // Cập nhật số lượng còn lại
					 product.setQuantity(remainingQuantity);

					 // Lưu cập nhật vào database
					 productRepository.save(product);
				 }

				 return ResponseEntity.ok()
						 .body(new ApiResponse(true, "Đơn hàng đã được xác nhận giao thành công", null));
			 } else {
				 return ResponseEntity.badRequest()
						 .body(new ApiResponse(false, "Không tìm thấy đơn hàng hoặc đơn hàng không ở trạng thái đang giao", null));
			 }
		 } catch (Exception e) {
			 return ResponseEntity.badRequest()
					 .body(new ApiResponse(false, "Có lỗi xảy ra khi xác nhận giao đơn hàng: " + e.getMessage(), null));
		 }
	 }
}
