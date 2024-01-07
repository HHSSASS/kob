package com.kob.backend.service.community;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface GetPostListService {
    JSONObject getList(Integer page);
}
