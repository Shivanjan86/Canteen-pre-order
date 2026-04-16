package com.pesu.canteen.service.interfaces;

import com.pesu.canteen.model.entity.Order;
import java.util.List;

public interface OrderService {
    
    // 1. Customer places a new order
    Order placeOrder(int customerId, List<Integer> menuItemIds, String pickupSlot);
    
    // 2. Customer can cancel their own placed order; staff/admin can cancel any order
    Order cancelOrder(int orderId, String currentUserEmail);

    // 2b. Customer marks a ready order as received/picked up
    Order markOrderReceived(int orderId, String currentUserEmail);
    
    // 3. Canteen Staff updates the status (e.g., from ACCEPTED to PREPARING)
    Order updateOrderStatus(int orderId, String newStatus);
    
    // 4. Customer views their own order history
    List<Order> getCustomerOrders(int customerId);
}