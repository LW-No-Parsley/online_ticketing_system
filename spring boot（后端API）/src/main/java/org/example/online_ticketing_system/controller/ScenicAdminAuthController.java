package org.example.online_ticketing_system.controller;

import org.example.online_ticketing_system.model.Admin;
import org.example.online_ticketing_system.repository.AdminRepository;
import org.example.online_ticketing_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scenic-admin")
public class ScenicAdminAuthController {

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        Map<String, Object> result = new HashMap<>();
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password) && "scenic".equals(admin.getRole())) {
            String token = JwtUtil.generateToken(admin);
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
}
