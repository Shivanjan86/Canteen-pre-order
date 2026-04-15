package com.pesu.canteen.service.impl;

import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.model.entity.User;
import com.pesu.canteen.model.entity.MenuItem;
import com.pesu.canteen.pattern.builder.OrderBuilder;
import com.pesu.canteen.repository.OrderRepository;
import com.pesu.canteen.repository.UserRepository;
import com.pesu.canteen.repository.MenuRepository;
import com.pesu.canteen.service.interfaces.NotificationService;
import com.pesu.canteen.service.interfaces.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Order placeOrder(int customerId, List<Integer> menuItemIds, String pickupSlot) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));

        List<MenuItem> items = menuRepository.findAllById(menuItemIds);
        if (items.isEmpty()) {
            throw new RuntimeException("No valid menu items selected!");
        }

        Order newOrder = new OrderBuilder()
                .customer(customer)
                .items(items)
                .status("PLACED")
            .pickupSlot(pickupSlot)
                .orderTime(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(newOrder);
        notificationService.createNotification(customer.getId(), "Order #" + savedOrder.getId() + " has been placed.");
        return savedOrder;
    }

    @Override
    public Order cancelOrder(int orderId, int userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        if (order.getStatus().equals("PREPARING") || order.getStatus().equals("READY")) {
            throw new RuntimeException("Cannot cancel! Your food is already being prepared or is ready.");
        }

        order.setStatus("CANCELLED");
        Order cancelledOrder = orderRepository.save(order);
        notificationService.createNotification(order.getCustomer().getId(), "Order #" + order.getId() + " has been cancelled.");
        return cancelledOrder;
    }

    @Override
    public Order updateOrderStatus(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        order.setStatus(newStatus.toUpperCase());
        Order updatedOrder = orderRepository.save(order);
        notificationService.createNotification(order.getCustomer().getId(), "Order #" + order.getId() + " is now " + order.getStatus() + ".");
        return updatedOrder;
    }

    @Override
    public List<Order> getCustomerOrders(int customerId) {
        return orderRepository.findByCustomer_Id(customerId);
    }
}