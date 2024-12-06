package vn.iotstar.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.dto.CategoryDTOService;
import vn.iotstar.dto.CategoryDTO_2;
import vn.iotstar.dto.ProductDTOService;
import vn.iotstar.dto.ProductDTO_2;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.services.CategoryService;
import vn.iotstar.services.ProductService;
import vn.iotstar.utils.ApiResponse;

@Controller
@RequestMapping("/vendor")
public class VendorController {

    @Value("${product.image.upload.dir}")
    private String uploadDir;

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductDTOService productDTOService;
    @Autowired
    private CategoryDTOService categoryDTO_2Service;
    @GetMapping
    public String Vendorpage()
    {
    	return "VendorPage";
    }
    @GetMapping("/manage_products")
    public String getProducts(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Product> productPage = productService.getProducts(page);  // Lấy danh sách sản phẩm cho trang
        List<Product> products = productPage.getContent();  // Lấy các sản phẩm trong trang hiện tại
        model.addAttribute("products", products);  // Thêm danh sách sản phẩm vào mô hình
        model.addAttribute("totalPages", productPage.getTotalPages());  // Thêm tổng số trang vào mô hình
        model.addAttribute("currentPage", page);  // Thêm trang hiện tại vào mô hình
        return "manageProduct";  // Trả về view manageProduct (tên của trang HTML)
    }

    @PostMapping("/add-products")
    public ResponseEntity<?> addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("quantity") int quantity,
            @RequestParam("status") int status,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("category") Long categoryId,
            @RequestParam("description") String description) 
    		
    {
        try 
        {
            // Kiểm tra và tạo thư mục nếu không tồn tại
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();  // Tạo thư mục nếu chưa tồn tại
            }

            // Tạo tên file duy nhất
            String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, uniqueFileName);

            // Lưu ảnh vào thư mục
            imageFile.transferTo(filePath.toFile());  // Lưu file vào thư mục
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found")); // Lỗi nếu không tìm thấy category
            // Lưu thông tin sản phẩm vào cơ sở dữ liệu
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setStatus(status);
            product.setCategory(category);
            product.setDescription(description); 

            // Lưu URL ảnh (tương đối) vào DB, ví dụ: /images/abc.jpg
            product.setImageUrl("/images/" + uniqueFileName);
            productService.save(product);

            return ResponseEntity.ok(new ApiResponse(true, "Sản phẩm đã được thêm thành công!", product));

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi lưu sản phẩm: " + e.getMessage());
        }
    }
    
    @GetMapping("/show-categories")
    public ResponseEntity<List<CategoryDTO_2>> getCategories() 
    {
        // Lấy danh sách các categories từ service
        List<CategoryDTO_2> categories = categoryDTO_2Service.getAllCategories();
        
        // Kiểm tra nếu không có category nào
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();  // Trả về 204 No Content nếu danh sách rỗng
        }
        
        // Trả về danh sách categories với mã trạng thái 200 OK
        return ResponseEntity.ok(categories);  // Trả về ResponseEntity với HTTP status 200
    }
    @GetMapping("/get-product/{productId}")
    public ResponseEntity<ProductDTO_2> getProductById(@PathVariable String productId) 
    {
    	Long productLongId = Long.parseLong(productId);
        ProductDTO_2 product = productDTOService.getProductById(productLongId);
        //Category abc =product.getCategory();
        if (product != null)
        {
            return ResponseEntity.ok(product);
        } 
        else 
        {
            return ResponseEntity.notFound().build();
    	  //return ResponseEntity.ok("Product ID received: " + productId);
        }
    }
    @PostMapping("/update-products/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String productId,  // productId là String
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("quantity") int quantity,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam("category") Long categoryId,
            @RequestParam("description") String description) {
        try {
            // Chuyển productId từ String sang Long
            Long productLongId = Long.parseLong(productId);  // Chuyển đổi String sang Long

            // Tìm sản phẩm theo ID
            Product existingProduct = productService.getProductById(productLongId);
            // Tìm category theo ID
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Cập nhật các thông tin khác của sản phẩm
            existingProduct.setName(name);
            existingProduct.setPrice(price);
            existingProduct.setQuantity(quantity);
            existingProduct.setStatus(status);
            existingProduct.setCategory(category);
            existingProduct.setDescription(description);

            // Nếu có ảnh mới, xử lý ảnh và lưu vào thư mục
            if (imageFile != null && !imageFile.isEmpty()) {
                // Xóa ảnh cũ nếu có (có thể tùy vào yêu cầu bạn cần xóa ảnh cũ hay không)
                String oldImagePath = existingProduct.getImageUrl();
                if (oldImagePath != null) {
                    File oldImageFile = new File(uploadDir + oldImagePath.replace("/images/", ""));
                    if (oldImageFile.exists()) {
                        oldImageFile.delete(); // Xóa ảnh cũ
                    }
                }

                // Tạo tên file duy nhất cho ảnh mới
                String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, uniqueFileName);

                // Lưu ảnh mới vào thư mục
                imageFile.transferTo(filePath.toFile());

                // Cập nhật URL ảnh mới cho sản phẩm
                existingProduct.setImageUrl("/images/" + uniqueFileName);
            }

            // Lưu sản phẩm đã cập nhật vào cơ sở dữ liệu
            productService.save(existingProduct);

            // Trả về phản hồi thành công
            return ResponseEntity.ok(new ApiResponse(true, "Sản phẩm đã được cập nhật thành công!", existingProduct));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Lỗi khi lưu sản phẩm: " + e.getMessage(), null));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, "ID sản phẩm không hợp lệ", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(new ApiResponse(false, "Lỗi không xác định: " + e.getMessage(), null));
        }
    }
    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable String productId) {
        try {
        	 Long productLongId = Long.parseLong(productId);  
            // Gọi service để xóa sản phẩm
            productService.deleteProduct(productLongId);
            
            // Tạo thông báo thành công
            ApiResponse response = new ApiResponse(true, "Sản phẩm đã được xóa thành công!", null);
            return ResponseEntity.ok(response);  // Trả về mã trạng thái 200 OK với phản hồi

        } catch (RuntimeException e) {
            // Trường hợp sản phẩm không tồn tại
            ApiResponse response = new ApiResponse(false, e.getMessage(), null);
            return ResponseEntity.status(404).body(response);  // Trả về mã trạng thái 404 Not Found
        } catch (Exception e) {
            // Trường hợp có lỗi ngoài dự kiến
            ApiResponse response = new ApiResponse(false, "Lỗi không xác định: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);  // Trả về mã trạng thái 500 Internal Server Error
        }
    }
   
}
