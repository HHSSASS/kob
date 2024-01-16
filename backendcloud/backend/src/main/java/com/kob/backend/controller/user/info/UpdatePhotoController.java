package com.kob.backend.controller.user.info;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.impl.user.info.UpdatePhotoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UpdatePhotoController {
    @Autowired
    UpdatePhotoServiceImpl updatePhotoService;

    @PostMapping("/api/user/info/photo/")
    public JSONObject update(@RequestParam("file")MultipartFile file){
        return updatePhotoService.update(file);
    }
}
