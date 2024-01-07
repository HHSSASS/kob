package com.kob.backend.service.impl.community;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.CommentMapper;
import com.kob.backend.mapper.LikerecordMapper;
import com.kob.backend.mapper.PostMapper;
import com.kob.backend.pojo.Comment;
import com.kob.backend.pojo.Likerecord;
import com.kob.backend.pojo.Post;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.RemovePostService;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemovePostServiceImpl implements RemovePostService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LikerecordMapper likerecordMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser=(UserDetailsImpl) authenticationToken.getPrincipal();
        User user=loginUser.getUser();

        int post_id=Integer.parseInt(data.get("post_id"));
        Post post=postMapper.selectById(post_id);
        Map<String,String> map=new HashMap<>();
        if(post==null){
            map.put("message","该帖不存在或已被删除");
            return map;
        }
        if(!post.getUserId().equals(user.getId())){
            map.put("message","没有权限删除该帖");
            return map;
        }
        postMapper.deleteById(post_id);
        QueryWrapper<Comment> queryWrapper1 =new QueryWrapper<>();
        queryWrapper1.eq("post_id",post_id);
        commentMapper.delete(queryWrapper1);
        QueryWrapper<Likerecord> queryWrapper2=new QueryWrapper<>();
        queryWrapper2.eq("post_id",post_id);
        likerecordMapper.delete(queryWrapper2);
        map.put("message","successful");
        return map;
    }
}
