package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductLike;
import vn.iotstar.entity.User;

public interface ProductLikeRepository extends MongoRepository<ProductLike, String>{
	 ProductLike findByProductAndUser(Product product, User user);
	 long countByProduct_Id(String productId);
}
