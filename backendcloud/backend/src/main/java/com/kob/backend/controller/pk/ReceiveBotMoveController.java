package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ReceiveBotMoveController {
    @Autowired
    private ReceiveBotMoveService receiveBotMoveService;

    @PostMapping("/pk/receivebotmove/")
    public String receiveBotMove(@RequestBody MultiValueMap<String,String> data){
        String uuid=data.getFirst("uuid");
        Integer userId=Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        Integer direction=Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));
        return receiveBotMoveService.receiveBotMove(uuid,userId,direction);
    }
}
