package org.example.online_ticketing_system.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.example.online_ticketing_system.model.User;
import org.example.online_ticketing_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class RegisterAuthController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> registerData) {
        String phone = registerData.get("username");
        String password = registerData.get("password");
        String email = registerData.get("email");
        Map<String, Object> result = new HashMap<>();
        
        // 验证必填字段
        if (phone == null || password == null) {
            result.put("success", false);
            result.put("message", "手机号和密码不能为空");
            return result;
        }
        
        // 验证手机号格式(11位数字)
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            result.put("success", false);
            result.put("message", "手机号格式不正确");
            return result;
        }
        
        // 验证邮箱格式(如果提供了邮箱)
        if (email != null && !email.isEmpty() && !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            result.put("success", false);
            result.put("message", "邮箱格式不正确");
            return result;
        }
        
        // 检查手机号和邮箱是否已存在
        if (userRepository.findByUsername(phone) != null) {
            result.put("success", false);
            result.put("message", "手机号已注册");
            return result;
        }
        if (email != null && !email.isEmpty() && userRepository.findByEmail(email) != null) {
            result.put("success", false);
            result.put("message", "邮箱已被注册");
            return result;
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(phone);
        user.setPassword(password);
        user.setEmail(email);
        user.setregister_time(LocalDateTime.now().toString());
        userRepository.save(user);
        
        result.put("success", true);
        result.put("message", "注册成功");
        return result;
    }
}
