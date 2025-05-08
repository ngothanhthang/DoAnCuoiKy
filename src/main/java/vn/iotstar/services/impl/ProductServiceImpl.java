package vn.iotstar.services.impl;

import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.services.ProductService;

import java.util.ArrayList;
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
    
    @Autowired
    private MongoTemplate mongoTemplate;

    // Lấy sản phẩm theo danh mục, status và phân trang
    public Page<ProductDTO> getProductsByCategory(String categoryId, int status, Pageable pageable) {
        // Tạo các stage cho aggregation pipeline
        MatchOperation matchStage = Aggregation.match(
            Criteria.where("category.$id").is(new ObjectId(categoryId))
                .and("status").is(status)
                .and("product_status").is(1)  // Sửa thành product_status và giá trị 0
        );
        
        // Sắp xếp theo thứ tự được chỉ định trong pageable
        SortOperation sortStage = null;
        if (pageable.getSort().isSorted()) {
            List<AggregationOperation> sortOperations = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                Direction direction = order.getDirection().isAscending() ? Direction.ASC : Direction.DESC;
                sortOperations.add(Aggregation.sort(direction, order.getProperty()));
            });
            if (!sortOperations.isEmpty()) {
                sortStage = (SortOperation) sortOperations.get(0);
            }
        } else {
            // Mặc định sắp xếp theo thời gian tạo giảm dần nếu không có sắp xếp được chỉ định
            sortStage = Aggregation.sort(Sort.Direction.DESC, "created_at");
        }
        
        // Thêm stage phân trang
        SkipOperation skipStage = Aggregation.skip(pageable.getOffset());
        LimitOperation limitStage = Aggregation.limit(pageable.getPageSize());
        
        // Tạo aggregation pipeline
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(matchStage);
        if (sortStage != null) {
            operations.add(sortStage);
        }
        operations.add(skipStage);
        operations.add(limitStage);
        
        Aggregation aggregation = Aggregation.newAggregation(operations);
        
        // Thực hiện truy vấn
        AggregationResults<ProductDTO> results = mongoTemplate.aggregate(
            aggregation, "products", ProductDTO.class
        );
        
        // Lấy danh sách kết quả
        List<ProductDTO> productList = results.getMappedResults();
        
        // Đếm tổng số sản phẩm thỏa mãn điều kiện (không có phân trang)
        Aggregation countAggregation = Aggregation.newAggregation(matchStage);
        AggregationResults<Document> countResults = mongoTemplate.aggregate(
            countAggregation, "products", Document.class
        );
        long total = countResults.getMappedResults().size();
        
        // Tạo và trả về Page
        return new PageImpl<>(productList, pageable, total);
    }


    // Lấy tất cả các danh mục
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Phân trang sản phẩm, giới hạn 20 sản phẩm mỗi trang
    @Override
    public Page<Product> getProducts(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 6);  // 6 sản phẩm mỗi trang
        return productRepository.findAll(pageable);
    }
    
    @Override
    // Lấy sản phẩm theo ID
    public Product getProductById(String productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.orElse(null);  // Trả về sản phẩm nếu tìm thấy, nếu không trả về null
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String productId, Product productDetails) {
        Product existingProduct = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        
        // Cập nhật thông tin sản phẩm
        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());
        existingProduct.setStatus(productDetails.getStatus());
        existingProduct.setImageUrl(productDetails.getImageUrl());
        existingProduct.setCategory(productDetails.getCategory());

        // Lưu lại thông tin đã cập nhật
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Xóa sản phẩm khỏi cơ sở dữ liệu
        productRepository.delete(product);
    }
    
    @Override
    public List<Product> getBestSellingProducts() {
        // Sử dụng MongoTemplate và Aggregation để tìm sản phẩm bán chạy nhất
        MatchOperation matchStage = Aggregation.match(
            Criteria.where("productStatus").is(1)
        );
        
        LookupOperation lookupOrderItems = Aggregation.lookup(
            "orderItems", "_id", "product.$id", "orderItemsData"
        );
        
        ProjectionOperation projectStage = Aggregation.project()
            .andInclude("_id", "name", "status", "description", "price", "quantity", 
                        "imageUrl", "category", "createdAt", "productStatus")
            .and("orderItemsData").as("orderItemsData")
            .andExpression("{ $sum: '$orderItemsData.quantity' }").as("totalSold");
        
        SortOperation sortStage = Aggregation.sort(Sort.Direction.DESC, "totalSold");
        
        Aggregation aggregation = Aggregation.newAggregation(
            matchStage,
            lookupOrderItems,
            projectStage,
            sortStage
        );
        
        AggregationResults<Product> results = mongoTemplate.aggregate(
            aggregation, "products", Product.class
        );
        
        return results.getMappedResults();
    }
    
    @Override
    public Page<ProductDTO> searchProductsByCategory(String categoryId, String keyword, int status, Pageable pageable) {
        // Tương tự như getProductsByCategory nhưng thêm điều kiện tìm kiếm theo keyword
        MatchOperation matchStage = Aggregation.match(
            Criteria.where("category.$id").is(categoryId)
                .and("status").is(status)
                .and("productStatus").is(1)
                .and("name").regex(keyword, "i")
        );
        
        // Các stage còn lại tương tự như getProductsByCategory
        LookupOperation lookupReviews = Aggregation.lookup("reviews", "_id", "product.$id", "reviewsData");
        LookupOperation lookupOrderItems = Aggregation.lookup("orderItems", "_id", "product.$id", "orderItemsData");
        
        ProjectionOperation projectStage = Aggregation.project()
            .and("_id").as("id")
            .and("name").as("name")
            .and("status").as("status")
            .and("description").as("description")
            .and("price").as("price")
            .and("quantity").as("quantity")
            .and("imageUrl").as("imageUrl")
            .and("category").as("category")
            .and("createdAt").as("createdAt")
            .and("productStatus").as("productStatus")
            .andExpression("{ $avg: '$reviewsData.rating' }").as("averageRating")
            .andExpression("{ $sum: '$orderItemsData.quantity' }").as("totalSold");
        
        Aggregation aggregation = Aggregation.newAggregation(
            matchStage,
            lookupReviews,
            lookupOrderItems,
            projectStage,
            Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
            Aggregation.limit(pageable.getPageSize())
        );
        
        AggregationResults<Document> results = mongoTemplate.aggregate(
            aggregation, "products", Document.class
        );
        
        long total = mongoTemplate.count(Query.query(
            Criteria.where("category.$id").is(categoryId)
                .and("status").is(status)
                .and("productStatus").is(1)
                .and("name").regex(keyword, "i")
        ), Product.class);
        
        List<ProductDTO> productDTOs = results.getMappedResults().stream()
            .map(doc -> {
                Product product = mongoTemplate.getConverter().read(Product.class, doc);
                Double averageRating = doc.get("averageRating", Double.class);
                Integer totalSold = doc.get("totalSold", Integer.class);
                
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
                    averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0,
                    totalSold != null ? totalSold : 0
                );
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(productDTOs, pageable, total);
    }
    
    @Override
    public Page<Product> getApprovedProducts(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 5);  // 5 sản phẩm mỗi trang
        return productRepository.findByProductStatus(1, pageable);
    }

    @Override
    public Page<Product> getUnapprovedProducts(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 5);  // 5 sản phẩm mỗi trang
        return productRepository.findByProductStatus(0, pageable);
    }

    @Override
    public Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Product> findByIdContaining(String id, Pageable pageable) {
        return productRepository.findById(id, pageable);
    }

    @Override
    public Page<Product> findByProductId(String id, Pageable pageable) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            List<Product> productList = new ArrayList<>();
            productList.add(product.get());
            return new PageImpl<>(productList, pageable, 1);
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }
}


