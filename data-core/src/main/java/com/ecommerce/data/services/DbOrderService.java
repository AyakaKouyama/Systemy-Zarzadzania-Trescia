package com.ecommerce.data.services;

import com.ecommerce.data.entities.Order;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DbOrderService {

    void save(Order order);

    List<Order> getAllOrders();
}
