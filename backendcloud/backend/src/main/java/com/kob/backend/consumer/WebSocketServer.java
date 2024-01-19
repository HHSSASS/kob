package com.kob.backend.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    final public static ConcurrentHashMap<Integer,WebSocketServer> users=new ConcurrentHashMap<>();
    private Session session=null;
    private User user;
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static BotMapper botMapper;
    public static RestTemplate restTemplate;
    public Game game=null;

    private boolean connected=false;
    private ReentrantLock lock=new ReentrantLock();

    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper=userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper=recordMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper=botMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate=restTemplate;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session=session;
        System.out.println("connected!");
        Integer userId= JwtAuthentication.getUserId(token);//解析token
        this.user=userMapper.selectById(userId);
        if(this.user!=null){
            if(users.get(user.getId())!=null){
                JSONObject resp=new JSONObject();
                resp.put("event","error");
                users.get(user.getId()).sendMessage(resp.toJSONString());
                users.get(user.getId()).session.close();
            }
            users.put(userId,this);
        }
        else{
            this.session.close();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(5000);
                        lock.lock();
                        if(!connected) {
                            //System.out.println("disheartbeat");
                            if(session!=null){
                                session.close();
                            }else if (user != null) {
                                users.remove(user.getId());
                            }
                            break;
                        }
                        else{
                            connected=false;
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                }
            }
        }.start();
    }

    @OnClose
    public void onClose() {
        System.out.println("disconnected!");
        if (this.user != null) {
            users.remove(this.user.getId());
            MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
            data.add("user_id",this.user.getId().toString());
            restTemplate.postForObject("http://127.0.0.1:3001/player/remove/",data,String.class);
            if(game!=null){
                if(game.getPlayerA().getId().equals(user.getId())){
                    game.getPlayerA().setConnection(false);
                } else if(game.getPlayerB().getId().equals(user.getId())){
                    game.getPlayerB().setConnection(false);
                }
            }
        }
    }
    public static void startGame(Integer aId,Integer aBotId,Integer bId,Integer bBotId){
        User a=userMapper.selectById(aId),b=userMapper.selectById(bId);
        Bot botA=botMapper.selectById(aBotId),botB=botMapper.selectById(bBotId);

        UUID uuid=UUID.randomUUID();

        Game game=new Game(uuid.toString(),13,14,20,a.getId(),botA,b.getId(),botB);
        game.createMap();
        if(users.get(a.getId())!=null){
            users.get(a.getId()).game=game;
        }
        if(users.get(b.getId())!=null){
            users.get(b.getId()).game=game;
        }


        JSONObject respGame=new JSONObject();
        respGame.put("uuid",uuid.toString());
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
        if(users.get(a.getId())!=null)
            users.get(a.getId()).sendMessage(respA.toJSONString());
        JSONObject respB=new JSONObject();
        respB.put("event","start-playing");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game",respGame);
        if(users.get(b.getId())!=null)
            users.get(b.getId()).sendMessage(respB.toJSONString());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        game.start();
    }
    public static void startpveGame(Integer Id,Integer BotId){
        User a=userMapper.selectById(Id);
        Bot bot=botMapper.selectById(BotId);

        UUID uuid=UUID.randomUUID();

        Game game=new Game(uuid.toString(),13,14,20,a.getId(),bot,0,null);
        game.createMap();
        if(users.get(a.getId())!=null)
            users.get(a.getId()).game = game;

        JSONObject respGame=new JSONObject();
        respGame.put("uuid",uuid.toString());
        respGame.put("a_id",game.getPlayerA().getId());
        respGame.put("a_sx",game.getPlayerA().getSx());
        respGame.put("a_sy",game.getPlayerA().getSy());
        respGame.put("b_id",game.getPlayerB().getId());
        respGame.put("b_sx",game.getPlayerB().getSx());
        respGame.put("b_sy",game.getPlayerB().getSy());
        respGame.put("map",game.getG());

        JSONObject resp=new JSONObject();
        resp.put("event","start-pve");
        resp.put("game",respGame);
        if(users.get(a.getId())!=null)
            users.get(a.getId()).sendMessage(resp.toJSONString());

        game.start();
    }
    private void startMatching(Integer botId){
        //System.out.println("startmathcing!");
        MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id",botId.toString());
        restTemplate.postForObject("http://127.0.0.1:3001/player/add/",data,String.class);
    }
    private void stopMatching(){
        //System.out.println("stopmatching!");
        MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject("http://127.0.0.1:3001/player/remove/",data,String.class);
    }
    private void move(int direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1))
                game.setNextStepA(direction);
        } else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerB().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        //System.out.println("receive message!");
        JSONObject data= JSON.parseObject(message);
        String event=data.getString("event");
        if("start-matching".equals(event)){
            startMatching(data.getInteger("bot_id"));
        } else if("stop-matching".equals(event)){
            stopMatching();
        } else if("start-pve".equals(event)){
            startpveGame(user.getId(),data.getInteger("bot_id"));
        } else if("move".equals(event)){
            move(data.getInteger("direction"));
        } else if("heartbeat".equals(event)){
            try {
                lock.lock();
                connected=true;
                //System.out.println("heartbeat");
                JSONObject resp=new JSONObject();
                resp.put("event","heartbeat");
                if(users.get(user.getId())!=null) {
                    sendMessage(resp.toJSONString());
                }
            }finally {
                lock.unlock();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        synchronized (this.session){
            try {
                if(this.session!=null)
                    this.session.getBasicRemote().sendText(message);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}