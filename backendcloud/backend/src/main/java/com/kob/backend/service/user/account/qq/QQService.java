package com.kob.backend.service.user.account.qq;

import com.alibaba.fastjson.JSONObject;

public interface QQService {
    public JSONObject applyCode();
    public JSONObject receiveCode(String code,String state);
}
