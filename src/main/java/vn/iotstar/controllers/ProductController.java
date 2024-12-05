package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.OrderItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductLike;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.ProductService;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;
    
    // Trang chủ
    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        return "home";  // Trang chủ, trả về home.html
    }
    
    @GetMapping("/products")
    public String getProducts(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Product> productPage = productService.getProducts(page);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "productList";  // Trang hiển thị danh sách sản phẩm
    }

 // Hiển thị sản phẩm theo danh mục với phân trang
    @GetMapping("/category")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId,
                                         @RequestParam(value = "status", defaultValue = "1") int status,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "5") int size,
                                         @RequestParam(value = "sort", defaultValue = "createdAt") String sortOption,
                                         Model model) {

        // Xử lý sắp xếp theo sortOption
        Sort sort = null;
        switch (sortOption) {
            case "newest":
                sort = Sort.by(Sort.Order.desc("createdAt"));
                break;
			case "best_selling": 
				sort = Sort.by(Sort.Order.desc("totalSold"));
				break;
			 
            case "ratings":
                sort = Sort.by(Sort.Order.desc("averageRating"));  // Average rating đã được tính trong service
                break;
            case "price_low_to_high":
                sort = Sort.by(Sort.Order.asc("price"));  // Average rating đã được tính trong service
                break;
            case "price_high_to_low":
                sort = Sort.by(Sort.Order.desc("price"));  // Average rating đã được tính trong service
                break;
            default:
                sort = Sort.by(Sort.Order.desc("createdAt"));
                break;
        }
        

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> productDTOPage = productService.getProductsByCategory(categoryId, status, pageable);

        model.addAttribute("products", productDTOPage);
        model.addAttribute("totalPages", productDTOPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortOption", sortOption);
        return "productList";  // Trang hiển thị sản phẩm
    }


 // Hiển thị chi tiết sản phẩm
    @GetMapping("/product")
    public String getProductDetails(@RequestParam("productId") Long productId, Model model) {
    	// Lấy user có id = 1 từ database
        User user = userRepository.findByUserId((long) 1) ; // Tìm user có id = 1

        if (user == null) {
            // Nếu không tìm thấy user, có thể hiển thị lỗi hoặc thông báo gì đó
            return "error";
        }
        
        Product product = productService.getProductById(productId);

        // Lấy danh sách đánh giá của sản phẩm
        List<Review> reviews = product.getReviews();
        
        int totalLikes= product.getProductLikes().size();
        
     // Kiểm tra xem người dùng đã like sản phẩm chưa
        boolean isLiked = product.getProductLikes().stream()
                                  .anyMatch(like -> like.getUser().getUserId().equals(user.getUserId()));
        
     // Tính tổng số đánh giá và điểm trung bình
        int totalReviews = reviews.size();
        double averageRating = reviews.isEmpty() ? 0 : reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        
     // Làm tròn điểm trung bình đến 1 chữ số thập phân
        averageRating = Math.round(averageRating * 10.0) / 10.0;
        
        int totalSoldQuantity = 0;
        for (OrderItem orderItem : product.getOrderItems()) {
            totalSoldQuantity += orderItem.getQuantity(); // Cộng dồn số lượng
        }
        
     // Thêm các thông tin vào model
        model.addAttribute("user", user);
        model.addAttribute("product", product);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("totalLikes", totalLikes);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalSoldQuantity", totalSoldQuantity);
        model.addAttribute("isLiked", isLiked);
        
        return "productDetail";  // Trang chi tiết sản phẩm
    }
}
