//package vn.iotstar.test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//import vn.iotstar.entity.Category;
//import vn.iotstar.repository.CategoryRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootApplication
//@EnableMongoRepositories(basePackages = "vn.iotstar.repository")
//public class MongoRepositoryTest implements CommandLineRunner {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//    
//    // Phương thức main cần phải được định nghĩa chính xác
//    public static void main(String[] args) {
//        SpringApplication.run(MongoRepositoryTest.class, args);
//    }
//    
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("===== TEST MONGODB REPOSITORY =====");
//        
//        // Xóa tất cả dữ liệu trước khi test
//        categoryRepository.deleteAll();
//        System.out.println("Đã xóa tất cả dữ liệu cũ");
//        
//        // Tạo mới các danh mục
//        Category category1 = new Category();
//        category1.setName("Đồ uống123");
//        category1.setImages("drinks.jpg");
//        category1.setStatus(true);
//        category1.setDescription("Các loại đồ uống");
//        
//        Category category2 = new Category();
//        category2.setName("Món chính");
//        category2.setImages("main.jpg");
//        category2.setStatus(true);
//        category2.setDescription("Các món ăn chính");
//        
//        Category category3 = new Category();
//        category3.setName("Tráng miệng");
//        category3.setImages("dessert.jpg");
//        category3.setStatus(false);
//        category3.setDescription("Các món tráng miệng");
//        
//        // Lưu các danh mục
//        category1 = categoryRepository.save(category1);
//        category2 = categoryRepository.save(category2);
//        category3 = categoryRepository.save(category3);
//        
//        System.out.println("Đã tạo 3 danh mục mới");
//        
//        // Test findAll
//        System.out.println("\n----- findAll() -----");
//        List<Category> allCategories = categoryRepository.findAll();
//        allCategories.forEach(cat -> System.out.println(cat));
//        
//        // Test findById
//        System.out.println("\n----- findById() -----");
//        Optional<Category> foundCategory = categoryRepository.findById(category1.getId());
//        foundCategory.ifPresent(cat -> System.out.println("Tìm thấy: " + cat));
//        
//        // Test findByName
//        System.out.println("\n----- findByName() -----");
//        Category category = categoryRepository.findByName("Món chính");
//        System.out.println("Tìm theo tên: " + category);
//        
//        // Test findByStatus
//        System.out.println("\n----- findByStatus() -----");
//        List<Category> activeCategories = categoryRepository.findByStatus(true);
//        System.out.println("Danh mục hoạt động:");
//        activeCategories.forEach(cat -> System.out.println(cat));
//        
//        // Cập nhật danh mục
//        System.out.println("\n----- update -----");
//        category1.setName("Đồ uống cập nhật123");
//        categoryRepository.save(category1);
//        foundCategory = categoryRepository.findById(category1.getId());
//        foundCategory.ifPresent(cat -> System.out.println("Sau khi cập nhật: " + cat));
//        
//        System.out.println("\nTest MongoDB Repository hoàn thành!");
//        System.exit(0);
//    }
//}