package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Tìm sản phẩm theo categoryId và status, với phân trang
	/*
	 * Page<Product> findByCategoryIdAndStatus(Long categoryId, int status, Pageable
	 * pageable);
	 */
<<<<<<< HEAD
	 Page<Product> findByProductStatus(int productStatus, Pageable pageable);
=======
    Page<Product> findAll(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.productStatus = :status")
    Page<Product> findAllByProductStatus(@Param("status") int status, Pageable pageable);
    
>>>>>>> remotes/origin/Nhanh_Cua_Anh_New2
    // Các truy vấn khác như: new arrivals, best sellers, etc.
	/*
	 * @Query("SELECT p, AVG(r.rating) AS averageRating, COALESCE(SUM(oi.quantity), 0) AS totalSold "
	 * + "FROM Product p " + "LEFT JOIN p.reviews r " + "LEFT JOIN p.orderItems oi "
	 * + "WHERE p.category.id = :categoryId AND p.status = :status " +
	 * "GROUP BY p.id")
	 */
    @Query("SELECT p, " +
    	       "(SELECT AVG(r.rating) FROM p.reviews r WHERE r.product.id = p.id) AS averageRating, " +
    	       "(SELECT COALESCE(SUM(oi.quantity), 0) FROM p.orderItems oi WHERE oi.product.id = p.id) AS totalSold " +
    	       "FROM Product p " +
    	       "WHERE p.category.id = :categoryId " +
    	       "AND p.status = :status")
     Page<Object[]> findByCategoryIdAndStatusWithAvgRating(Long categoryId, int status, Pageable pageable);
     
     @Query("SELECT p FROM Product p " +
    	       "JOIN p.orderItems oi ON oi.product.id = p.id " +
    	       "GROUP BY p.id ORDER BY SUM(oi.quantity) DESC")
    	List<Product> findBestSellingProducts();
     @Query("SELECT p, " +
    	       "(SELECT AVG(r.rating) FROM p.reviews r WHERE r.product.id = p.id) AS averageRating, " +
    	       "(SELECT COALESCE(SUM(oi.quantity), 0) FROM p.orderItems oi WHERE oi.product.id = p.id) AS totalSold " +
    	       "FROM Product p " +
    	       "WHERE p.category.id = :categoryId " +
    	       "AND p.status = :status " +
    	       "AND p.name LIKE %:keyword%")
    	Page<Object[]> findByCategoryIdAndStatusWithAvgRatingAndKeyword(Long categoryId, String keyword, int status, Pageable pageable);

    	
    	
}