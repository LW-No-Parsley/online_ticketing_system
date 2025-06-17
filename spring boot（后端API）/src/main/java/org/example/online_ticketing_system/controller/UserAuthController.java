package org.example.online_ticketing_system.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.online_ticketing_system.model.User;
import org.example.online_ticketing_system.model.UserToken;
import org.example.online_ticketing_system.repository.UserRepository;
import org.example.online_ticketing_system.repository.UserTokenRepository;
import org.example.online_ticketing_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        System.out.println("Login request received: " + loginData);
        String username = loginData.get("username");
        String password = loginData.get("password");
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // 检查用户是否有有效token
            List<UserToken> existingTokens = userTokenRepository.findValidTokensByUserId(
                user.getId(), LocalDateTime.now());
            
            if (!existingTokens.isEmpty()) {
                // 返回最新的有效token
                UserToken latestToken = existingTokens.stream()
                    .max((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                    .orElseThrow();
                result.put("success", true);
                result.put("message", "登录成功");
                result.put("token", latestToken.getToken());
            } else {
                // 生成新token
                String token = JwtUtil.generateToken(user);
                
                // 删除用户旧token
                userTokenRepository.deleteByUserId(user.getId());
                
                // 保存新token到数据库
                UserToken userToken = new UserToken();
                userToken.setUserId(user.getId());
                userToken.setToken(token);
                userToken.setCreatedAt(LocalDateTime.now());
                userToken.setExpiresAt(LocalDateTime.now().plusDays(15)); // 15天后过期，与JWT有效期一致
                userTokenRepository.save(userToken);
                
                result.put("success", true);
                result.put("message", "登录成功");
                result.put("token", token);
            }
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
}