/*package vn.iotstar.services.impl;

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

import java.util.ArrayList;
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
        Pageable pageable = PageRequest.of(pageNumber, 6);  // 5 sản phẩm mỗi trang
        return productRepository.findAll(pageable);
    }
    
    @Override
    // Lấy sản phẩm theo ID
    public Product getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.orElse(null);  // Trả về sản phẩm nếu tìm thấy, nếu không trả về null
    }

	@Override
	public Product save(Product product) {
		// TODO Auto-generated method stub
		 return productRepository.save(product);
	}

	@Override
	public Product updateProduct(Long productId, Product productDetails) {
		// TODO Auto-generated method stub
		  Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
	        
	        // Cập nhật thông tin sản phẩm
	        existingProduct.setName(productDetails.getName());
	        existingProduct.setPrice(productDetails.getPrice());
	        existingProduct.setQuantity(productDetails.getQuantity());
	        existingProduct.setStatus(productDetails.getStatus());
	        existingProduct.setImageUrl(productDetails.getImageUrl());
	        existingProduct.setCategory(productDetails.getCategory());

	        // Lưu lại thông tin đã cập nhật
	        return productRepository.save(existingProduct);
	}

	@Override
	public void deleteProduct(Long productId)
	{
		 Product product = productRepository.findById(productId)
			        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

			    // Xóa sản phẩm khỏi cơ sở dữ liệu
			   productRepository.delete(product);
	}
	
	@Override
    public List<Product> getBestSellingProducts() {
        return productRepository.findBestSellingProducts();
    }
	
	@Override
	public Page<ProductDTO> searchProductsByCategory(Long categoryId, String keyword, int status, Pageable pageable) {
	    // Lấy dữ liệu từ repository với averageRating tính toán từ cơ sở dữ liệu
	    Page<Object[]> results = productRepository.findByCategoryIdAndStatusWithAvgRatingAndKeyword(categoryId, keyword, status, pageable);

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
	            totalSold != null ? totalSold.intValue() : 0 // Chuyển totalSold thành int, xử lý nếu null
	        );
	    });

	    // Trả về Page<ProductDTO>
	    return productDTOPage;
	}
	
	@Override
	public Page<Product> getApprovedProducts(int pageNum) {
		Pageable pageable = PageRequest.of(pageNum, 5);  // 5 sản phẩm mỗi trang
        return productRepository.findAllByProductStatus(1, pageable);
	}

	@Override
	public Page<Product> getUnapprovedProducts(int pageNum) {
		Pageable pageable = PageRequest.of(pageNum, 5);  // 5 sản phẩm mỗi trang
		return productRepository.findAllByProductStatus(0, pageable);
	}

	@Override
	public Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable) {
		return productRepository.findByNameContainingIgnoreCase(name, pageable);
	}

	@Override
	public Page<Product> findByIdContaining(Long id, Pageable pageable) {
		return productRepository.findById(id, pageable);
	}

	@Override
	public Page<Product> findByProductId(Long id, Pageable pageable) 
	{
		Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            List<Product> productList = new ArrayList<>();
            productList.add(product.get());
            return new PageImpl<>(productList, pageable, 1);
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
	}

	
    
}*/