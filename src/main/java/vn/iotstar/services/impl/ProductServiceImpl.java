package vn.iotstar.services.impl;

import org.springframework.data.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.services.ProductService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy sản phẩm theo danh mục, status và phân trang
    @Override
    public Page<Product> getProductsByCategory(Long categoryId, int status, int page, int size) {
        // Tạo Pageable cho phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))); // Sắp xếp theo tên (hoặc bạn có thể thay đổi theo nhu cầu)
        
        // Truy vấn sản phẩm theo categoryId và status với phân trang
        return productRepository.findByCategoryIdAndStatus(categoryId, status, pageable);
    }

    // Lấy tất cả các danh mục
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Phân trang sản phẩm, giới hạn 20 sản phẩm mỗi trang
    @Override
    public Page<Product> getProducts(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 5);  // 5 sản phẩm mỗi trang
        return productRepository.findAll(pageable);
    }
    
    @Override
    // Lấy sản phẩm theo ID
    public Product getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.orElse(null);  // Trả về sản phẩm nếu tìm thấy, nếu không trả về null
    }
}
