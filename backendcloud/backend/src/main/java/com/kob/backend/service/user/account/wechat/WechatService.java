package com.kob.backend.service.user.account.wechat;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public interface WechatService {
    public String receiveMessage(HttpServletRequest request) throws Exception;
    public JSONObject applyQR();
    public JSONObject applyInfo(String scenestr);
}
