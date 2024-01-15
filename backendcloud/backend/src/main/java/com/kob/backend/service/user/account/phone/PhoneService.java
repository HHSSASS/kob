package com.kob.backend.service.user.account.phone;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface PhoneService {
    JSONObject applyCode(String number);
    JSONObject login(String number,String code);
}
