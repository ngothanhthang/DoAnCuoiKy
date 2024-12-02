package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
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

    // Sản phẩm theo danh mục
    @GetMapping("/category/{categoryId}")
    public String category(@PathVariable Long categoryId, 
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           Model model) {
        List<Product> products = productService.getProductsByCategory(categoryId, 1, page, size);
        model.addAttribute("products", products);
        return "category";  // Trang sản phẩm theo danh mục, trả về category.html
    }

    // Sản phẩm bán chạy
    @GetMapping("/best-sellers")
    public String bestSellers(Model model) {
        // Giả sử sản phẩm bán chạy có status = 2
        List<Product> bestSellers = productService.getProductsByCategory(null, 2, 0, 20);
        model.addAttribute("products", bestSellers);
        return "best-sellers";  // Trang sản phẩm bán chạy, trả về best-sellers.html
    }
}
