package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.entity.Notification;
public interface NotificationRepository extends JpaRepository<Notification, Long> 
{
	 Long  countByIsReadFalseAndStatusIn(List<String> statuses);
	 
	 List<Notification> findByStatusIn(List<String> statuses);
}
