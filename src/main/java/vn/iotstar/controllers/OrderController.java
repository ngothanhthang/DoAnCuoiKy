package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.ProductService;
import vn.iotstar.services.ReviewService;
import vn.iotstar.services.UserService;
import vn.iotstar.utils.ApiResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Value("${product.image.upload.dir}")
    private String uploadDir;
	
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;

    // Tạo đơn hàng từ giỏ hàng
    @PostMapping("/create")
    public ResponseEntity<Long> createOrder(@RequestParam("selectedItems") List<Long> selectedItems, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L;
        }

        User user = userService.findById(userId);
        List<CartItem> cartItems = getCartItemsByIds(selectedItems, userId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Tạo đơn hàng
        Order order = orderService.createOrder(user, cartItems);

        // Trả về ID của đơn hàng vừa tạo
        return ResponseEntity.ok(order.getId());
    }


    // Lấy các CartItem từ giỏ hàng dựa trên ids đã chọn
    private List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId) {
        // Cần viết logic để lấy các CartItem theo ids và userId từ cơ sở dữ liệu
        return orderService.getCartItemsByIds(selectedItems, userId);
    }
    
    @PostMapping("/update-status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, Object> request) {
    	final Logger logger = LoggerFactory.getLogger(OrderController.class);
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String status = request.get("status").toString();
        logger.info("Updating orderId: {} with status: {}", orderId, status);
        // Cập nhật trạng thái đơn hàng trong cơ sở dữ liệu
        boolean success = orderService.updateOrderStatus(orderId, status);
        
        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
        }
    }
    
    @PostMapping("/submit-review/{productId}")
    public ResponseEntity<?> submitReview(
        @PathVariable("productId") Long productId,
        @RequestParam("rating") int rating,
        @RequestParam("reviewText") String reviewText,
        @RequestParam(value = "imageFile", required = false) MultipartFile[] imageFiles,
        @RequestParam(value = "videoFile", required = false) MultipartFile[] videoFiles,
        HttpSession session) {
    	System.out.println("File name: " + imageFiles);
        try {
            // Tạo thư mục nếu chưa tồn tại
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }
         // Lấy thông tin người dùng từ session, nếu không có thì gán User mặc định với ID = 1
            User user = (User) session.getAttribute("user");
            if (user == null) {
                // Gán User mặc định nếu không có trong session
                user = userService.findById(1L);  // Lấy User mặc định có ID = 1 từ cơ sở dữ liệu
            }
            // Khởi tạo các biến để lưu URL ảnh và video
            String imageUrl = null;
            String videoUrl = null;

            // Xử lý và lưu ảnh/video
            if (imageFiles != null) {
                for (MultipartFile file : imageFiles) {
                    // Lấy tên file và tạo tên duy nhất cho nó
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDirectory.getPath(), uniqueFileName);

                    // Kiểm tra loại file và xử lý tương ứng
                    String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
                    
                    // Nếu là ảnh (JPEG, PNG, GIF,...)
                    if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif")) {
                        file.transferTo(filePath.toFile());
                        imageUrl = "/images/" + uniqueFileName;  // Lưu URL ảnh
                    }
                    // Nếu là video (MP4, AVI,...)
                    else if (fileExtension.equals("mp4") || fileExtension.equals("avi") || fileExtension.equals("mov")) {
                        file.transferTo(filePath.toFile());
                        videoUrl = "/images/" + uniqueFileName;  // Lưu URL video
                    }
                    // Nếu là file không hợp lệ
                    else {
                        return ResponseEntity.status(400).body(new ApiResponse(false, "Chỉ cho phép tải lên ảnh hoặc video hợp lệ!", null));
                    }
                }
            }
            if (videoFiles != null) {
                for (MultipartFile file : videoFiles) {
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDirectory.getPath(), uniqueFileName);

                    String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();

                    if (fileExtension.equals("mp4") || fileExtension.equals("avi") || fileExtension.equals("mov")) {
                        file.transferTo(filePath.toFile());
                        videoUrl = "/images/" + uniqueFileName;  // Lưu URL video
                    } else {
                        return ResponseEntity.status(400).body(new ApiResponse(false, "Chỉ cho phép tải lên video hợp lệ!", null));
                    }
                }
            }

            Product product = productService.getProductById(productId);

            // Tạo đối tượng Review và gán thông tin
            Review review = new Review();
            review.setProduct(product);  // Gán đối tượng product
            review.setUser(user);        // Gán đối tượng user
            review.setRating(rating);    // Lưu đánh giá sao
            review.setReviewText(reviewText);  // Lưu bình luận
            review.setImageUrl(imageUrl);  // Lưu URL ảnh
            review.setVideoUrl(videoUrl);  // Lưu URL video
            review.setCreatedAt(new Date());  // Đặt thời gian tạo
            reviewService.saveReview(review);  // Lưu vào cơ sở dữ liệu

            return ResponseEntity.ok(new ApiResponse(true, "Đánh giá đã được gửi thành công!", null));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Lỗi khi tải lên media: " + e.getMessage(), null));
        }
    }
}
