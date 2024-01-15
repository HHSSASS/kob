package com.kob.backend.controller.user.account.qq;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.account.qq.QQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class QQController {
    @Autowired
    QQService qqService;

    @PostMapping("/api/user/account/qq/applyurl/")
    public JSONObject applyUrl(){
        return qqService.applyUrl();
    }

    @PostMapping("/api/user/account/qq/applyinfo/")
    public JSONObject applyInfo(@RequestParam Map<String,String> data){
        String code=data.get("code");
        String state=data.get("state");
        return qqService.applyInfo(code,state);
    }
}
