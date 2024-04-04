package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    public String uuid;
    private final Integer rows,cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx={-1,0,1,0},dy={0,1,0,-1};
    private final Player playerA,playerB;
    private Integer nextStepA=null,nextStepB=null;
    private ReentrantLock lock=new ReentrantLock();
    private String status="playing";//playing->finished
    private String winner="";//all平局;A;B


    public Game(String uuid,Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Bot botA, Integer idB,Bot botB) {
        this.uuid=uuid;
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA=-1,botIdB=-1;
        String botCodeA="",botCodeB="";
        if(botA!=null){
            botIdA=botA.getId();
            botCodeA=botA.getContent();
        }
        if(botB!=null){
            botIdB=botB.getId();
            botCodeB=botB.getContent();
        }

        playerA =new Player(idA,botIdA,botCodeA,rows-2,1,new ArrayList<>(),true);
        playerB =new Player(idB,botIdB,botCodeB,1,cols-2,new ArrayList<>(),true);
    }
    public Player getPlayerA(){
        return playerA;
    }
    public Player getPlayerB(){
        return playerB;
    }
    public int[][] getG(){
        return g;
    }

    public boolean check_connectivity(int sx,int sy,int tx,int ty){
        if(sx==tx&&sy==ty) return true;
        g[sx][sy]=1;
        for(int i=0;i<4;i++){
            int x=sx+dx[i],y=sy+dy[i];
            if(g[x][y]==0&&this.check_connectivity(x,y,tx,ty)){
                g[sx][sy]=0;
                return true;
            }
        }
        g[sx][sy]=0;
        return false;
    }
    public boolean draw(){
        for(int r=0;r<this.rows;r++){//初始化
            for(int c=0;c<this.cols;c++){
                g[r][c]=0;
            }
        }

        for(int r=0;r<this.rows;r++){//四周创建墙
            g[r][0]=g[r][this.cols-1]=1;
        }
        for(int c=0;c<this.cols;c++){
            g[0][c]=g[this.rows-1][c]=1;
        }

        Random random=new Random();
        for(int i=0;i<this.inner_walls_count/2;i++){//随机创建墙
            for(int j=0;j<1000;j++){
                int r=random.nextInt(this.rows);
                int c=random.nextInt(this.cols);
                if(g[r][c]==1||g[this.rows-1-r][this.cols-1-c]==1||r==this.rows-2&&c==1||r==1&&c==this.cols-2) continue;
                g[r][c]=g[this.rows-1-r][this.cols-1-c]=1;
                break;
            }
        }

        return check_connectivity(this.rows-2,1,1,this.cols-2);//检查连通
    }
    public void createMap(){
        for(int i=0;i<1000;++i){
            if(draw()) break;
        }
    }

    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try{
            this.nextStepA=nextStepA;
        }finally {
            lock.unlock();
        }
    }
    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try{
            this.nextStepB=nextStepB;
        }finally {
            lock.unlock();
        }
    }
    private String getInput(Player player){
        Player me,you;
        if(playerA.getId().equals(player.getId())){
            me=playerA;
            you=playerB;
        }else{
            me=playerB;
            you=playerA;
        }
        return getMapString()+"#"+
                me.getSx()+"#"+
                me.getSy()+"#("+
                me.getStepsString()+")#"+
                you.getSx()+"#"+
                you.getSy()+"#("+
                you.getStepsString()+")";
    }
    private void sendBotCode(Player player){
        if(player.getId()==0){
            AI ai=new AI();
            setNextStepB(ai.nextMove(getInput(player)));
            return;
        }
        if(player.getBotId().equals(-1)) return;

//        MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
//        data.add("uuid",uuid);
//        data.add("user_id",player.getId().toString());
//        data.add("bot_code",player.getBotCode());
//        data.add("input",getInput(player));
//        WebSocketServer.restTemplate.postForObject("http://127.0.0.1:3006/bot/add/",data,String.class);
//        WebSocketServer.botService.addBot(data);
        Map<String,String> data=new HashMap<>();
        data.put("uuid",uuid);
        data.put("user_id",player.getId().toString());
        data.put("bot_code",player.getBotCode());
        data.put("input",getInput(player));
        WebSocketServer.rocketMQTemplate.convertAndSend("bot",data);
    }
    private void sendReceiveMove(Player player){
        JSONObject resp=new JSONObject();
        resp.put("event","receivemove");
        resp.put("uuid",uuid);
        if(player.isConnection()&&WebSocketServer.users.get(player.getId())!=null)
            WebSocketServer.users.get(player.getId()).sendMessage(resp.toJSONString());
    }
    private boolean nextStep(){//等待两名玩家下一步操作
        try {
            Thread.sleep(200);//前端蛇0.2秒走一格
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for(int i=0;i<50;++i){
            try{
                Thread.sleep(100);
                lock.lock();
                try{
                    if(nextStepA!=null){
                        sendReceiveMove(playerA);
                    }
                    if(nextStepB!=null){
                        sendReceiveMove(playerB);
                    }
                    if(nextStepA!=null&&nextStepB!=null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    private boolean check_valid(List<Cell> cellsA,List<Cell> cellsB){
        int n=cellsA.size();
        Cell cell=cellsA.get(n-1);
        if(g[cell.x][cell.y]==1) return false;
        for(int i=0;i<n-1;++i){
            if(cellsA.get(i).x==cell.x&&cellsA.get(i).y==cell.y) return false;
        }
        for(int i=0;i<n-1;++i){
            if(cellsB.get(i).x==cell.x&&cellsB.get(i).y==cell.y) return false;
        }
        return true;
    }
    private void judge(){//判断两名玩家下一步操作是否合法
        List<Cell> cellsA=playerA.getCells();
        List<Cell> cellsB=playerB.getCells();
        boolean validA=check_valid(cellsA,cellsB);
        boolean validB=check_valid(cellsB,cellsA);
        if(!validA||!validB){
            status="finished";
            if(!validA&&!validB){
                winner="all";
            }else if(!validA){
                winner="B";
            }else{
                winner="A";
            }
        }
    }
    private String getMapString(){
        StringBuilder res=new StringBuilder();
        for(int i=0;i<rows;++i){
            for(int j=0;j<cols;++j){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }
    private void updateUserRating(Player player,Integer rating){
        User user=WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }
    private void saveToDataBase(){
        Record record=new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                winner,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }
    private void sendAllMessage(String message){
        if(playerA.isConnection()&&WebSocketServer.users.get(playerA.getId())!=null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(playerB.isConnection()&&WebSocketServer.users.get(playerB.getId())!=null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    private void sendMove(){//向两名玩家传输移动信息
        lock.lock();
        try {
            JSONObject resp=new JSONObject();
            resp.put("event","move");
            resp.put("uuid",uuid);
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA=nextStepB=null;
        }finally {
            lock.unlock();
        }
    }
    private void sendResult(){//向两名玩家公布结果
        JSONObject resp=new JSONObject();
        resp.put("event","result");
        resp.put("uuid",uuid);
        resp.put("winner",winner);

        if(playerB.getId()!=0){//修改积分
            Integer ratingA=WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
            Integer ratingB=WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
            if("A".equals(winner)){
                ratingA+=5;
                ratingB-=3;
                resp.put("a_rating",ratingA.toString()+"(+5)");
                resp.put("b_rating",ratingB.toString()+"(-3)");
            }else if("B".equals(winner)){
                ratingA-=3;
                ratingB+=5;
                resp.put("a_rating",ratingA.toString()+"(-3)");
                resp.put("b_rating",ratingB.toString()+"(+5)");
            }else{
                resp.put("a_rating",ratingA.toString()+"(+0)");
                resp.put("b_rating",ratingB.toString()+"(+0)");
            }
            updateUserRating(playerA,ratingA);
            updateUserRating(playerB,ratingB);
        }else{
            Integer ratingA=WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
            if("A".equals(winner)){
                ratingA+=1;
                resp.put("a_rating",ratingA.toString()+"(+1)");
                resp.put("b_rating","");
            }else if("B".equals(winner)){
                ratingA-=1;
                resp.put("a_rating", ratingA.toString()+"(-1)");
                resp.put("b_rating", "");
            }else{
                resp.put("a_rating",ratingA.toString()+"(+0)");
                resp.put("b_rating","");
            }
            updateUserRating(playerA,ratingA);
        }

        saveToDataBase();
        sendAllMessage(resp.toJSONString());
    }
    @Override
    public void run() {
        for(int i=0;i<1000;++i){
            if(nextStep()){
                judge();
                if(status.equals("playing")){
                    sendMove();
                }else{
                    sendResult();
                    break;
                }
            }else{
                status="finished";
                lock.lock();;
                try{
                    if (nextStepA == null && nextStepB == null) {
                        winner = "all";
                    } else if (nextStepA == null) {
                        winner = "B";
                    } else {
                        winner = "A";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
