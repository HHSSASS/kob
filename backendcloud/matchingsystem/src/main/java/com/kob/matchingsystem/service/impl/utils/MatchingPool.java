package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> players=new ArrayList<>();
    private ReentrantLock lock=new ReentrantLock();
    private static RestTemplate restTemplate;
    private static StartGameService startGameService;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate=restTemplate;
    }
    @Autowired
    public void setStartGameService(StartGameService startGameService){
        MatchingPool.startGameService=startGameService;
    }
    public void addPlayer(Integer userId,Integer rating,Integer botId){
        lock.lock();
        try{
            players.add(new Player(userId,rating,botId,0));
        }finally {
            lock.unlock();
        }
    }
    public boolean removePlayer(Integer userId){
        boolean flag=false;
        lock.lock();
        try{
            List<Player> newPlayers=new ArrayList<>();
            for(Player player:players){
                if(!player.getUserId().equals(userId)){
                    newPlayers.add(player);
                }else flag=true;
            }
            players=newPlayers;
        }finally {
            lock.unlock();
            return flag;
        }
    }
    private void increaseWaitingTime(){//将所有等待玩家等待时间加一
        for(Player player:players){
            player.setWaitingTime(player.getWaitingTime()+1);
        }
    }
    private boolean checkMatched(Player a,Player b){//判断两名玩家是否匹配
        int ratingDelta=Math.abs(a.getRating()-b.getRating());
        int waitingTime= Math.max(a.getWaitingTime(),b.getWaitingTime());
        return ratingDelta<=waitingTime*30;
    }
    private void sendResult(Player a,Player b){//返回匹配结果
        MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
        data.add("a_id",a.getUserId().toString());
        data.add("a_bot_id",a.getBotId().toString());
        data.add("b_id",b.getUserId().toString());
        data.add("b_bot_id",b.getBotId().toString());
        //restTemplate.postForObject("http://127.0.0.1:3001/pk/startgame/",data,String.class);
        startGameService.startgame(data);
    }
    private void matchPlayers(){//尝试匹配所有玩家
        //System.out.println("matchplayers "+players.toString());
        boolean[] used=new boolean[players.size()];
        for(int i=0;i<players.size();++i){
            if(used[i]) continue;
            for(int j=i+1;j<players.size();++j){
                if(used[j]) continue;
                Player a=players.get(i),b=players.get(j);
                if(checkMatched(a,b)){
                    used[i]=used[j]=true;
                    sendResult(a,b);
                    break;
                }
            }
        }
        List<Player> newPlayers=new ArrayList<>();
        for(int i=0;i<players.size();++i){
            if(!used[i]){
                newPlayers.add(players.get(i));
            }
        }
        players=newPlayers;
    }
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
