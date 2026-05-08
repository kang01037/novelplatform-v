package org.example.novelplatform.service;

import org.example.novelplatform.util.ResponseMessage;

import java.util.Map;

public interface WechatAuthService {

    ResponseMessage<Map<String, Object>> login(String code, String nickname, String avatarUrl);
}
