package com.kob.backend.controller.user.account.wechat;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.account.wechat.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class WechatController {
    @Autowired
    private WechatService wechatService;

    @PostMapping("/api/user/account/wechat/receivemessage/")
    public String receiveMessage(HttpServletRequest request) throws Exception {
        return wechatService.receiveMessage(request);
    }

    @GetMapping("/api/user/account/wechat/applyqr/")
    public JSONObject applyqr(){
        return wechatService.applyQR();
    }

    @GetMapping("/api/user/account/wechat/applyinfo")
    public JSONObject receiveCode(@RequestParam Map<String,String> data){
        String scenestr=data.get("scenestr");
        return wechatService.applyInfo(scenestr);
    }
}
