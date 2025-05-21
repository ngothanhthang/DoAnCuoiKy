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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    @Override
    public Page<ProductDTO> getProductsByCategory(String categoryId, int status, Pageable pageable) {
        // Tạo ObjectId từ categoryId
        ObjectId categoryObjectId;
        try {
            categoryObjectId = new ObjectId(categoryId);
        } catch (Exception e) {
            System.out.println("Invalid categoryId format: " + e.getMessage());
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        
        // Tạo criteria
        Criteria criteria = new Criteria();
        List<Criteria> conditions = new ArrayList<>();
        
        // Thêm điều kiện category
        conditions.add(Criteria.where("category.$id").is(categoryObjectId));
        
        // Thêm điều kiện status nếu khác -1 (giả sử -1 là "tất cả status")
        if (status != -1) {
            conditions.add(Criteria.where("status").is(status));
        }
        
        // Luôn thêm điều kiện product_status
        conditions.add(Criteria.where("product_status").is(1));
        
        // Kết hợp tất cả điều kiện
        criteria.andOperator(conditions.toArray(new Criteria[0]));
        
        // Tạo match stage với criteria đã xây dựng
        MatchOperation matchStage = Aggregation.match(criteria);
        
        // Các stage lookup giữ nguyên
        LookupOperation lookupReviews = Aggregation.lookup("reviews", "_id", "product.$id", "reviewsData");
        LookupOperation lookupOrderItems = Aggregation.lookup("orderItems", "_id", "product.$id", "orderItemsData");
        
        // Projection stage
        ProjectionOperation projectStage = Aggregation.project()
            .and("_id").as("id")
            .and("product_name").as("name")
            .and("status").as("status")
            .and("description").as("description")
            .and("price").as("price")
            .and("quantity").as("quantity")
            .and("image_url").as("imageUrl")
            .and("category.$id").as("categoryId")
            .and("category.name").as("categoryName")
            .and("created_at").as("createdAt")
            .andExpression("{ $avg: '$reviewsData.rating' }").as("averageRating")
            .andExpression("{ $sum: '$orderItemsData.quantity' }").as("totalSold");
        
        // Tạo aggregation pipeline
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(matchStage);
        operations.add(lookupReviews);
        operations.add(lookupOrderItems);
        operations.add(projectStage);
        
        // Thêm sorting nếu có
        if (pageable.getSort().isSorted()) {
            SortOperation sortStage = Aggregation.sort(pageable.getSort());
            operations.add(sortStage);
        }
        
        // Thêm phân trang
        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));
        
        Aggregation aggregation = Aggregation.newAggregation(operations);
        
        // Thực hiện aggregation và ghi log kết quả
        AggregationResults<Document> results = mongoTemplate.aggregate(
            aggregation, "products", Document.class
        );
                
        // Đếm tổng số sản phẩm với cùng criteria
        long total = mongoTemplate.count(Query.query(criteria), "products");
        System.out.println("Total count: " + total);
        
        // Map kết quả
        List<ProductDTO> productDTOs = results.getMappedResults().stream()
            .map(doc -> {
                ProductDTO dto = new ProductDTO();
                
                // Xử lý ID
                Object idObj = doc.get("id");
                if (idObj instanceof ObjectId) {
                    dto.setId(((ObjectId) idObj).toHexString());
                } else if (idObj instanceof String) {
                    dto.setId((String) idObj);
                } else {
                    dto.setId(idObj != null ? idObj.toString() : null);
                }
                
                // Các trường khác giữ nguyên như phiên bản trước
                dto.setName(doc.getString("name"));
                dto.setStatus(doc.getInteger("status", 0));
                dto.setDescription(doc.getString("description"));
                
             // Xử lý price là int32
                Object priceObj = doc.get("price");
                if (priceObj != null) {
                    if (priceObj instanceof Integer) {
                        // Chuyển đổi trực tiếp từ Integer sang BigDecimal
                        dto.setPrice(BigDecimal.valueOf((Integer) priceObj));
                    } else if (priceObj instanceof Long) {
                        // Nếu là Long
                        dto.setPrice(BigDecimal.valueOf((Long) priceObj));
                    } else {
                        // Các trường hợp khác, chuyển sang BigDecimal
                        dto.setPrice(new BigDecimal(priceObj.toString()));
                    }
                } else {
                    // Nếu giá trị price là null
                    dto.setPrice(BigDecimal.ZERO);
                }
                
                dto.setQuantity(doc.getInteger("quantity", 0));
                dto.setImageUrl(doc.getString("imageUrl"));
                
                // Xử lý categoryId
                Object categoryIdObj = doc.get("categoryId");
                if (categoryIdObj instanceof ObjectId) {
                    dto.setCategoryId(((ObjectId) categoryIdObj).toHexString());
                } else if (categoryIdObj instanceof String) {
                    dto.setCategoryId((String) categoryIdObj);
                } else {
                    dto.setCategoryId(categoryIdObj != null ? categoryIdObj.toString() : null);
                }
                
                dto.setCategoryName(doc.getString("categoryName"));
                
                // Xử lý createdAt
                Object createdAtObj = doc.get("createdAt");
                if (createdAtObj != null) {
                    if (createdAtObj instanceof LocalDateTime) {
                        dto.setCreatedAt((LocalDateTime) createdAtObj);
                    } else if (createdAtObj instanceof Date) {
                        dto.setCreatedAt(((Date) createdAtObj).toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                    }
                }
                
                Double averageRating = doc.getDouble("averageRating");
                dto.setAverageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
                
                dto.setTotalSold(doc.getInteger("totalSold", 0));
                
                return dto;
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(productDTOs, pageable, total);
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
        System.out.println("Searching with categoryId=" + categoryId + ", keyword=" + keyword + ", status=" + status);
        
        // Tạo ObjectId từ categoryId nếu cần
        ObjectId categoryObjectId;
        try {
            categoryObjectId = new ObjectId(categoryId);
        } catch (Exception e) {
            System.out.println("Invalid categoryId format: " + e.getMessage());
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        
        // Tạo criteria phù hợp với cấu trúc thực tế
        Criteria criteria = new Criteria();
        List<Criteria> conditions = new ArrayList<>();
        
        // Thêm điều kiện category
        conditions.add(Criteria.where("category.$id").is(categoryObjectId));
        
        // Thêm điều kiện status nếu khác -1 (giả sử -1 là "tất cả status")
        if (status != -1) {
            conditions.add(Criteria.where("status").is(status));
        }
        
        // Luôn thêm điều kiện product_status
        conditions.add(Criteria.where("product_status").is(1));
        
        // Thêm điều kiện keyword nếu không rỗng
        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add(Criteria.where("product_name").regex(keyword, "i"));
        }
        
        // Kết hợp tất cả điều kiện
        criteria.andOperator(conditions.toArray(new Criteria[0]));
        
        // Tạo match stage với criteria đã xây dựng
        MatchOperation matchStage = Aggregation.match(criteria);
        
        // Các stage khác giữ nguyên
        LookupOperation lookupReviews = Aggregation.lookup("reviews", "_id", "product.$id", "reviewsData");
        LookupOperation lookupOrderItems = Aggregation.lookup("orderItems", "_id", "product.$id", "orderItemsData");
        
        ProjectionOperation projectStage = Aggregation.project()
            .and("_id").as("id")
            .and("product_name").as("name")
            .and("status").as("status")
            .and("description").as("description")
            .and("price").as("price")
            .and("quantity").as("quantity")
            .and("image_url").as("imageUrl")
            .and("category.$id").as("categoryId")
            .and("category.name").as("categoryName")
            .and("created_at").as("createdAt")
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
        
        // Thực hiện aggregation và ghi log kết quả
        AggregationResults<Document> results = mongoTemplate.aggregate(
            aggregation, "products", Document.class
        );
                
        // Đếm tổng số sản phẩm với cùng criteria
        long total = mongoTemplate.count(Query.query(criteria), "products");
        System.out.println("Total count: " + total);
        
        // Map kết quả như trước
        List<ProductDTO> productDTOs = results.getMappedResults().stream()
                .map(doc -> {
                    // In ra document để debug
                    System.out.println("Document: " + doc);
                    
                    ProductDTO dto = new ProductDTO();
                    
                    // Sửa lỗi ClassCastException - Xử lý id đúng cách
                    Object idObj = doc.get("id");
                    if (idObj instanceof ObjectId) {
                        dto.setId(((ObjectId) idObj).toHexString());
                    } else if (idObj instanceof String) {
                        dto.setId((String) idObj);
                    } else {
                        dto.setId(idObj != null ? idObj.toString() : null);
                    }
                    
                    dto.setName(doc.getString("name"));
                    dto.setStatus(doc.getInteger("status", 0));
                    dto.setDescription(doc.getString("description"));
                    
                 // Xử lý price là int32
                    Object priceObj = doc.get("price");
                    if (priceObj != null) {
                        if (priceObj instanceof Integer) {
                            // Chuyển đổi trực tiếp từ Integer sang BigDecimal
                            dto.setPrice(BigDecimal.valueOf((Integer) priceObj));
                        } else if (priceObj instanceof Long) {
                            // Nếu là Long
                            dto.setPrice(BigDecimal.valueOf((Long) priceObj));
                        } else {
                            // Các trường hợp khác, chuyển sang BigDecimal
                            dto.setPrice(new BigDecimal(priceObj.toString()));
                        }
                    } else {
                        // Nếu giá trị price là null
                        dto.setPrice(BigDecimal.ZERO);
                    }

                    
                    dto.setQuantity(doc.getInteger("quantity", 0));
                    dto.setImageUrl(doc.getString("imageUrl"));
                    
                    // Xử lý categoryId tương tự như id
                    Object categoryIdObj = doc.get("categoryId");
                    if (categoryIdObj instanceof ObjectId) {
                        dto.setCategoryId(((ObjectId) categoryIdObj).toHexString());
                    } else if (categoryIdObj instanceof String) {
                        dto.setCategoryId((String) categoryIdObj);
                    } else {
                        dto.setCategoryId(categoryIdObj != null ? categoryIdObj.toString() : null);
                    }
                    
                    dto.setCategoryName(doc.getString("categoryName"));
                    
                    // Xử lý createdAt
                    Object createdAtObj = doc.get("createdAt");
                    if (createdAtObj != null) {
                        if (createdAtObj instanceof LocalDateTime) {
                            dto.setCreatedAt((LocalDateTime) createdAtObj);
                        } else if (createdAtObj instanceof Date) {
                            dto.setCreatedAt(((Date) createdAtObj).toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());
                        }
                    }
                    
                    Double averageRating = doc.getDouble("averageRating");
                    dto.setAverageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
                    
                    dto.setTotalSold(doc.getInteger("totalSold", 0));
                    
                    return dto;
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