package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
	Role findByName(String name);
}
