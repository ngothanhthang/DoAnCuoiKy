package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.services.ProductService;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

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
                                        Model model) {
        Page<Product> productPage = productService.getProductsByCategory(categoryId, status, page, size);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("categoryId", categoryId);
        return "productList";  // Trang hiển thị sản phẩm của danh mục
    }
 // Hiển thị chi tiết sản phẩm
    @GetMapping("/product")
    public String getProductDetails(@RequestParam("productId") Long productId, Model model) {
        Product product = productService.getProductById(productId);

        // Lấy danh sách đánh giá của sản phẩm
        List<Review> reviews = product.getReviews();
        
     // Tính tổng số đánh giá và điểm trung bình
        int totalReviews = reviews.size();
        double averageRating = reviews.isEmpty() ? 0 : reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        
     // Làm tròn điểm trung bình đến 1 chữ số thập phân
        averageRating = Math.round(averageRating * 10.0) / 10.0;
        
     // Thêm các thông tin vào model
        model.addAttribute("product", product);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("averageRating", averageRating);
        
        return "productDetail";  // Trang chi tiết sản phẩm
    }
}
