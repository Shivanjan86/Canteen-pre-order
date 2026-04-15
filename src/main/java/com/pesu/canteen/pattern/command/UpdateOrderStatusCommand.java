package com.pesu.canteen.pattern.command;

import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.service.interfaces.OrderService;

public class UpdateOrderStatusCommand implements OrderCommand {

    private final OrderService orderService;
    private final int orderId;
    private final String newStatus;

    public UpdateOrderStatusCommand(OrderService orderService, int orderId, String newStatus) {
        this.orderService = orderService;
        this.orderId = orderId;
        this.newStatus = newStatus;
    }

    @Override
    public Order execute() {
        return orderService.updateOrderStatus(orderId, newStatus);
    }
}
