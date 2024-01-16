package com.kob.backend.service.impl.user.info;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.info.UpdatePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class UpdatePhotoServiceImpl implements UpdatePhotoService {
    @Autowired
    UserMapper userMapper;

    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject update(MultipartFile file) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String path="/home/hh/kob/images/photo/"+user.getId().toString()+".jpeg";
        String photo="https://app6418.acapp.acwing.com.cn/images/photo/"+user.getId().toString()+".jpeg";
        File toFile=new File(path);
        if (toFile.exists()) {
            toFile.delete();
        }
        try {
            inputStreamToFile(file.getInputStream(),toFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        User new_user=new User(user.getId(),user.getUsername(),photo,user.getPassword(),user.getPhoneNumber(),user.getWechatOpenid(),user.getQqOpenid(),user.getRating());
        userMapper.updateById(new_user);
        JSONObject resp=new JSONObject();
        resp.put("message","successful");
        resp.put("photo",photo);
        return resp;
    }
}
