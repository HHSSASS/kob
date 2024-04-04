package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotTest;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    private static ReceiveMoveService receiveMoveService;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate=restTemplate;
    }
    @Autowired
    public void setReceiveMoveService(ReceiveMoveService receiveMoveService){
        Consumer.receiveMoveService=receiveMoveService;
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
        int k=code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0,k)+uid+code.substring(k);
    }
    @Override
    public void run() {
        UUID uuid=UUID.randomUUID();
        String uid=uuid.toString().substring(0,8);
        Supplier<Integer> botInterface= Reflect.compile(
                "com.kob.botrunningsystem.utils.BotTest"+uid,
                addUid(bot.getBotCode(),uid)
        ).create().get();

        File file=new File("input.txt");
        try(PrintWriter fout=new PrintWriter(file)) {
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Integer direction=botInterface.get();
        //System.out.println("move "+bot.getUserId()+" "+direction);
        MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
        data.add("uuid",bot.uuid);
        data.add("user_id",bot.getUserId().toString());
        data.add("direction",direction.toString());
        //restTemplate.postForObject("http://127.0.0.1:3000/pk/receivebotmove/",data,String.class);
        receiveMoveService.receiveBotMove(data);
    }
}
