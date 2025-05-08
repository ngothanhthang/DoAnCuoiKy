package vn.iotstar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import vn.iotstar.entity.Coupon;

public interface CouponRepository extends MongoRepository<Coupon, String> {
    Coupon findByCode(String code);
}
