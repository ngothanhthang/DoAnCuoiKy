package vn.iotstar.controllers;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
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
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.services.AddressService;
import vn.iotstar.services.CartItemService;
import vn.iotstar.services.CartService;
import vn.iotstar.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/order")
public class OrderViewController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartService cartService;

    @GetMapping("/summary/{orderId}")
    public String orderSummary(@PathVariable("orderId") Long orderId, @RequestParam(required = false) List<Long> selectedItems, Model model, HttpSession session) {
    	// Khởi tạo Logger
        Logger logger = LoggerFactory.getLogger(OrderController.class);
     // In thử selectedItems vào log để xem kết quả
        logger.info("Selected items: " + (selectedItems != null ? selectedItems.toString() : "No selected items"));
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
        model.addAttribute("selectedItem", selectedItems);
        model.addAttribute("order", order);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("allAddresses", allAddresses);

        return "order_summary";
    }
   
    @GetMapping("/purchase")
    public String viewOrders(
            @RequestParam(name = "status", defaultValue = "ALL") String status,
            Model model,
            HttpSession session) {

        // Lấy userId từ session
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L; // Giá trị mặc định nếu userId không có trong session
        }

        // Lấy danh sách đơn hàng theo trạng thái
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

        // Trả về view hiển thị danh sách đơn hàng
        return "orderPurchase";
    }
    
    @GetMapping("/purchases")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("selectedItem") List<Long> selectedItems,
                                    Model model,
                                    HttpSession session) {
    	Logger logger = LoggerFactory.getLogger(OrderController.class);
    	Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L;
        }
        // Lấy đơn hàng theo orderId
        Order order = orderService.getOrderById(orderId);

        if (order != null) {
            // Cập nhật trạng thái của đơn hàng
            order.setStatus("Chờ xác nhận");
            orderService.save(order);
            
         // Lấy giỏ hàng của người dùng
            Cart cart = cartService.getCartByUserId(userId);

            if (cart != null) {
                // Duyệt qua danh sách selectedItems và xóa các sản phẩm trong giỏ hàng
                List<CartItem> cartItems = cart.getCartItems();
                
                // Tạo một danh sách tạm để lưu các phần tử sẽ bị xóa
                List<CartItem> itemsToRemove = new ArrayList<>();
                for (Long selectedItem : selectedItems) {
                    boolean found = false;
                    for (CartItem item : cartItems) {
                        // Kiểm tra nếu id của item trùng với selectedItem
                        if (item.getId().equals(selectedItem)) {
                            found = true;
                            itemsToRemove.add(item);
                            break; // Đã tìm thấy, không cần kiểm tra tiếp
                        }
                    }

                    // Log kiểm tra kết quả so sánh
                    logger.info("Item ID: " + selectedItem + " found in cart: " + found);
                }

                if (!itemsToRemove.isEmpty()) {
                    boolean allItemsDeleted = true;  // Biến để kiểm tra tất cả các mục đã được xóa

                    // Duyệt qua từng CartItem trong danh sách itemsToRemove và gọi deleteById() cho từng item
                    for (CartItem item : itemsToRemove) {
                        try {              
                                cartItemService.deleteById(item.getId());
                                logger.info("Item with ID " + item.getId() + " was successfully deleted.");
                            
                        } catch (Exception e) {
                            // Xử lý ngoại lệ (nếu có lỗi xảy ra khi xóa)
                            allItemsDeleted = false;
                            logger.error("Failed to delete item with ID " + item.getId() + ": " + e.getMessage());
                        }
                    }

                    // Log kết quả tổng thể việc xóa
                    if (allItemsDeleted) {
                        logger.info("All selected items were successfully deleted.");
                    } else {
                        logger.warn("Some items could not be deleted.");
                    }

                    // Lưu lại giỏ hàng sau khi xóa sản phẩm
                    cartService.save(cart);
                    // Log giỏ hàng sau khi xóa
                    logger.info("Cart after removal: " + cart.getCartItems().size() + " items remaining.");
                } else {
                    logger.warn("No items were removed from the cart.");
                }

            }
        }       
        // Sau khi cập nhật, chuyển hướng đến trang danh sách đơn hàng
        return "redirect:/order/purchase";
    }
}
