package com.kob.backend.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "matchingsystem")
public interface MatchService {
    @PostMapping(value = "/player/add/")
    public  String addPlayer(@RequestBody MultiValueMap<String,String> data);

    @PostMapping(value = "/player/remove/")
    public String removePlayer(@RequestBody MultiValueMap<String,String> data);
}
