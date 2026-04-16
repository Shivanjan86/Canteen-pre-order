package com.pesu.canteen.pattern.facade;

import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.pattern.command.CancelOrderCommand;
import com.pesu.canteen.pattern.command.OrderCommandInvoker;
import com.pesu.canteen.pattern.command.UpdateOrderStatusCommand;
import com.pesu.canteen.service.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderFacade {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderCommandInvoker orderCommandInvoker;

    public Order placeOrder(int customerId, List<Integer> menuItemIds, String pickupSlot) {
        return orderService.placeOrder(customerId, menuItemIds, pickupSlot);
    }

    public List<Order> getCustomerOrders(int customerId) {
        return orderService.getCustomerOrders(customerId);
    }

    public Order cancelOrder(int orderId, String currentUserEmail) {
        return orderCommandInvoker.execute(new CancelOrderCommand(orderService, orderId, currentUserEmail));
    }

    public Order markOrderReceived(int orderId, String currentUserEmail) {
        return orderService.markOrderReceived(orderId, currentUserEmail);
    }

    public Order updateOrderStatus(int orderId, String status) {
        return orderCommandInvoker.execute(new UpdateOrderStatusCommand(orderService, orderId, status));
    }
}
