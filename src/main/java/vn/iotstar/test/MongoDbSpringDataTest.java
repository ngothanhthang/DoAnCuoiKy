/*
 * package vn.iotstar.test;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.boot.CommandLineRunner; import
 * org.springframework.boot.SpringApplication; import
 * org.springframework.boot.autoconfigure.SpringBootApplication; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.PropertySource; import
 * org.springframework.data.mongodb.core.MongoTemplate; import
 * org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
 * import org.springframework.data.mongodb.core.query.Criteria; import
 * org.springframework.data.mongodb.core.query.Query; import
 * org.springframework.data.mongodb.core.query.Update;
 * 
 * import com.mongodb.client.MongoClient;
 * 
 * import vn.iotstar.entity.Category;
 * 
 * @SpringBootApplication
 * 
 * @PropertySource("classpath:application.properties") // Đảm bảo đọc file cấu
 * hình public class MongoDbSpringDataTest {
 * 
 * @Autowired private MongoClient mongoClient;
 * 
 * public static void main(String[] args) {
 * SpringApplication.run(MongoDbSpringDataTest.class, args); }
 * 
 * @Bean public CommandLineRunner commandLineRunner(MongoTemplate
 * defaultTemplate) { return args -> { // Tạo một MongoTemplate mới với database
 * doancuoiky MongoTemplate mongoTemplate = new MongoTemplate( new
 * SimpleMongoClientDatabaseFactory(mongoClient, "doancuoiky") );
 * 
 * System.out.println("Sử dụng database: " + mongoTemplate.getDb().getName());
 * 
 * // Phần còn lại của code không thay đổi // CREATE - Tạo một document mới
 * Category category = new Category();
 * category.setName("Thử nghiệm Spring Data");
 * category.setImages("test_spring.jpg"); category.setStatus(true);
 * category.setDescription("Đây là danh mục thử nghiệm với Spring Data");
 * 
 * Category savedCategory = mongoTemplate.save(category, "categories");
 * System.out.println("Đã tạo danh mục với ID: " + savedCategory.getId());
 * 
 * // READ - Đọc document vừa tạo Category foundCategory =
 * mongoTemplate.findById(savedCategory.getId(), Category.class, "categories");
 * System.out.println("Danh mục đọc được: " + foundCategory);
 * 
 * // UPDATE - Cập nhật document Query query = new
 * Query(Criteria.where("_id").is(savedCategory.getId())); Update update = new
 * Update().set("name", "Đã cập nhật với Spring Data");
 * mongoTemplate.updateFirst(query, update, Category.class, "categories");
 * 
 * // Đọc lại sau khi cập nhật foundCategory =
 * mongoTemplate.findById(savedCategory.getId(), Category.class, "categories");
 * System.out.println("Danh mục sau khi cập nhật: " + foundCategory);
 * 
 * // READ ALL - Liệt kê tất cả các document trong collection
 * System.out.println("\nDanh sách tất cả các danh mục:");
 * mongoTemplate.findAll(Category.class,
 * "categories").forEach(System.out::println);
 * System.out.println("Connecting to database: " +
 * mongoTemplate.getDb().getName());
 * System.out.println("Thử nghiệm CRUD với Spring Data MongoDB thành công!"); };
 * } }
 */