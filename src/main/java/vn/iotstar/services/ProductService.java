package vn.iotstar.services;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;

import java.util.List;

import org.springframework.data.domain.Page;

public interface ProductService {
    
    // Lấy tất cả các danh mục
    List<Category> getAllCategories();
    
    // Lấy sản phẩm theo danh mục và phân trang
    List<Product> getProductsByCategory(Long categoryId, int status, int page, int size);

	Page<Product> getProducts(int pageNumber);
    
    // Các phương thức khác, ví dụ: lấy sản phẩm mới, bán chạy, yêu thích, ...
}
