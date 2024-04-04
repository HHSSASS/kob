package com.kob.botrunningsystem.service.impl.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("backend")
public interface ReceiveMoveService {
    @PostMapping("/pk/receivebotmove/")
    public String receiveBotMove(@RequestBody MultiValueMap<String,String> data);
}
