package vn.iotstar.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;

public interface ProductService {

	// Thay đổi Long categoryId thành String cho MongoDB ObjectId
	Page<ProductDTO> getProductsByCategory(String categoryId, int status, Pageable pageable);

	List<Category> getAllCategories();

	Page<Product> getProducts(int pageNumber);

	// Thay đổi Long productId thành String cho MongoDB ObjectId
	Product getProductById(String productId);

	Product save(Product product);

	// Thay đổi Long productId thành String cho MongoDB ObjectId
	Product updateProduct(String productId, Product productDetails);

	// Thay đổi Long productId thành String cho MongoDB ObjectId
	void deleteProduct(String productId);

	List<Product> getBestSellingProducts();

	// Thay đổi Long categoryId thành String cho MongoDB ObjectId
	Page<ProductDTO> searchProductsByCategory(String categoryId, String keyword, int status, Pageable pageable);
	
	Page<Product> getApprovedProducts(int pageNum);

	Page<Product> getUnapprovedProducts(int pageNum);
	
	Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
	
    // Thay đổi Long id thành String cho MongoDB ObjectId
    Page<Product> findByIdContaining(String id, Pageable pageable);
    //Page<Product> getProducts(int page);
    
    // Thay đổi Long id thành String cho MongoDB ObjectId
    Page<Product> findByProductId(String id, Pageable pageable);

}
