package vn.iotstar.services.impl;

import org.springframework.data.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.services.ProductService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy sản phẩm theo danh mục, status và phân trang
    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int status, Pageable pageable) {
        // Lấy dữ liệu từ repository với averageRating tính toán từ cơ sở dữ liệu
        Page<Object[]> results = productRepository.findByCategoryIdAndStatusWithAvgRating(categoryId, status, pageable);

        // Chuyển đổi kết quả từ Object[] thành Page<ProductDTO>
        Page<ProductDTO> productDTOPage = results.map(result -> {
            Product product = (Product) result[0];  // Lấy đối tượng Product
            Double averageRating = (Double) result[1];  // Lấy giá trị averageRating
            Long totalSold = (Long) result[2];  // Giả sử tổng bán được là Long

            // Tạo DTO và gán giá trị
            return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getStatus(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0, // Làm tròn averageRating
                totalSold.intValue()
            );
        });

        // Trả về Page<ProductDTO>
        return productDTOPage;
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
    
    @Override
    public List<Product> getBestSellingProducts() {
        return productRepository.findBestSellingProducts();
    }

}
