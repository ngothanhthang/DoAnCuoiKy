package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Notification;
import vn.iotstar.repository.NotificationRepository;
import vn.iotstar.services.NotificationService;
@Service
public class NotificationServiceImpl implements NotificationService
{
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Override
	public void save(Notification notification) 
	{
		notificationRepository.save(notification);
	}

	@Override
	public Notification findById(Long id)
	{
		return notificationRepository.findById(id).orElse(null);
	}
}
