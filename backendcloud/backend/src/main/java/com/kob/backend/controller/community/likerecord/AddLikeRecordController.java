package com.kob.backend.controller.community.likerecord;

import com.kob.backend.service.community.likerecord.AddLikeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddLikeRecordController {
    @Autowired
    private AddLikeRecordService addLikeRecordService;

    @PostMapping("/community/likerecord/add/")
    public Map<String,String> add(@RequestParam Map<String,String> data){
        return addLikeRecordService.add(data);
    }
}
