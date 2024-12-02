package vn.iotstar.services.impl;

import org.springframework.data.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.services.ProductService;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy sản phẩm theo danh mục và phân trang
    @Override
    public List<Product> getProductsByCategory(Long categoryId, int status, int page, int size) {
        return productRepository.findByCategoryIdAndStatus(categoryId, status, PageRequest.of(page, size));
    }

    // Lấy tất cả các danh mục
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
 // Phân trang sản phẩm, giới hạn 20 sản phẩm mỗi trang
    public Page<Product> getProducts(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 5);  // 20 sản phẩm mỗi trang
        return productRepository.findAll(pageable);
    }
    // Các phương thức khác như lấy sản phẩm mới, bán chạy, yêu thích, v.v.
}
