package com.pesu.canteen.service.interfaces;

import com.pesu.canteen.model.entity.Order;
import java.util.List;

public interface OrderService {
    
    // 1. Customer places a new order
    Order placeOrder(int customerId, List<Integer> menuItemIds, String pickupSlot);
    
    // 2. Customer or Canteen Staff cancels an order
    Order cancelOrder(int orderId, int userId);
    
    // 3. Canteen Staff updates the status (e.g., from ACCEPTED to PREPARING)
    Order updateOrderStatus(int orderId, String newStatus);
    
    // 4. Customer views their own order history
    List<Order> getCustomerOrders(int customerId);
}