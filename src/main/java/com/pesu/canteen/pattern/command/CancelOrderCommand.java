package com.pesu.canteen.pattern.command;

import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.service.interfaces.OrderService;

public class CancelOrderCommand implements OrderCommand {

    private final OrderService orderService;
    private final int orderId;
    private final String currentUserEmail;

    public CancelOrderCommand(OrderService orderService, int orderId, String currentUserEmail) {
        this.orderService = orderService;
        this.orderId = orderId;
        this.currentUserEmail = currentUserEmail;
    }

    @Override
    public Order execute() {
        return orderService.cancelOrder(orderId, currentUserEmail);
    }
}
