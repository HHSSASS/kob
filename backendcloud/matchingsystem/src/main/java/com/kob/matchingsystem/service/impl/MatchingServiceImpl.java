package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    final public static MatchingPool matchingPool =new MatchingPool();
    @Override
    public String addPlayer(Integer userId, Integer rating) {
        matchingPool.addPlayer(userId,rating);
        System.out.println("add"+userId+" "+rating);
        return "add successfully";
    }

    @Override
    public String removePlayer(Integer userId) {
        matchingPool.removePlayer(userId);
        System.out.println("remove"+userId);
        return "remove successfully";
    }
}
