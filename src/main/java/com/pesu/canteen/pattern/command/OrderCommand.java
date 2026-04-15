package com.pesu.canteen.pattern.command;

import com.pesu.canteen.model.entity.Order;

public interface OrderCommand {
    Order execute();
}
