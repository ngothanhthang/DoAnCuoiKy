package vn.iotstar.services;

import org.springframework.data.domain.Page;
import vn.iotstar.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    
    Optional<Category> getCategoryById(String id); // Đã chuyển từ Long sang String
    
    Page<Category> getCategoriesPage(int page, int size);
    
    Page<Category> getCategories(int pageNumber);
    
    Category saveCategory(Category category);
    
    void deleteCategoryById(String id); // Đã chuyển từ Long sang String
    
    // Thêm các phương thức mới
    Category findByName(String name);
    
    List<Category> findByStatus(boolean status);
}
