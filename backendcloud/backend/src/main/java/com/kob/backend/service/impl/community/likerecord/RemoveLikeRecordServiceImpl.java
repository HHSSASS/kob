package com.kob.backend.service.impl.community.likerecord;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.LikerecordMapper;
import com.kob.backend.pojo.Likerecord;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.likerecord.RemoveLikeRecordService;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveLikeRecordServiceImpl implements RemoveLikeRecordService {
    @Autowired
    private LikerecordMapper likerecordMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int post_id=Integer.parseInt(data.get("post_id"));
        Map<String,String> map=new HashMap<>();
        QueryWrapper<Likerecord> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId()).eq("post_id",post_id);
        Likerecord likerecord=likerecordMapper.selectOne(queryWrapper);
        if(likerecord==null){
            map.put("message","记录不存在");
            return map;
        }
        likerecordMapper.delete(queryWrapper);
        map.put("message","successful");
        return map;
    }
}
