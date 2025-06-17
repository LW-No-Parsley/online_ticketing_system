package org.example.online_ticketing_system.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.example.online_ticketing_system.model.Admin;
import org.example.online_ticketing_system.model.User;
import org.example.online_ticketing_system.repository.AdminRepository;
import org.example.online_ticketing_system.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    private static final String SECRET_KEY = "wangshengyuan01234567899876543210"; // 长度>=32
    private static final long EXPIRATION = 300 * 60 * 60 * 1000; // 15天
    private static Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
    public static String generateToken(Admin admin) {
        return Jwts.builder()
                .setSubject(admin.getUsername())
                .claim("role", admin.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey())
                .compact();
    }
    public static String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey())
                .compact();
    }
    public static Admin parseToken(String token, AdminRepository adminRepository) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            String username = claims.getSubject();
            return adminRepository.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }

    public static User parseUserToken(String token, UserRepository userRepository) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            String username = claims.getSubject();
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
}
