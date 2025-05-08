package vn.iotstar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication(scanBasePackages = {"vn.iotstar", "configs"})
@EnableJpaRepositories("vn.iotstar.repository")
public class DoAnCuoiKyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoAnCuoiKyApplication.class, args);
	}
	@Bean
    public CommandLineRunner ensureCollectionsExist(MongoTemplate mongoTemplate) {
        return args -> {
            // Đảm bảo các collection tồn tại
            if (!mongoTemplate.collectionExists("categories")) {
                mongoTemplate.createCollection("categories");
                System.out.println("Created collection: categories");
            }
            if (!mongoTemplate.collectionExists("products")) {
                mongoTemplate.createCollection("products");
                System.out.println("Created collection: products");
            }
            if (!mongoTemplate.collectionExists("users")) {
                mongoTemplate.createCollection("users");
                System.out.println("Created collection: users");
            }
            if (!mongoTemplate.collectionExists("orders")) {
                mongoTemplate.createCollection("orders");
                System.out.println("Created collection: orders");
            }
            if (!mongoTemplate.collectionExists("carts")) {
                mongoTemplate.createCollection("carts");
                System.out.println("Created collection: carts");
            }
            // Thêm các collection khác theo nhu cầu của bạn
        };
	}
}