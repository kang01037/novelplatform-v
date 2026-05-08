package org.example.novelplatform.service;

import java.util.Map;

public interface WechatAuthService {

    Map<String, Object> login(String code, String nickname, String avatarUrl);
}
