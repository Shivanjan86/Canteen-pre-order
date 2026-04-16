package com.pesu.canteen.service.impl;

import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.model.entity.Admin;
import com.pesu.canteen.model.entity.Staff;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private static final long READY_GRACE_MINUTES = 15;
    private static final DateTimeFormatter SLOT_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h:mm a")
            .toFormatter(Locale.ENGLISH);

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
    public Order cancelOrder(int orderId, String currentUserEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        boolean isPrivilegedUser = currentUser instanceof Staff || currentUser instanceof Admin;
        boolean isOwner = Objects.equals(order.getCustomer().getId(), currentUser.getId());

        if (!isPrivilegedUser && !isOwner) {
            throw new SecurityException("You can only cancel your own order!");
        }

        if (!isPrivilegedUser && !"PLACED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("You can only cancel an order before preparation starts.");
        }

        order.setStatus("CANCELLED");
        Order cancelledOrder = orderRepository.save(order);
        notificationService.createNotification(order.getCustomer().getId(), "Order #" + order.getId() + " has been cancelled.");
        return cancelledOrder;
    }

    @Override
    public Order markOrderReceived(int orderId, String currentUserEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!Objects.equals(order.getCustomer().getId(), currentUser.getId())) {
            throw new SecurityException("You can only mark your own order as received!");
        }

        expireOrderIfPickupWindowMissed(order);

        if (!"READY".equalsIgnoreCase(order.getStatus())) {
            if ("EXPIRED".equalsIgnoreCase(order.getStatus())) {
                throw new IllegalStateException("Pickup window is over. This order has expired.");
            }
            throw new IllegalStateException("Order can only be marked received after it is READY.");
        }

        order.setStatus("PICKED_UP");
        Order pickedUpOrder = orderRepository.save(order);
        notificationService.createNotification(order.getCustomer().getId(), "Order #" + order.getId() + " has been marked as received.");
        return pickedUpOrder;
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
        List<Order> orders = orderRepository.findByCustomer_Id(customerId);
        orders.forEach(this::expireOrderIfPickupWindowMissed);
        return orders;
    }

    private void expireOrderIfPickupWindowMissed(Order order) {
        if (!"READY".equalsIgnoreCase(order.getStatus())) {
            return;
        }

        LocalDateTime slotDeadline = resolvePickupDeadline(order);
        if (slotDeadline == null || LocalDateTime.now().isBefore(slotDeadline)) {
            return;
        }

        order.setStatus("EXPIRED");
        orderRepository.save(order);
        notificationService.createNotification(
                order.getCustomer().getId(),
                "Order #" + order.getId() + " expired because pickup window was missed."
        );
    }

    private LocalDateTime resolvePickupDeadline(Order order) {
        String pickupSlot = order.getPickupSlot();
        if (pickupSlot == null || !pickupSlot.contains("-")) {
            return null;
        }

        String[] slotParts = pickupSlot.split("-");
        if (slotParts.length < 2) {
            return null;
        }

        try {
            String endPart = slotParts[1].trim();
            LocalTime slotEnd = LocalTime.parse(endPart, SLOT_TIME_FORMATTER);
            LocalDate orderDate = order.getOrderTime() != null ? order.getOrderTime().toLocalDate() : LocalDate.now();
            return LocalDateTime.of(orderDate, slotEnd).plusMinutes(READY_GRACE_MINUTES);
        } catch (RuntimeException ex) {
            return null;
        }
    }
}