package com.pesu.canteen.controller;

import com.pesu.canteen.model.entity.Admin;
import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.model.entity.Staff;
import com.pesu.canteen.model.entity.User;
import com.pesu.canteen.pattern.facade.OrderFacade;
import com.pesu.canteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    // A helper class to grab the incoming JSON data for placing an order
    public static class OrderRequest {
        public int customerId;
        public List<Integer> menuItemIds;
        public String pickupSlot;
    }

    // POST request to /api/orders
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request, Principal principal) {
        try {
            User currentUser = resolveCurrentUser(principal);
            int effectiveCustomerId = request.customerId;
            boolean isPrivileged = currentUser instanceof Staff || currentUser instanceof Admin;

            if (!isPrivileged) {
                effectiveCustomerId = currentUser.getId();
            }

            Order newOrder = orderFacade.placeOrder(effectiveCustomerId, request.menuItemIds, request.pickupSlot);
            return ResponseEntity.ok(newOrder);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/slots")
    public ResponseEntity<List<String>> getSlots() {
        List<String> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end = LocalTime.of(20, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        while (start.isBefore(end)) {
            LocalTime next = start.plusMinutes(15);
            slots.add(start.format(formatter) + " - " + next.format(formatter));
            start = next;
        }

        return ResponseEntity.ok(slots);
    }

    // GET request to /api/orders/customer/{id}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerOrders(@PathVariable int customerId, Principal principal) {
        try {
            User currentUser = resolveCurrentUser(principal);
            boolean isPrivileged = currentUser instanceof Staff || currentUser instanceof Admin;

            if (!isPrivileged && !Objects.equals(currentUser.getId(), customerId)) {
                throw new SecurityException("You can only view your own orders.");
            }

            List<Order> orders = orderFacade.getCustomerOrders(customerId);
            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET request to /api/orders/customer/by-email?email=...
    @GetMapping("/customer/by-email")
    public ResponseEntity<?> getCustomerOrdersByEmail(@RequestParam String email, Principal principal) {
        try {
            User currentUser = resolveCurrentUser(principal);
            boolean isPrivileged = currentUser instanceof Staff || currentUser instanceof Admin;

            if (!isPrivileged) {
                throw new SecurityException("Only staff or admin can fetch orders by customer email.");
            }

            User customer = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found for email: " + email));

            List<Order> orders = orderFacade.getCustomerOrders(customer.getId());
            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET request to /api/orders/my
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        try {
            User currentUser = resolveCurrentUser(principal);
            List<Order> orders = orderFacade.getCustomerOrders(currentUser.getId());
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT request to /api/orders/{orderId}/cancel
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable int orderId, Principal principal) {
        try {
            Order cancelledOrder = orderFacade.cancelOrder(orderId, principal.getName());
            return ResponseEntity.ok(cancelledOrder);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT request to /api/orders/{orderId}/receive
    @PutMapping("/{orderId}/receive")
    public ResponseEntity<?> markReceived(@PathVariable int orderId, Principal principal) {
        try {
            Order receivedOrder = orderFacade.markOrderReceived(orderId, principal.getName());
            return ResponseEntity.ok(receivedOrder);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT request to /api/orders/{orderId}/status
    // Used by canteen staff to update status to PREPARING, READY, etc.
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable int orderId, @RequestParam String status) {
        try {
            Order updatedOrder = orderFacade.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User resolveCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new SecurityException("Authentication required");
        }

        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }
}