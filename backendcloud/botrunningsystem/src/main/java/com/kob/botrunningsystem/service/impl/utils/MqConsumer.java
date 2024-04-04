package com.kob.botrunningsystem.service.impl.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Component
@RocketMQMessageListener(consumerGroup = "consumer",topic = "bot")
public class MqConsumer implements RocketMQListener<Map<String,String>> {
    private static RestTemplate restTemplate;
    private static ReceiveMoveService receiveMoveService;
    private static ExecutorService threadPool;

    static {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(5, 20, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), threadFactory);
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MqConsumer.restTemplate=restTemplate;
    }
    @Autowired
    public void setReceiveMoveService(ReceiveMoveService receiveMoveService){
        MqConsumer.receiveMoveService=receiveMoveService;
    }

    private String addUid(String code,String uid){
        int k=code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0,k)+uid+code.substring(k);
    }
    @Override
    public void onMessage(Map<String, String> data) {
        threadPool.execute(() -> {
            Bot bot=new Bot(data.get("uuid"),Integer.parseInt(data.get("user_id")),data.get("bot_code"),data.get("input"));
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
            MultiValueMap<String,String> resp=new LinkedMultiValueMap<>();
            resp.add("uuid",bot.uuid);
            resp.add("user_id",bot.getUserId().toString());
            resp.add("direction",direction.toString());
            //restTemplate.postForObject("http://127.0.0.1:3001/pk/receivebotmove/",data,String.class);
            receiveMoveService.receiveBotMove(resp);
        });
    }
}
