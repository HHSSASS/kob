package com.kob.backend.service.impl.community.likerecord;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.LikerecordMapper;
import com.kob.backend.pojo.Likerecord;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.likerecord.AddLikeRecordService;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AddLikeRecordServiceImpl implements AddLikeRecordService {
    @Autowired
    private LikerecordMapper likerecordMapper;

    @Override
    public Map<String, String> add(Map<String,String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int post_id=Integer.parseInt(data.get("post_id"));
        Map<String,String> map=new HashMap<>();
        QueryWrapper<Likerecord> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId()).eq("post_id",post_id);
        Likerecord likerecord=likerecordMapper.selectOne(queryWrapper);
        if(likerecord!=null){
            map.put("message","记录已存在");
            return map;
        }
        likerecord=new Likerecord(null, user.getId(), post_id);
        likerecordMapper.insert(likerecord);
        map.put("message","successful");
        return map;
    }
}
