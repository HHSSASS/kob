package com.kob.backend.controller.user.account.wechat;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.account.wechat.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class WechatController {
    @Autowired
    private WechatService wechatService;

    @PostMapping("/api/user/account/wechat/applyqr/")
    public JSONObject applyQR(){
        return wechatService.applyQR();
    }

    @PostMapping("/api/user/account/wechat/applyinfo/")
    public JSONObject applyInfo(@RequestParam Map<String,String> data){
        String code=data.get("code");
        String state=data.get("state");
        return wechatService.applyInfo(code,state);
    }

    @GetMapping("/api/user/account/wechat/login/")
    public JSONObject login(@RequestParam Map<String,String> data){
        String state=data.get("state");
        return wechatService.login(state);
    }

}
