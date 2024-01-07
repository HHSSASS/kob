package com.kob.backend.service.impl.community.comment;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.CommentMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Comment;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.comment.GetCommentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GetCommentListServiceImpl implements GetCommentListService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Map<String,String> data) {
        int page=Integer.parseInt(data.get("page"));
        int post_id=Integer.parseInt(data.get("post_id"));
        IPage<Comment> commentIPage=new Page<>(page,5);
        QueryWrapper<Comment> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("post_id",post_id);
        queryWrapper.orderByDesc("id");
        List<Comment> comments=commentMapper.selectPage(commentIPage,queryWrapper).getRecords();
        JSONObject resp=new JSONObject();
        List<JSONObject> items=new LinkedList<>();
        for(Comment comment:comments){
            User user=userMapper.selectById(comment.getUserId());
            JSONObject item=new JSONObject();
            item.put("comment",comment);
            item.put("username",user.getUsername());
            item.put("photo",user.getPhoto());
            items.add(item);
        }
        resp.put("comments",items);
        resp.put("comments_count",commentMapper.selectCount(queryWrapper));
        return resp;
    }
}
