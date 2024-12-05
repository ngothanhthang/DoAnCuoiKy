package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.User;
import vn.iotstar.services.AddressService;
import vn.iotstar.services.UserService;


@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;

    // Hiển thị form thêm hoặc cập nhật địa chỉ
    @PostMapping("/save")
    public String saveAddress(@ModelAttribute Address address, @RequestParam String orderId, @RequestParam List<String> selectedItems, @RequestParam boolean isDefault, HttpSession session, Model model
    		,RedirectAttributes redirectAttributes) {
    	Logger logger = LoggerFactory.getLogger(this.getClass());
    	Long orderIdLong;
        try {
            orderIdLong = Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            // Xử lý khi không parse được
            orderIdLong = null; // hoặc giá trị mặc định
        }
        logger.info("Is Default: {}", orderIdLong);
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L;  // Nếu không có userId trong session, giả sử là userId = 1
        }

        User user = userService.findById(userId);
        if (user != null) {
            // Nếu isDefault là true, cần cập nhật các địa chỉ khác của người dùng
            if (isDefault) {
                // Lấy tất cả địa chỉ của user, kiểm tra và cập nhật
                List<Address> userAddresses = addressService.getAddressesByUserId(userId);
                for (Address existingAddress : userAddresses) {
                    // Nếu địa chỉ đã là mặc định, set lại thành false
                    if (existingAddress.isDefault()) {
                        existingAddress.setDefault(false);
                        addressService.saveAddress(existingAddress);  // Lưu lại địa chỉ đã cập nhật
                    }
                }
            }

            // Cập nhật địa chỉ hiện tại, đảm bảo isDefault được xử lý đúng
            address.setUser(user);
            address.setDefault(isDefault);  // Đặt địa chỉ này là mặc định nếu isDefault = true
            addressService.saveAddress(address);  // Lưu địa chỉ mới hoặc cập nhật

            model.addAttribute("message", "Cập nhật địa chỉ thành công!");
        }
		/* logger.info("Is Default: {}", isDefault); */
        // Điều hướng đến trang thông tin đơn hàng với orderId
        return "redirect:/order/summary/" + orderIdLong + "?selectedItems=" + String.join(",", selectedItems);
    }

}
