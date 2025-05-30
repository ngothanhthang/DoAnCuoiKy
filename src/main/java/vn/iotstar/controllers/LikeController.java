package vn.iotstar.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductLike;
import vn.iotstar.entity.User;
import vn.iotstar.repository.ProductLikeRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.ProductService;

@RestController
@RequestMapping("/like")
public class LikeController {
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private ProductService productService;
	@Autowired
    private ProductLikeRepository productLikeRepository;
	
    @PostMapping("/product")
    public String likeProduct(@RequestParam("productId") String productId, HttpSession session) {
        // Lấy user có id = 1 từ database
        User user = userRepository.findById((String) session.getAttribute("user0")).orElse(null); 
        
        if (user == null) {
            // Nếu không tìm thấy user, có thể hiển thị lỗi hoặc thông báo gì đó
            return "redirect_to_login";
        }

        Product product = productService.getProductById(productId);

        if (product == null) {
            // Nếu không tìm thấy sản phẩm, có thể hiển thị lỗi
            return "error";
        }

        // Kiểm tra xem người dùng đã like sản phẩm này chưa
        ProductLike existingLike = productLikeRepository.findByProductAndUser(product, user);

        if (existingLike == null) {
            // Nếu chưa like, thêm like mới
            ProductLike newLike = new ProductLike();
            newLike.setProduct(product);
            newLike.setUser(user);
            productLikeRepository.save(newLike);
        } else {
            // Nếu đã like, xóa like
            productLikeRepository.delete(existingLike);
        }

        // Lấy lại thông tin sản phẩm sau khi cập nhật lượt thích
        long totalLikes = productLikeRepository.countByProduct_Id(productId);

        // Trả về số lượt thích mới (JSON)
        return String.valueOf(totalLikes);
    }
    @GetMapping("/product/count")
    public String getProductLikeCount(@RequestParam("productId") String productId) {
        long totalLikes = productLikeRepository.countByProduct_Id(productId);
        return String.valueOf(totalLikes);
    }
    
    @GetMapping("/product/status")
    public String checkLikeStatus(@RequestParam("productId") String productId, HttpSession session) {
        String userId = (String) session.getAttribute("user0");
        if (userId == null) {
            return "false";
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "false";
        }
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "false";
        }
        
        ProductLike existingLike = productLikeRepository.findByProductAndUser(product, user);
        return existingLike != null ? "true" : "false";
    }

}
