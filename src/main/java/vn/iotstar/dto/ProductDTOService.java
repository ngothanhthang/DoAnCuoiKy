package vn.iotstar.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;

@Service
public class ProductDTOService {
    @Autowired
    ProductRepository productRepository;
    
    public ProductDTO_2 getProductById(String productId) {  // Thay đổi từ Long sang String
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Chuyển đổi từ Entity sang DTO
        ProductDTO_2 productDTO = new ProductDTO_2();
        productDTO.setId(product.getId());  // Giả sử ProductDTO_2 đã được cập nhật để sử dụng String cho id
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setImageUrl(product.getImageUrl());
        
        // Xử lý an toàn cho category
        if (product.getCategory() != null) {
            productDTO.setCategoryName(product.getCategory().getName());
            productDTO.setCategoryId(product.getCategory().getId());  // Giả sử đã cập nhật để sử dụng String
        }
        
        productDTO.setStatus(product.getStatus());
        productDTO.setDescription(product.getDescription());
        return productDTO;
    }
    
    // Thêm phương thức mới để hỗ trợ tìm kiếm bằng Long ID (cho tương thích ngược)
    public ProductDTO_2 getProductByLongId(Long productId) {
        // Chuyển Long ID thành String và gọi phương thức chính
        return getProductById(productId.toString());
    }
}


/*package vn.iotstar.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;
@Service
public class ProductDTOService {
	@Autowired
	ProductRepository productRepository;
	public ProductDTO_2 getProductById(Long productId) {
	    Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
	    // Chuyển đổi từ Entity sang DTO
	    ProductDTO_2 productDTO = new ProductDTO_2();
	    productDTO.setId(product.getId());
	    productDTO.setName(product.getName());
	    productDTO.setPrice(product.getPrice());
	    productDTO.setQuantity(product.getQuantity());
	    productDTO.setImageUrl(product.getImageUrl());
	    productDTO.setCategoryName(product.getCategory().getName());
	    productDTO.setCategoryId(product.getCategory().getId());
	    productDTO.setStatus(product.getStatus());
	    productDTO.setDescription(product.getDescription());
	    return productDTO;
	}
}*/