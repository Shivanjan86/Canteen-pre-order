package com.pesu.canteen.pattern.command;

import com.pesu.canteen.model.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandInvoker {

    public Order execute(OrderCommand command) {
        return command.execute();
    }
}
