package org.example.novelplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 公开的 POST 接口
                        .requestMatchers("/api/user/login").permitAll()
                        .requestMatchers("/api/user/register").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/wechat/login").permitAll()
                        .requestMatchers("/api/wechat/check-token").permitAll()
                        // 所有 GET 读取接口公开
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        // 管理操作公开（无需认证）
                        .requestMatchers("/api/user/update").permitAll()
                        .requestMatchers("/api/user/delete/**").permitAll()
                        .requestMatchers("/api/user/avatar/**").permitAll()
                        .requestMatchers("/api/novel/create").permitAll()
                        .requestMatchers("/api/novel/update").permitAll()
                        .requestMatchers("/api/novel/delete/**").permitAll()
                        .requestMatchers("/api/novel/*/click").permitAll()
                        .requestMatchers("/api/novel/*/collect").permitAll()
                        .requestMatchers("/api/novel/*/recommend").permitAll()
                        .requestMatchers("/api/novel/*/rate").permitAll()
                        .requestMatchers("/api/chapter/create").permitAll()
                        .requestMatchers("/api/chapter/update").permitAll()
                        .requestMatchers("/api/chapter/delete/**").permitAll()
                        .requestMatchers("/api/bookshelf/add").permitAll()
                        .requestMatchers("/api/bookshelf/remove").permitAll()
                        .requestMatchers("/api/bookshelf/remove/batch").permitAll()
                        .requestMatchers("/api/bookshelf/reading-progress").permitAll()
                        .requestMatchers("/api/comment/create").permitAll()
                        .requestMatchers("/api/comment/update").permitAll()
                        .requestMatchers("/api/comment/delete/**").permitAll()
                        .requestMatchers("/api/comment/*/like").permitAll()
                        .requestMatchers("/api/comment/*/unlike").permitAll()
                        // 静态资源
                        .requestMatchers("/uploads/**").permitAll()
                        // 其余需要认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
