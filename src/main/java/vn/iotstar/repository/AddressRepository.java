package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.iotstar.entity.Address;

import java.util.List;

public interface AddressRepository extends MongoRepository<Address, String> {
    List<Address> findByUserId(String userId);  // Lấy tất cả địa chỉ của user
    Address findByUserIdAndIsDefaultTrue(String userId);  // Lấy địa chỉ mặc định của user
}
