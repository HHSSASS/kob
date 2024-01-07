package com.kob.backend.controller.community.comment;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.community.comment.GetCommentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetCommentListController {
    @Autowired
    GetCommentListService getCommentListService;

    @GetMapping("/community/comment/getlist/")
    public JSONObject getList(@RequestParam Map<String,String> data){
        return getCommentListService.getList(data);
    }
}
