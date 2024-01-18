package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(String uuid,Integer userId, Integer direction) {
        //System.out.println("receive bot move "+userId+" "+direction);
        if(direction<0||direction>3){
            return "direction error";
        }
        if(WebSocketServer.users.get(userId)!=null&&uuid.equals(WebSocketServer.users.get(userId).game.uuid)){
            Game game=WebSocketServer.users.get(userId).game;
            if(game!=null){
                if(game.getPlayerA().getId().equals(userId)){
                    game.setNextStepA(direction);
                } else if(game.getPlayerB().getId().equals(userId)){
                    game.setNextStepB(direction);
                }
            }
        }
        return "receive bot move successfully";
    }
}
