package com.kob.backend.service.user.account.qq;

import com.alibaba.fastjson.JSONObject;

public interface QQService {
    public JSONObject applyUrl();
    public JSONObject applyInfo(String code,String state);
}
