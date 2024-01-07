package com.kob.backend.controller.community.comment;

import com.kob.backend.service.community.comment.RemoveCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RemoveCommentController {
    @Autowired
    RemoveCommentService removeCommentService;

    @PostMapping("/api/community/comment/remove/")
    public Map<String,String> remove(@RequestParam Map<String,String> data){
        return removeCommentService.remove(data);
    }
}
