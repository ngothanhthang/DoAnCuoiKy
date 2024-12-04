package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Tìm sản phẩm theo categoryId và status, với phân trang
	/*
	 * Page<Product> findByCategoryIdAndStatus(Long categoryId, int status, Pageable
	 * pageable);
	 */
    Page<Product> findAll(Pageable pageable);
    // Các truy vấn khác như: new arrivals, best sellers, etc.
    @Query("SELECT p, AVG(r.rating) AS averageRating, COALESCE(SUM(oi.quantity), 0) AS totalSold " +
    	       "FROM Product p " +
    	       "LEFT JOIN p.reviews r " +
    	       "LEFT JOIN p.orderItems oi " +
    	       "WHERE p.category.id = :categoryId AND p.status = :status " +
    	       "GROUP BY p.id")
     Page<Object[]> findByCategoryIdAndStatusWithAvgRating(Long categoryId, int status, Pageable pageable);
}
	