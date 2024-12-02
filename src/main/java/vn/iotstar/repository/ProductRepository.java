package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Truy vấn các sản phẩm theo danh mục, ví dụ:
    List<Product> findByCategoryIdAndStatus(Long categoryId, int status, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
    // Các truy vấn khác như: new arrivals, best sellers, etc.
}
