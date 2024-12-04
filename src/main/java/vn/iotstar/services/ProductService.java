package vn.iotstar.services;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    
    // Lấy tất cả các danh mục
    List<Category> getAllCategories();
    
    // Lấy sản phẩm theo danh mục và phân trang
    Page<ProductDTO> getProductsByCategory(Long categoryId, int status, Pageable pageable);

	Page<Product> getProducts(int pageNumber);

	Product getProductById(Long productId);
	
	Product save(Product product);
    
    // Các phương thức khác, ví dụ: lấy sản phẩm mới, bán chạy, yêu thích, ...
}
