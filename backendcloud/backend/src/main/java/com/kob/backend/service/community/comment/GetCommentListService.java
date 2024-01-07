package com.kob.backend.service.community.comment;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface GetCommentListService {
    JSONObject getList(Map<String,String> data);
}
