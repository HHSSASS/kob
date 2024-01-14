package com.kob.backend.controller.user.account.qq;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.account.qq.QQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class QQController {
    @Autowired
    QQService qqService;

    @GetMapping("/api/user/account/qq/applycode/")
    public JSONObject applyCode(){
        return qqService.applyCode();
    }

    @GetMapping("/api/user/account/qq/receivecode")
    public JSONObject receiveCode(@RequestParam Map<String,String> data){
        String code=data.get("code");
        String state=data.get("state");
        return qqService.receiveCode(code,state);
    }
}
