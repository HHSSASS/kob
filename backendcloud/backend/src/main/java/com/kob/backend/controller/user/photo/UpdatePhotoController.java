package com.kob.backend.controller.user.photo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdatePhotoController {
    @PostMapping("/api/user/photo/update/")
    public String update(@RequestParam Map<String,String> data){
        System.out.println(data);
        return "successful";
    }
}
