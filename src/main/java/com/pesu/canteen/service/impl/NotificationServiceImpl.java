package com.pesu.canteen.service.impl;

import com.pesu.canteen.model.entity.Notification;
import com.pesu.canteen.model.entity.User;
import com.pesu.canteen.repository.NotificationRepository;
import com.pesu.canteen.repository.UserRepository;
import com.pesu.canteen.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Notification createNotification(int userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setReadStatus(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(int userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Notification markAsRead(int notificationId, int userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.getUser().getId() != userId) {
            throw new RuntimeException("Not allowed to modify this notification");
        }

        notification.setReadStatus(true);
        return notificationRepository.save(notification);
    }
}
