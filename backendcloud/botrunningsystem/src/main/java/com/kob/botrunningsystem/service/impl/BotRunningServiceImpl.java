package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public final static BotPool botPool=new BotPool();
    @Override
    public String addBot(String uuid,Integer userId, String botCode, String input) {
        System.out.println("add "+userId);
        botPool.addBot(uuid,userId, botCode, input);
        return "add bot successfully";
    }
}
