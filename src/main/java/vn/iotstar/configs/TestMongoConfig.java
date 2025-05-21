//package vn.iotstar.configs;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
//import com.mongodb.client.MongoClients;
//import org.springframework.boot.CommandLineRunner;
//
//@Configuration
//public class TestMongoConfig {
//
//    @Value("${spring.data.mongodb.host:localhost}")
//    private String host;
//    
//    @Value("${spring.data.mongodb.port:27017}")
//    private String port;
//    
//    @Value("${spring.data.mongodb.database:doancuoiky}")
//    private String database;
//
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(
//            new SimpleMongoClientDatabaseFactory(
//                MongoClients.create(String.format("mongodb://%s:%s", host, port)), 
//                database
//            )
//        );
//    }
//    
//    @Bean
//    public CommandLineRunner testMongoConnection(MongoTemplate mongoTemplate) {
//        return args -> {
//            try {
//                // Thử lấy danh sách collections để kiểm tra kết nối
//                System.out.println("MongoDB connection test:");
//                System.out.println("------------------------------------");
//                mongoTemplate.getCollectionNames().forEach(System.out::println);
//                System.out.println("------------------------------------");
//                System.out.println("MongoDB connection successful!");
//            } catch (Exception e) {
//                System.err.println("MongoDB connection failed: " + e.getMessage());
//                e.printStackTrace();
//            }
//        };
//    }
//}