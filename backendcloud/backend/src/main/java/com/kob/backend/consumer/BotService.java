package com.kob.backend.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "botrunningsystem")
public interface BotService {
    @PostMapping(value = "/bot/add/")
    public String addBot(@RequestBody MultiValueMap<String,String> data);
}
