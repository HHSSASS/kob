package com.kob.backend.service.impl.user.account.wechat;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.utils.CreateQRUtil;
import com.kob.backend.utils.DownloadPhotoUtil;
import com.kob.backend.utils.HttpClientUtil;
import com.kob.backend.service.user.account.wechat.WechatService;
import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.*;

@Service
public class WechatServiceImpl implements WechatService {
    private final static String appId="wx85ca0e59ad01ae35";
    private final static String appSecret="58e9c8aec2eec0bc6fe3a2d6c2c4ae34";
    private final static String redirectUrl="https://app6418.acapp.acwing.com.cn/user/account/wechat/auth";
    private final static String applyAccessTokenUrl="https://api.weixin.qq.com/sns/oauth2/access_token";
    private final static String getUserInfoUrl="https://api.weixin.qq.com/sns/userinfo";
    private final static Random random=new Random();

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public JSONObject applyQR() {
        JSONObject resp=new JSONObject();
        StringBuilder state=new StringBuilder();
        for(int i=0;i<10;++i){
            state.append((char)(random.nextInt(10)+'0'));
        }
        redisTemplate.opsForValue().set(state.toString(),"none");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));
        resp.put("state",state.toString());
        String encodeUrl="";
        try{
            encodeUrl= URLEncoder.encode(redirectUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("message","failed");
            return resp;
        }
        String applyUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId
                +"&redirect_uri="+encodeUrl
                +"&response_type=code"
                +"&scope=snsapi_userinfo"
                +"&state="+state
                +"#wechat_redirect";
        String qrUrl= CreateQRUtil.createQR(applyUrl,state.toString());
        resp.put("message","successful");
        resp.put("qr_url",qrUrl);
        return resp;
    }

    @Override
    public JSONObject applyInfo(String code,String state) {
        JSONObject resp=new JSONObject();
        resp.put("message","failed");
        if(code==null||state==null) return resp;
        if(Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;

        List<NameValuePair> nameValuePairs=new LinkedList<>();//获取accesstoken和openid
        nameValuePairs.add(new BasicNameValuePair("appid",appId));
        nameValuePairs.add(new BasicNameValuePair("secret",appSecret));
        nameValuePairs.add(new BasicNameValuePair("code",code));
        nameValuePairs.add(new BasicNameValuePair("grant_type","authorization_code"));
        String getString= HttpClientUtil.get(applyAccessTokenUrl,nameValuePairs);
        if(getString==null) return resp;
        JSONObject getResp=JSONObject.parseObject(getString);
        String accessToken=getResp.getString("access_token");
        String openid=getResp.getString("openid");
        if(accessToken==null||openid==null) return resp;

        nameValuePairs=new LinkedList<>();//获取用户信息
        nameValuePairs.add(new BasicNameValuePair("access_token",accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid",openid));
        nameValuePairs.add(new BasicNameValuePair("lang","zh_CN"));
        getString=HttpClientUtil.get(getUserInfoUrl,nameValuePairs);
        if(getString==null) return resp;
        getResp=JSONObject.parseObject(getString);
        String username=getResp.getString("nickname");
        String photo=getResp.getString("headimgurl");
        if(username==null) return resp;

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("wechat_openid",openid);
        List<User> users=userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            User user=users.get(0);
            String jwt= JwtUtil.createJWT(user.getId().toString());
            redisTemplate.opsForValue().set(state,jwt);
            resp.put("message","successful");
            return resp;
        }else{
            for (int i = 0; i < 100; i ++ ) {
                QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
                usernameQueryWrapper.eq("username", username);
                if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
                username += (char)(random.nextInt(10) + '0');
                if (i == 99) return resp;
            }
            User user=new User(null,username,photo,null,null,openid,null,0);
            userMapper.insert(user);
            if(DownloadPhotoUtil.downloadPhoto(photo,"/home/hh/kob/images/photo/"+user.getId().toString()+".png")){
                photo="https://app6418.acapp.acwing.com.cn/images/photo/"+user.getId().toString()+".png";
            }
            else photo=null;
            User new_user=new User(user.getId(),username,photo,null,null,openid,null,0);
            userMapper.updateById(new_user);
            String jwt= JwtUtil.createJWT(user.getId().toString());
            redisTemplate.opsForValue().set(state,jwt);
            resp.put("message","successful");
            return resp;
        }
    }

    @Override
    public JSONObject login(String state){
        JSONObject resp=new JSONObject();
        if(Boolean.FALSE.equals(redisTemplate.hasKey(state))){
            resp.put("message","failed");
            return resp;
        }else{
            String jwt=redisTemplate.opsForValue().get(state);
            if("none".equals(jwt)){
                resp.put("message","failed");
                return resp;
            }else{
                resp.put("message","successful");
                resp.put("jwt_token",jwt);
                return resp;
            }
        }
    }
}
