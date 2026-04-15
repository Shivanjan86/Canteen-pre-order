package com.pesu.canteen.controller;

import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.pattern.facade.OrderFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderFacade orderFacade;

    // A helper class to grab the incoming JSON data for placing an order
    public static class OrderRequest {
        public int customerId;
        public List<Integer> menuItemIds;
        public String pickupSlot;
    }

    // POST request to /api/orders
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        try {
            Order newOrder = orderFacade.placeOrder(request.customerId, request.menuItemIds, request.pickupSlot);
            return ResponseEntity.ok(newOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/slots")
    public ResponseEntity<List<String>> getSlots() {
        return ResponseEntity.ok(Arrays.asList(
                "12:00-12:15",
                "12:15-12:30",
                "12:30-12:45",
                "12:45-13:00",
                "13:00-13:15",
                "13:15-13:30"
        ));
    }

    // GET request to /api/orders/customer/{id}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable int customerId) {
        List<Order> orders = orderFacade.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    // PUT request to /api/orders/{orderId}/cancel
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable int orderId, @RequestParam int userId) {
        try {
            Order cancelledOrder = orderFacade.cancelOrder(orderId, userId);
            return ResponseEntity.ok(cancelledOrder);
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
}