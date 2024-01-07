package com.kob.backend.service.impl.community;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.PostMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Post;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.AddPostService;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddPostServiceImpl implements AddPostService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostMapper postMapper;

    @Override
    public Map<String, String> add(Map<String,String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String content=data.get("content");
        Map<String,String> map=new HashMap<>();
        if(content==null||content.length()==0){
            map.put("message","内容不能为空");
            return map;
        }
        if(content.length()>200){
            map.put("message","内容长度大于超过200");
            return map;
        }
        Date now=new Date();
        Post post=new Post(null,user.getId(), content,now);
        postMapper.insert(post);
        map.put("message","successful");
        return map;
    }
}
