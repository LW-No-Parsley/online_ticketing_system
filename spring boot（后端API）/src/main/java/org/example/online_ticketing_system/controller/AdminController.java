package org.example.online_ticketing_system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.online_ticketing_system.repository.OrderRepository;
import org.example.online_ticketing_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理员控制器
 * 提供用户管理和订单管理的后台接口
 * 需要景区管理员(scenic)或系统管理员(system)权限才能访问
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 获取所有用户信息
     * @return 包含用户基本信息的列表(包含id、用户名、邮箱和注册时间)
     */
    @GetMapping("/users")
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"scenic", "system"})
    public ResponseEntity<List<Object>> getAllUsers() {
        List<Object> users = userRepository.findAll().stream().map(user -> {
            return new Object() {
                public final Long id = user.getId();
                public final String username = user.getUsername();
                public final String email = user.getEmail();
                public final String time = user.getregister_time();
            };
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * 获取所有订单信息
     * @return 包含订单详细信息的列表(包含订单ID、日期、时间、状态、用户名、总金额和票务信息)
     */
    @GetMapping("/orders")
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"scenic", "system"})
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        List<Map<String, Object>> orders = orderRepository.findAll().stream().map(order -> {
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", order.getId());
            orderDetails.put("date", order.getDate());
            orderDetails.put("time", order.getTime());
            orderDetails.put("status", order.getStatus());
            orderDetails.put("username", order.getUsername());
            orderDetails.put("totalAmount", order.getTotalAmount());
            orderDetails.put("tickets", order.getTickets());
            return orderDetails;
        }).toList();
        return ResponseEntity.ok(orders);
    }

    /**
     * 删除指定用户
     * @param id 要删除的用户ID
     * @return 包含操作结果的Map，success表示操作是否成功，message包含相关信息
     */
    @DeleteMapping("/users/{id}")
    @org.example.online_ticketing_system.config.RequiresAuth(roles = {"scenic", "system"})
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
