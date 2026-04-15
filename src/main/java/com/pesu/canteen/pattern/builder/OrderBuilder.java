package com.pesu.canteen.pattern.builder;

import com.pesu.canteen.model.entity.MenuItem;
import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class OrderBuilder {

    private final Order order;

    public OrderBuilder() {
        this.order = new Order();
    }

    public OrderBuilder customer(User customer) {
        order.setCustomer(customer);
        return this;
    }

    public OrderBuilder items(List<MenuItem> items) {
        order.setItems(items);
        return this;
    }

    public OrderBuilder status(String status) {
        order.setStatus(status);
        return this;
    }

    public OrderBuilder pickupSlot(String pickupSlot) {
        order.setPickupSlot(pickupSlot);
        return this;
    }

    public OrderBuilder orderTime(LocalDateTime orderTime) {
        order.setOrderTime(orderTime);
        return this;
    }

    public Order build() {
        return order;
    }
}
