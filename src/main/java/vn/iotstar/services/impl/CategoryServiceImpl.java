package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.services.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy tất cả danh mục
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy danh mục theo ID - Đã chuyển từ Long sang String
    @Override
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);  // Tự động trả về Optional<Category>
    }

    // Lấy danh sách danh mục với phân trang
    @Override
    public Page<Category> getCategoriesPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryRepository.findAll(pageRequest);
    }

    // Phân trang sản phẩm, giới hạn 5 sản phẩm mỗi trang
    @Override
    public Page<Category> getCategories(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 5);  // 5 sản phẩm mỗi trang
        return categoryRepository.findAll(pageable);
    }
    
    // Lưu danh mục mới
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Xóa danh mục theo ID - Đã chuyển từ Long sang String
    @Override
    public void deleteCategoryById(String id) {
        categoryRepository.deleteById(id);
    }
    
    // Thêm các phương thức mới dựa trên repository MongoDB
    
    // Tìm danh mục theo tên
    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    // Tìm danh mục theo trạng thái
    @Override
    public List<Category> findByStatus(boolean status) {
        return categoryRepository.findByStatus(status);
    }
}
