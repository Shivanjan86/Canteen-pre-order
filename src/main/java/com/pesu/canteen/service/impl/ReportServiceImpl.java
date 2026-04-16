package com.pesu.canteen.service.impl;

import com.pesu.canteen.dto.ReportSummaryDTO;
import com.pesu.canteen.model.entity.MenuItem;
import com.pesu.canteen.model.entity.Order;
import com.pesu.canteen.repository.OrderRepository;
import com.pesu.canteen.service.interfaces.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public ReportSummaryDTO getSummary() {
        List<Order> orders = orderRepository.findAll();

        long placed = orders.stream().filter(o -> "PLACED".equalsIgnoreCase(o.getStatus())).count();
        long preparing = orders.stream().filter(o -> "PREPARING".equalsIgnoreCase(o.getStatus())).count();
        long ready = orders.stream().filter(o -> "READY".equalsIgnoreCase(o.getStatus())).count();
        long pickedUp = orders.stream().filter(o -> "PICKED_UP".equalsIgnoreCase(o.getStatus())).count();
        long cancelled = orders.stream().filter(o -> "CANCELLED".equalsIgnoreCase(o.getStatus())).count();

        double revenue = orders.stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getStatus()))
                .flatMap(o -> o.getItems().stream())
                .mapToDouble(MenuItem::getPrice)
                .sum();

        Map<String, Integer> itemCount = new HashMap<>();
        for (Order order : orders) {
            for (MenuItem item : order.getItems()) {
                itemCount.merge(item.getName(), 1, Integer::sum);
            }
        }

        List<String> topItems = itemCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .toList();

        ReportSummaryDTO dto = new ReportSummaryDTO();
        dto.setTotalOrders(orders.size());
        dto.setPlacedOrders(placed);
        dto.setPreparingOrders(preparing);
        dto.setReadyOrders(ready);
        dto.setPickedUpOrders(pickedUp);
        dto.setCancelledOrders(cancelled);
        dto.setTotalRevenue(revenue);
        dto.setTopItems(topItems);

        return dto;
    }
}
