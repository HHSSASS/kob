package com.kob.backend.controller.community;

import com.kob.backend.service.community.RemovePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RemovePostController {
    @Autowired
    private RemovePostService removePostService;

    @PostMapping("/community/remove/")
    public Map<String,String> remove(@RequestParam Map<String,String> data){
        return removePostService.remove(data);
    }
}
