package vn.iotstar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Category;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    // Phương thức tìm theo tên
    Category findByName(String name);
    
    // Phương thức tìm theo status
    java.util.List<Category> findByStatus(boolean status);
}