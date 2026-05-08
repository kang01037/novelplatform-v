package org.example.novelplatform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.novelplatform.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/user/login",
            "/api/user/register",
            "/api/auth/refresh",
            "/api/wechat/login",
            "/api/wechat/check-token",
            // 管理操作（无需认证）
            "/api/user/update",
            "/api/user/delete/",
            "/api/user/avatar/",
            "/api/novel/create",
            "/api/novel/update",
            "/api/novel/delete/",
            "/api/novel/",
            "/api/chapter/create",
            "/api/chapter/update",
            "/api/chapter/delete/",
            "/api/bookshelf/add",
            "/api/bookshelf/remove",
            "/api/bookshelf/reading-progress",
            "/api/comment/create",
            "/api/comment/update",
            "/api/comment/delete/",
            "/api/comment/",
            "/uploads/"
    );

    private static final List<String> PUBLIC_GET_EXACT_PATHS = List.of(
            "/api/novel/list",
            "/api/novel/hot",
            "/api/novel/latest",
            "/api/novel/search"
    );

    private static final List<String> PUBLIC_GET_PREFIX_PATHS = List.of(
            "/api/novel/cover/",
            "/api/novel/author/",
            "/api/novel/category/",
            "/api/user/avatar/",
            "/api/chapter/novel/",
            "/api/comment/novel/",
            "/api/comment/chapter/",
            "/api/comment/replies/"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }

        if ("GET".equalsIgnoreCase(method) && isPublicGetPath(path)) {
            return true;
        }

        return false;
    }

    private boolean isPublicGetPath(String path) {
        for (String exact : PUBLIC_GET_EXACT_PATHS) {
            if (path.equals(exact)) {
                return true;
            }
        }

        for (String prefix : PUBLIC_GET_PREFIX_PATHS) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        if (path.matches("/api/novel/\\d+")) {
            return true;
        }

        if (path.matches("/api/chapter/\\d+")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未提供认证令牌\"}");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateAccessToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"令牌无效或已过期\"}");
            return;
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, username, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 认证失败：{}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"认证失败\"}");
        }
    }
}
