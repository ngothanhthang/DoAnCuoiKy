package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.ReturnRequest;

@Repository
public interface ReturnRequestRepository extends MongoRepository<ReturnRequest, String> 
{
    // Xóa ReturnRequest theo orderId
    @DeleteQuery("{'order.$id': ?0}")
    void deleteByOrderId(String orderId);
    
    // Phương thức findAll với phân trang được cung cấp sẵn bởi MongoRepository
    Page<ReturnRequest> findAll(Pageable pageable);
}
