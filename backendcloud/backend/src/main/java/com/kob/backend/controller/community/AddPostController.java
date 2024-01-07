package com.kob.backend.controller.community;

import com.kob.backend.service.community.AddPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddPostController {
    @Autowired
    private AddPostService addPostService;

    @PostMapping("/community/add/")
    public Map<String,String> add(@RequestParam Map<String,String> data){
        return addPostService.add(data);
    }
}
