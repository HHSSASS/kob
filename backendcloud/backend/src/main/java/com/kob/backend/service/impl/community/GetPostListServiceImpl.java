package com.kob.backend.service.impl.community;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.CommentMapper;
import com.kob.backend.mapper.LikerecordMapper;
import com.kob.backend.mapper.PostMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Comment;
import com.kob.backend.pojo.Likerecord;
import com.kob.backend.pojo.Post;
import com.kob.backend.pojo.User;
import com.kob.backend.service.community.GetPostListService;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetPostListServiceImpl implements GetPostListService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LikerecordMapper likerecordMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public JSONObject getList(Integer page) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        JSONObject resp=new JSONObject();
        QueryWrapper<Likerecord> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("user_id",user.getId());
        List<Likerecord> likerecords=likerecordMapper.selectList(queryWrapper1);
        resp.put("like_posts",likerecords.stream().map(Likerecord::getPostId).collect(Collectors.toList()));

        IPage<Post> postIPage=new Page<>(page,10);
        QueryWrapper<Post> queryWrapper2=new QueryWrapper<>();
        queryWrapper2.orderByDesc("id");
        List<Post> posts=postMapper.selectPage(postIPage,queryWrapper2).getRecords();
        List<JSONObject> items=new LinkedList<>();
        for(Post post:posts){
            User postuser=userMapper.selectById(post.getUserId());
            QueryWrapper<Likerecord> queryWrapper3=new QueryWrapper<>();
            queryWrapper3.eq("post_id",post.getId());
            QueryWrapper<Comment> queryWrapper4=new QueryWrapper<>();
            queryWrapper4.eq("post_id",post.getId());
            JSONObject item=new JSONObject();
            item.put("post",post);
            item.put("username",postuser.getUsername());
            item.put("photo",postuser.getPhoto());
            item.put("likes_count",likerecordMapper.selectCount(queryWrapper3));
            item.put("comments_count", commentMapper.selectCount(queryWrapper4));
            items.add(item);
        }
        resp.put("posts",items);
        resp.put("posts_count",postMapper.selectCount(null));
        return resp;
    }
}
