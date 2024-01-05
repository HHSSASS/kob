package com.kob.backend.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordListService;
import net.sf.jsqlparser.expression.JsonAggregateFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        IPage<Record> recordIPage=new Page<>(page,20);
        QueryWrapper<Record> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<Record> records=recordMapper.selectPage(recordIPage,queryWrapper).getRecords();
        JSONObject resp=new JSONObject();
        List<JSONObject> items=new LinkedList<>();
        for(Record record:records){
            JSONObject item=new JSONObject();
            User userA=userMapper.selectById(record.getAId());
            item.put("a_photo",userA.getPhoto());
            item.put("a_username",userA.getUsername());
            if(record.getBId()==0){
                item.put("b_photo","https://cdn.luogu.com.cn/upload/image_hosting/sk5lwdp3.png");
                item.put("b_username","人机训练");
            }else{
                User userB=userMapper.selectById(record.getBId());
                item.put("b_photo",userB.getPhoto());
                item.put("b_username",userB.getUsername());
            }
            String result="平局";
            if("A".equals(record.getWinner())) result="A胜";
            else if("B".equals(record.getWinner())) result="B胜";
            item.put("result",result);
            item.put("record",record);
            items.add(item);
        }
        resp.put("records",items);
        resp.put("records_count",recordMapper.selectCount(null));
        return resp;
    }
}
