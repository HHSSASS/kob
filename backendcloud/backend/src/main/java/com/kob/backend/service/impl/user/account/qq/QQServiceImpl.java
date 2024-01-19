package com.kob.backend.service.impl.user.account.qq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.utils.CreateQRUtil;
import com.kob.backend.utils.DownloadPhotoUtil;
import com.kob.backend.utils.HttpClientUtil;
import com.kob.backend.service.user.account.qq.QQService;
import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class QQServiceImpl implements QQService {
    private final static String appId="102084267";
    private final static String appSecret="53mOmzymLlwwhHcm";
    private final static String redirectUrl="https://app6418.acapp.acwing.com.cn/user/account/qq/auth";
    private final static String applyAccessTokenUrl="https://graph.qq.com/oauth2.0/token";
    private final static String applyOpenidUrl="https://graph.qq.com/oauth2.0/me";
    private final static String getUserInfoUrl="https://graph.qq.com/user/get_user_info";
    private final static Random random=new Random();

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public JSONObject applyUrl() {
        JSONObject resp=new JSONObject();
        StringBuilder state=new StringBuilder();
        for(int i=0;i<10;++i){
            state.append((char)(random.nextInt(10)+'0'));
        }
        redisTemplate.opsForValue().set(state.toString(),"none");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));
        String encodeUrl="";
        try{
            encodeUrl= URLEncoder.encode(redirectUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("message","failed");
            return resp;
        }
        String applyUrl="https://graph.qq.com/oauth2.0/authorize?response_type=code"
                +"&client_id="+appId
                +"&redirect_uri="+encodeUrl
                +"&state="+state
                +"&scope=get_user_info";
        resp.put("message","successful");
        resp.put("apply_url",applyUrl);
        return resp;
    }

    @Override
    public JSONObject applyInfo(String code,String state) {
        JSONObject resp=new JSONObject();
        resp.put("message","failed");
        if(code==null||state==null) return resp;
        if(Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;

        List<NameValuePair> nameValuePairs=new LinkedList<>();//获取accesstoken
        nameValuePairs.add(new BasicNameValuePair("grant_type","authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("client_id",appId));
        nameValuePairs.add(new BasicNameValuePair("client_secret",appSecret));
        nameValuePairs.add(new BasicNameValuePair("code",code));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri",redirectUrl));
        String getString= HttpClientUtil.get(applyAccessTokenUrl,nameValuePairs);
        if(getString==null) return resp;
        String[] strs=getString.split("&");
        String accessToken=strs[0].substring(strs[0].indexOf("=")+1);
        if(accessToken==null) return resp;

        nameValuePairs=new LinkedList<>();//获取openid
        nameValuePairs.add(new BasicNameValuePair("access_token",accessToken));
        getString= HttpClientUtil.get(applyOpenidUrl,nameValuePairs);
        if(getString==null) return resp;
        JSONObject getResp=JSONObject.parseObject(getString.substring(10,getString.length()-3));
        String openid=getResp.getString("openid");
        if(openid==null) return resp;

        nameValuePairs=new LinkedList<>();//获取用户信息
        nameValuePairs.add(new BasicNameValuePair("access_token",accessToken));
        nameValuePairs.add(new BasicNameValuePair("oauth_consumer_key",appId));
        nameValuePairs.add(new BasicNameValuePair("openid",openid));
        getString=HttpClientUtil.get(getUserInfoUrl,nameValuePairs);
        if(getString==null) return resp;
        getResp=JSONObject.parseObject(getString);
        String username=getResp.getString("nickname");
        String photo=getResp.getString("figureurl_qq_2");
        if(photo==null) photo=getResp.getString("figureurl_qq_1");
        if(username==null) return resp;

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("qq_openid",openid);
        List<User> users=userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            User user=users.get(0);
            String jwt= JwtUtil.createJWT(user.getId().toString());
            resp.put("message","successful");
            resp.put("jwt_token",jwt);
            return resp;
        }else{
            for (int i = 0; i < 100; i ++ ) {
                QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
                usernameQueryWrapper.eq("username", username);
                if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
                if(i==0) username+='_';
                username += (char)(random.nextInt(10) + '0');
                if (i == 99) return resp;
            }
            User user=new User(null,username,photo,null,null,null,openid,0);
            userMapper.insert(user);
            if(DownloadPhotoUtil.downloadPhoto(photo,"/home/hh/kob/images/photo/"+user.getId().toString()+".jpeg")){
                photo="https://app6418.acapp.acwing.com.cn/images/photo/"+user.getId().toString()+".jpeg";
            }
            else photo=null;
            User new_user=new User(user.getId(),username,photo,null,null,null,openid,0);
            userMapper.updateById(new_user);
            String jwt= JwtUtil.createJWT(user.getId().toString());
            resp.put("message","successful");
            resp.put("jwt_token",jwt);
            return resp;
        }
    }
}
