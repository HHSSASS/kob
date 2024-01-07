package com.kob.backend.service.impl.community.comment;

import com.kob.backend.mapper.CommentMapper;
import com.kob.backend.pojo.Comment;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.comment.RemoveCommentService;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveCommentServiceImpl implements RemoveCommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser=(UserDetailsImpl) authenticationToken.getPrincipal();
        User user=loginUser.getUser();

        int comment_id=Integer.parseInt(data.get("comment_id"));
        Comment comment=commentMapper.selectById(comment_id);

        Map<String,String> map=new HashMap<>();
        if(comment==null){
            map.put("message","该评论不存在或已被删除");
            return map;
        }
        if(!comment.getUserId().equals(user.getId())){
            map.put("message","没有权限删除该评论");
            return map;
        }
        commentMapper.deleteById(comment_id);
        map.put("message","successful");
        return map;
    }
}
