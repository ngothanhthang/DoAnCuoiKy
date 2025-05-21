/*
 * package vn.iotstar.test;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.boot.CommandLineRunner; import
 * org.springframework.boot.SpringApplication; import
 * org.springframework.boot.autoconfigure.SpringBootApplication; import
 * org.springframework.context.annotation.PropertySource; import
 * org.springframework.data.mongodb.core.MongoTemplate; import
 * org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
 * 
 * import com.mongodb.client.MongoClient; import
 * com.mongodb.client.MongoClients;
 * 
 * import vn.iotstar.entity.Category;
 * 
 * @SpringBootApplication
 * 
 * @PropertySource("classpath:application.properties") // Chỉ định file cấu hình
 * public class MongoDataViewer implements CommandLineRunner {
 * 
 * @Autowired private MongoTemplate mongoTemplate;
 * 
 * @Autowired private MongoClient mongoClient;
 * 
 * 
 * public static void main(String[] args) {
 * SpringApplication.run(MongoDataViewer.class, args); }
 * 
 * @Override public void run(String... args) throws Exception { // Tạo
 * MongoTemplate mới với database doancuoiky MongoTemplate doancuoikyTemplate =
 * new MongoTemplate( new SimpleMongoClientDatabaseFactory(mongoClient,
 * "doancuoiky") );
 * 
 * System.out.println("===== HIỂN THỊ DỮ LIỆU MONGODB =====");
 * System.out.println("Database mặc định: " + mongoTemplate.getDb().getName());
 * System.out.println("Database đã chỉ định: " +
 * doancuoikyTemplate.getDb().getName()); System.out.println("Collections: " +
 * doancuoikyTemplate.getCollectionNames());
 * 
 * System.out.println("\n===== DANH SÁCH CATEGORIES ====="); // Sử dụng
 * doancuoikyTemplate thay vì mongoTemplate
 * doancuoikyTemplate.findAll(Category.class, "categories").forEach(category ->
 * { System.out.println("--------------------------------");
 * System.out.println("ID: " + category.getId()); System.out.println("Tên: " +
 * category.getName()); System.out.println("Hình ảnh: " + category.getImages());
 * System.out.println("Trạng thái: " + category.isStatus());
 * System.out.println("Mô tả: " + category.getDescription()); });
 * 
 * System.exit(0); } }
 */