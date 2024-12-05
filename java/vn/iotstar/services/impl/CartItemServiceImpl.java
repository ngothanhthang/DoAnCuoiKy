package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.services.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;  // Tiêm repository vào service

    @Override
    public void deleteById(Long id) {
        // Kiểm tra sự tồn tại của CartItem trước khi xóa (tùy chọn)
        if (cartItemRepository.existsById(id)) {
            cartItemRepository.deleteById(id);  // Gọi phương thức deleteById từ repository
            System.out.println("CartItem with ID " + id + " has been deleted.");
        } else {
            System.out.println("CartItem with ID " + id + " does not exist.");
        }
    }
}
