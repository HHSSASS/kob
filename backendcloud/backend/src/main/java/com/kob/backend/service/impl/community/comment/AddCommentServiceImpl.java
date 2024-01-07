package com.kob.backend.service.impl.community.comment;

import com.kob.backend.mapper.CommentMapper;
import com.kob.backend.mapper.PostMapper;
import com.kob.backend.pojo.Comment;
import com.kob.backend.pojo.Post;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.comment.AddCommentService;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddCommentServiceImpl implements AddCommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private PostMapper postMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int post_id=Integer.parseInt(data.get("post_id"));
        Post post=postMapper.selectById(post_id);
        String content=data.get("content");
        Map<String,String> map=new HashMap<>();
        if(post==null){
            map.put("message","该帖不存在或已被删除");
            return map;
        }
        if(content==null||content.length()==0){
            map.put("message","内容不能为空");
            return map;
        }
        if(content.length()>200){
            map.put("message","内容长度大于超过200");
            return map;
        }
        Date now=new Date();
        Comment comment=new Comment(null,user.getId(), post_id,content,now);
        commentMapper.insert(comment);
        map.put("message","successful");
        return map;
    }
}
