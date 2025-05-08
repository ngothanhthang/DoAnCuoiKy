package vn.iotstar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import vn.iotstar.entity.UserCoupon;

public interface UserCouponRepository extends MongoRepository<UserCoupon, String> {
}