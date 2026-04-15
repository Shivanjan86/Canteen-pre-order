package com.pesu.canteen.service.interfaces;

import com.pesu.canteen.model.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(int userId, String message);
    List<Notification> getUserNotifications(int userId);
    Notification markAsRead(int notificationId, int userId);
}
