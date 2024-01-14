package com.kob.backend.service.impl.user.account.wechat;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.utils.DownloadPhotoUtil;
import com.kob.backend.utils.HttpClientUtil;
import com.kob.backend.service.user.account.wechat.WechatService;
import com.kob.backend.utils.JwtUtil;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;

@Service
public class WechatServiceImpl implements WechatService {
    private final static String appId="wx85ca0e59ad01ae35";
    private final static String appSecret="58e9c8aec2eec0bc6fe3a2d6c2c4ae34";
    private final static String getUserInfoUrl="https://api.weixin.qq.com/sns/userinfo";
    private final static Random random=new Random();

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public static String bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        byte[] hex = "0123456789ABCDEF".getBytes();
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }
    @Override
    public String receiveMessage(HttpServletRequest request) throws Exception {//接受到消息
        String signature=request.getParameter("signature");
        String timestamp=request.getParameter("timestamp");
        String nonce=request.getParameter("nonce");
        String echostr=request.getParameter("echostr");
        String token="123456";
        String [] param=new String[]{timestamp,nonce,token};
        Arrays.sort(param);
        String paramstr=param[0]+param[1]+param[2];
        MessageDigest sha1=MessageDigest.getInstance("SHA-1");
        byte [] digestResult=sha1.digest(paramstr.getBytes("UTF-8"));
        String mySignature=bytes2HexString(digestResult).toLowerCase(Locale.ROOT);
        if(mySignature.equals(signature)&&echostr!=null){
            return echostr;
        }else{
            return callback(request).toJSONString();
        }
    }
    public JSONObject callback(HttpServletRequest request) throws Exception {//回应消息
        WxMpXmlMessage message=WxMpXmlMessage.fromXml(request.getInputStream());
        String messageType=message.getMsgType();								//消息类型
        String messageEvent=message.getEvent();								    //消息事件
        String fromUser=message.getFromUser();									//发送者帐号
        String toUser=message.getToUser();										//开发者微信号
        String text=message.getContent();                                       //文本消息  文本内容
        String eventKey=message.getEventKey();									//二维码参数
        redisTemplate.opsForValue().set(eventKey,fromUser);
        redisTemplate.expire(eventKey, Duration.ofMinutes(10));
        JSONObject resp=new JSONObject();
        resp.put("message","successful");
        return resp;
    }

    public String applyAccessToken(){//申请accesstoken
        if(Boolean.TRUE.equals(redisTemplate.hasKey("accesstoken"))) return redisTemplate.opsForValue().get("accesstoken");
        String applyUrl="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId
                + "&secret="+appSecret;
        String getString=HttpClientUtil.get(applyUrl);
        JSONObject getResp=JSONObject.parseObject(getString);
        String accessToken=getResp.getString("access_token");
        System.out.println(accessToken);
        redisTemplate.opsForValue().set("accesstoken",accessToken);
        redisTemplate.expire("accesstoken",Duration.ofMinutes(3));
        return accessToken;
    }
    @Override
    public JSONObject applyQR() {//申请二维码
        String accessToken=applyAccessToken();
        String applyUrl="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+accessToken;
        StringBuilder sceneStr=new StringBuilder();
        for(int i=0;i<10;++i){
            sceneStr.append((char)(random.nextInt(10)+'0'));
        }
        String json="{\"expire_seconds\": 3600, \"action_name\": \"QR_STR_SCENE\"" +", \"action_info\": {\"scene\": {\"scene_str\": \""+sceneStr+"\"}}}";
        String getString=HttpClientUtil.postJson(applyUrl,json);
        JSONObject getResp=JSONObject.parseObject(getString);
        getResp.put("message","successful");
        getResp.put("scenestr",sceneStr);
        return getResp;
    }

    @Override
    public JSONObject applyInfo(String scenestr) {
        JSONObject resp=new JSONObject();
        resp.put("message","failed");
        if(scenestr==null) return resp;
        if(Boolean.FALSE.equals(redisTemplate.hasKey(scenestr))) return resp;
        String openid=redisTemplate.opsForValue().get(scenestr);
        String accessToken=applyAccessToken();
        System.out.println(openid);

        List<NameValuePair> nameValuePairs=new LinkedList<>();//获取用户信息
        nameValuePairs=new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token",accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid",openid));
        nameValuePairs.add(new BasicNameValuePair("lang","zh_CN"));
        String getString=HttpClientUtil.get(getUserInfoUrl,nameValuePairs);
        System.out.println(getString);
        if(getString==null) return resp;
        JSONObject getResp=JSONObject.parseObject(getString);
        String username=getResp.getString("nickname");
        String photo=getResp.getString("headimgurl");
        if(username==null) return resp;

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("wechat_openid",openid);
        List<User> users=userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            User user=users.get(0);
            String jwt= JwtUtil.createJWT(user.getId().toString());
            resp.put("message","successful");
            resp.put("jwt_token",jwt);
            return resp;
        }
        for (int i = 0; i < 100; i ++ ) {
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", username);
            if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
            username += (char)(random.nextInt(10) + '0');
            if (i == 99) return resp;
        }
        User user=new User(null,username,null,photo,openid,0);
        userMapper.insert(user);
        if(photo==null) photo="https://cdn.luogu.com.cn/upload/image_hosting/lt6ej9ga.png";
        else{
            if(DownloadPhotoUtil.downloadPhoto(photo,"/home/hh/kob/images/photo/"+user.getId().toString()+".png")){
                photo="https://app6418.acapp.acwing.com.cn/images/photo/"+user.getId().toString()+".png";
            }
            else photo="https://cdn.luogu.com.cn/upload/image_hosting/lt6ej9ga.png";
        }
        User new_user=new User(user.getId(),username,null,photo,openid,0);
        userMapper.updateById(new_user);
        String jwt= JwtUtil.createJWT(user.getId().toString());
        resp.put("message","successful");
        resp.put("jwt_token",jwt);
        return resp;
    }
}
