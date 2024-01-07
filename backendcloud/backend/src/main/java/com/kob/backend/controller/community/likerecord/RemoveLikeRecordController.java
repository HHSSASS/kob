package com.kob.backend.controller.community.likerecord;

import com.kob.backend.service.community.likerecord.RemoveLikeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RemoveLikeRecordController {
    @Autowired
    private RemoveLikeRecordService removeLikeRecordService;

    @PostMapping("/community/likerecord/remove/")
    public Map<String,String> remove(@RequestParam Map<String,String> data){
        return removeLikeRecordService.remove(data);
    }
}
