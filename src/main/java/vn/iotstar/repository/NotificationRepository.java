package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.entity.Notification;
public interface NotificationRepository extends JpaRepository<Notification, Long> 
{

}
