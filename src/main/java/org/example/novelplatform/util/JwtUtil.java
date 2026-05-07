package org.example.novelplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:tielu12306SecretKey2026ForGatewayAuth}")
    private String secretKey;

    @Value("${jwt.access-expiration:1800000}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 生成 Access Token（短期，默认30分钟）
     */
    public String generateAccessToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "access");

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成 Refresh Token（长期，默认7天）
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "refresh");

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 判断是否为 Refresh Token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证 Access Token 是否有效（同时验证是 access 类型）
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date()) && "access".equals(claims.get("tokenType"));
        } catch (Exception e) {
            log.error("验证 Access Token 失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证 Refresh Token 是否有效
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date()) && "refresh".equals(claims.get("tokenType"));
        } catch (Exception e) {
            log.error("验证 Refresh Token 失败：{}", e.getMessage());
            return false;
        }
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }

    /**
     * 从 Token 中获取 Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
