package com.kob.backend.service.user.account.wechat;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public interface WechatService {
    public JSONObject applyQR();
    public JSONObject applyInfo(String code,String state);
    public JSONObject login(String state);
}
