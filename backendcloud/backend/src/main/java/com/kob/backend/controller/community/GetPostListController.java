package com.kob.backend.controller.community;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.community.GetPostListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetPostListController {
    @Autowired
    GetPostListService getPostListService;

    @GetMapping("/community/getlist/")
    public JSONObject getList(@RequestParam Map<String,String> data){
        Integer page=Integer.parseInt(data.get("page"));
        return getPostListService.getList(page);
    }
}
