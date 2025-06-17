package org.example.online_ticketing_system.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.online_ticketing_system.model.Order;
import org.example.online_ticketing_system.model.TicketInventory;
import org.example.online_ticketing_system.repository.OrderRepository;
import org.example.online_ticketing_system.repository.TicketInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketInventoryRepository ticketInventoryRepository;

    @PostMapping("/create")
    @org.example.online_ticketing_system.config.RequiresAuth
    public Map<String, Object> createOrder(@RequestBody Map<String, String> orderData) {
        String date = orderData.get("date");
        String time = orderData.get("time");

        Order order = new Order();
        order.setDate(date);
        order.setTime(time);
        order.setStatus("待支付");
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "订单创建成功");
        result.put("orderId", order.getId());
        return result;
    }

    @GetMapping("/status/{id}")
    public Map<String, Object> getOrderStatus(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);

        Map<String, Object> result = new HashMap<>();
        if (order.isPresent()) {
            result.put("success", true);
            result.put("status", order.get().getStatus());
        } else {
            result.put("success", false);
            result.put("message", "订单不存在");
        }
        return result;
    }

    @PostMapping("/update-status")
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"admin"})
    public Map<String, Object> updateOrderStatus(@RequestBody Map<String, String> statusData) {
        Long orderId = Long.valueOf(statusData.get("orderId"));
        String newStatus = statusData.get("status");

        Optional<Order> order = orderRepository.findById(orderId);

        Map<String, Object> result = new HashMap<>();
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setStatus(newStatus);
            orderRepository.save(existingOrder);

            result.put("success", true);
            result.put("message", "订单状态更新成功");
        } else {
            result.put("success", false);
            result.put("message", "订单不存在");
        }
        return result;
    }

    @PostMapping("/submit-order")
    @org.example.online_ticketing_system.config.RequiresAuth
    public ResponseEntity<Map<String, Object>> submitOrder(@RequestBody Order order) {
        if (order.getDate() == null && order.getSelectedDate() != null) {
            order.setDate(order.getSelectedDate().toString());
        }
        if (order.getTime() == null && order.getSelectedTime() != null) {
            order.setTime(order.getSelectedTime());
        }
        if (order.getStatus() == null) {
            order.setStatus("待支付");
        }
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // Update sold_tickets in ticket_inventory
        Optional<TicketInventory> ticketInventoryOptional = ticketInventoryRepository.findByDateAndTime(order.getDate(), order.getTime());
        if (ticketInventoryOptional.isPresent()) {
            TicketInventory ticketInventory = ticketInventoryOptional.get();
            ticketInventory.setSoldTickets(ticketInventory.getSoldTickets() + order.getTickets().stream().mapToInt(ticket -> ticket.getCount()).sum());
            ticketInventoryRepository.save(ticketInventory);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "订单提交成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{username}")
    @org.example.online_ticketing_system.config.RequiresAuth
    public ResponseEntity<Map<String, Object>> getOrderDetailsByUsername(@PathVariable String username) {
        List<Order> orders = orderRepository.findByUsername(username);
        if (orders.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "找不到订单");
            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("orders", orders.stream().map(order -> {
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", order.getId());
            orderDetails.put("date", order.getDate());
            orderDetails.put("time", order.getTime());
            orderDetails.put("status", order.getStatus());
            orderDetails.put("username", order.getUsername());
            orderDetails.put("totalAmount", order.getTotalAmount());
            orderDetails.put("tickets", order.getTickets());
            return orderDetails;
        }).toList());

        return ResponseEntity.ok(response);
    }
}
