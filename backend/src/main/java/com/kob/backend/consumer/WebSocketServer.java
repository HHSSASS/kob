package com.kob.backend.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    final static ConcurrentHashMap<Integer,WebSocketServer> users=new ConcurrentHashMap<>();
    final static CopyOnWriteArraySet<User> matchpool=new CopyOnWriteArraySet<>();
    private Session session=null;
    private User user;
    private static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper=userMapper;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session=session;
        System.out.println("connected!");
        Integer userId= JwtAuthentication.getUserId(token);//解析token
        this.user=userMapper.selectById(userId);
        if(this.user!=null){
            users.put(userId,this);
        }
        else{
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        System.out.println("disconnected!");
        if(this.user!=null){
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
    }

    private void startMatching(){
        System.out.println("startmathcing!");
        matchpool.add(this.user);
        while(matchpool.size()>=2){
            Iterator<User> it=matchpool.iterator();
            User a=it.next(),b=it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game=new Game(13,14,20,a.getId(),b.getId());
            game.createMap();
            JSONObject respGame=new JSONObject();
            respGame.put("a_id",game.getPlayerA().getId());
            respGame.put("a_sx",game.getPlayerA().getSx());
            respGame.put("a_sy",game.getPlayerA().getSy());
            respGame.put("b_id",game.getPlayerB().getId());
            respGame.put("b_sx",game.getPlayerB().getSx());
            respGame.put("b_sy",game.getPlayerB().getSy());
            respGame.put("map",game.getG());

            JSONObject respA=new JSONObject();
            respA.put("event","start-playing");
            respA.put("opponent_username",b.getUsername());
            respA.put("opponent_photo",b.getPhoto());
            respA.put("game",respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());
            JSONObject respB=new JSONObject();
            respB.put("event","start-playing");
            respB.put("opponent_username",a.getUsername());
            respB.put("opponent_photo",a.getPhoto());
            respB.put("game",respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }
    private void stopMatching(){
        System.out.println("stopmatching!");
        matchpool.remove(this.user);
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("receive message!");
        JSONObject data= JSON.parseObject(message);
        String event=data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
        }
        else if("stop-matching".equals(event)){
            stopMatching();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(message);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}