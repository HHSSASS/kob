package com.kob.backend.service.impl.user.account.phone;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.phone.PhoneService;
import com.kob.backend.utils.JwtUtil;
import com.kob.backend.utils.SMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class PhoneServiceImpl implements PhoneService {
    String signName="";
    String templateCode="";
    Random random=new Random();
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Override
    public JSONObject applyCode(String number) {
        JSONObject resp=new JSONObject();
        if(number==null||number.length()==0){
            resp.put("message","手机号码不能为空");
            return resp;
        }
        String regex = "^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$";
        if(!number.matches(regex)){
            resp.put("message","手机号码格式不正确");
            return resp;
        }
        StringBuilder code=new StringBuilder();
        for(int i=0;i<6;++i){
            code.append((char)(random.nextInt(10)+'0'));
        }
        SMSUtil.sendMessage(signName,templateCode,number,code.toString());
        redisTemplate.opsForValue().set(number,code.toString());
        redisTemplate.expire(number, Duration.ofMinutes(10));
        resp.put("message","successful");
        return resp;
    }

    @Override
    public JSONObject login(String number,String code) {
        JSONObject resp=new JSONObject();
        if(number==null||number.length()==0){
            resp.put("message","手机号码不能为空");
            return resp;
        }
        String regex = "^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$";
        if(!number.matches(regex)){
            resp.put("message","手机号码格式不正确");
            return resp;
        }
        if(code==null||code.length()==0){
            resp.put("message","验证码不能为空");
            return resp;
        }
        if(Boolean.TRUE.equals(redisTemplate.hasKey(number))){
            if(code.equals(redisTemplate.opsForValue().get(number))){
                redisTemplate.delete(number);
                QueryWrapper<User> queryWrapper=new QueryWrapper<>();
                queryWrapper.eq("phone_number",number);
                List<User> users=userMapper.selectList(queryWrapper);
                if(!users.isEmpty()){
                    User user=users.get(0);
                    String jwt= JwtUtil.createJWT(user.getId().toString());
                    resp.put("message","successful");
                    resp.put("jwt_token",jwt);
                    return resp;
                }else{
                    String username=number+'_';
                    for (int i = 0; i < 100; i ++ ) {
                        QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
                        usernameQueryWrapper.eq("username", username);
                        if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
                        username += (char)(random.nextInt(10) + '0');
                        if (i == 99) return resp;
                    }
                    User user=new User(null,username,null,null,number,null,null,0);
                    userMapper.insert(user);
                    String jwt= JwtUtil.createJWT(user.getId().toString());
                    resp.put("message","successful");
                    resp.put("jwt_token",jwt);
                    return resp;
                }
            }else{
                resp.put("message","验证码不正确，请重新输入");
                return resp;
            }
        }else{
            resp.put("message","验证码不正确，请重新输入");
            return resp;
        }
    }
}
