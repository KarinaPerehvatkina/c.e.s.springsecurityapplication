package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getById(int id) {
        return orderRepository.getReferenceById(id);
    }

    public Order getByNumberPostfix(String postfix) {
        return orderRepository.getByNumberPostfix(postfix);
    }

    public Order update(Order order) {
        return orderRepository.save(order);
    }
}
