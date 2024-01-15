package com.kob.backend.controller.user.account.phone;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.account.phone.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PhoneController {
    @Autowired
    private PhoneService phoneService;

    @PostMapping("/api/user/account/phone/applycode/")
    public JSONObject applyCode(@RequestParam Map<String,String> data){
        String number=data.get("number");
        return phoneService.applyCode(number);
    }

    @PostMapping("/api/user/account/phone/login/")
    public JSONObject login(@RequestParam Map<String,String> data){
        String number=data.get("number");
        String code=data.get("code");
        return phoneService.login(number,code);
    }
}
