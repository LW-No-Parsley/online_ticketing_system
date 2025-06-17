package org.example.online_ticketing_system.config;

import org.example.online_ticketing_system.model.Admin;
import org.example.online_ticketing_system.model.User;
import org.example.online_ticketing_system.repository.AdminRepository;
import org.example.online_ticketing_system.repository.UserRepository;
import org.example.online_ticketing_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查是否是HandlerMethod
        if (!(handler instanceof org.springframework.web.method.HandlerMethod)) {
            return true;
        }

        org.springframework.web.method.HandlerMethod handlerMethod = (org.springframework.web.method.HandlerMethod) handler;
        
        // 检查是否需要认证
        RequiresAuth requiresAuth = handlerMethod.getMethodAnnotation(RequiresAuth.class);
        if (requiresAuth == null) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少有效的Token");
            return false;
        }

        // 尝试解析为Admin
        Admin admin = JwtUtil.parseToken(token, adminRepository);
        if (admin != null) {
            request.setAttribute("currentAdmin", admin);
            // 检查角色权限
        if (requiresAuth.roles().length > 0) {
            // 管理员角色可以是"scenic"或"system"
            boolean isAdminRole = admin.getRole().equals("scenic") || admin.getRole().equals("system");
            if (!isAdminRole && !hasRequiredRole(admin.getRole(), requiresAuth.roles())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "权限不足");
                return false;
            }
        }
            return true;
        }

        // 尝试解析为User
        User user = JwtUtil.parseUserToken(token, userRepository);
        if (user != null) {
            request.setAttribute("currentUser", user);
            // 普通用户只能访问没有角色限制或roles包含"user"的接口
            if (requiresAuth.roles().length > 0 && !hasRequiredRole("user", requiresAuth.roles())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "权限不足");
                return false;
            }
            return true;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的Token");
        return false;
    }

    private boolean hasRequiredRole(String userRole, String[] requiredRoles) {
        for (String role : requiredRoles) {
            if (role.equals(userRole)) {
                return true;
            }
        }
        return false;
    }
}
