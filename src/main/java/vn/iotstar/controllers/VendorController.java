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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import utils.ApiResponse;
import vn.iotstar.dto.ProductDTOService;
import vn.iotstar.dto.ProductDTO_2;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.services.CategoryService;
import vn.iotstar.services.ProductService;

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

    @GetMapping("/manage_products")
    public String getProducts(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Product> productPage = productService.getProducts(page);
        List<Product> products = productPage.getContent();
        model.addAttribute("products", products);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "manageProduct";
    }

    @PostMapping("/add-products")
    public ResponseEntity<?> addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("quantity") int quantity,
            @RequestParam("status") int status,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("category") Long categoryId ) 
    		
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
    public ResponseEntity<List<Category>> getCategories() 
    {
        // Lấy danh sách các categories từ service
        List<Category> categories = categoryService.getAllCategories();
        
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
}
