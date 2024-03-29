package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {
        Map<String,String> map=new HashMap<>();
        if(username==null) {
            map.put("message", "用户名不能为空");
            return map;
        }
        username=username.trim();
        if(username.length()==0) {
            map.put("message", "用户名不能为空");
            return map;
        }
        if(username.length()>100){
            map.put("message","用户名长度不能大于100");
            return map;
        }
        if(password==null||confirmPassword==null){
            map.put("message", "密码不能为空");
            return map;
        }
        if(password.length()==0||confirmPassword.length()==0){
            map.put("message", "密码不能为空");
            return map;
        }
        if(password.length()>100){
            map.put("message","密码长度不能大于100");
            return map;
        }
        if(!password.equals(confirmPassword)){
            map.put("message","两次输入的密码不一致");
            return map;
        }
        if("人机训练".equals(username)){
            map.put("message","用户名已存在");
            return map;
        }
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<User> users=userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("message","用户名已存在");
            return map;
        }

        String encodedPassword=passwordEncoder.encode(password);
        User user=new User(null,username,null,encodedPassword,null,null,null,0);
        userMapper.insert(user);
        map.put("message","successful");
        return map;
    }
}
