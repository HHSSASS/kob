package com.kob.backend.service.user.info;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface UpdatePhotoService {
    public JSONObject update(MultipartFile file);
}
