package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate=restTemplate;
    }
    public void startTimeout(long timeout,Bot bot){
        this.bot=bot;
        this.start();
        try {
            this.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt();//中断
        }
    }
    private String addUid(String code,String uid){
        int k=code.indexOf(" implements BotInterface");
        return code.substring(0,k)+uid+code.substring(k);
    }
    @Override
    public void run() {
        UUID uuid=UUID.randomUUID();
        String uid=uuid.toString().substring(0,8);
        BotInterface botInterface= Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot"+uid,
                addUid(bot.getBotCode(),uid)
        ).create().get();
        Integer direction=botInterface.nextMove(bot.getInput());
        System.out.println("move "+bot.getUserId()+" "+direction);
        MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
        data.add("user_id",bot.getUserId().toString());
        data.add("direction",direction.toString());
        restTemplate.postForObject("http://127.0.0.1:3000/pk/receivebotmove/",data,String.class);
    }
}
