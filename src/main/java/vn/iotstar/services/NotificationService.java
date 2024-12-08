package vn.iotstar.services;

import vn.iotstar.entity.Notification;

public interface NotificationService 
{
	// lưu thông báo:
	void save(Notification notification);
	
	// tìm notification theo ID:
	Notification findById(Long id);
	
}
