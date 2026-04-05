package org.example.novelplatformv.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User{
    @Id
    private Long userId;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String avatar;

    private String nickname;

    private String wechatOpenid;

    private Integer gender;

    private LocalDate birthday;

    private Integer userStatus;

    private LocalDateTime lastLoginTime;

    private Integer loginCount;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Integer deleted;
}
