package vn.iotstar.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;

public interface ProductService {

	Page<ProductDTO> getProductsByCategory(Long categoryId, int status, Pageable pageable);

	List<Category> getAllCategories();

	Page<Product> getProducts(int pageNumber);

	Product getProductById(Long productId);

	Product save(Product product);

	Product updateProduct(Long productId, Product productDetails);

	void deleteProduct(Long productId);

	List<Product> getBestSellingProducts();

	Page<ProductDTO> searchProductsByCategory(Long categoryId, String keyword, int status, Pageable pageable);

}
