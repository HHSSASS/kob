package com.kob.backend.controller.user.bot;

import com.kob.backend.pojo.Bot;
import com.kob.backend.service.user.bot.GetlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetlistController {
    @Autowired
    GetlistService getlistService;
    @GetMapping("/api/user/bot/getlist/")
    public List<Bot> getList(){
        return getlistService.getList();
    }
}
