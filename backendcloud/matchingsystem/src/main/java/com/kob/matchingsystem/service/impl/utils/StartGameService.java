package com.kob.matchingsystem.service.impl.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("backend")
public interface StartGameService {
    @PostMapping("/pk/startgame/")
    public String startgame(@RequestBody MultiValueMap<String,String> data);
}
