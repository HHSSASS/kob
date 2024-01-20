<template>
    <div class="container" style="max-width: 800px;">
        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add">发布新帖</button>
        <div class="modal fade" id="add" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5" id="exampleModalLabel">发布新帖</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="exampleFormControlTextarea1" class="form-label">内容</label>
                            <textarea v-model="postadd.content" class="form-control" id="exampleFormControlTextarea1" rows="8" placeholder="请输入内容"></textarea>
                            <div style="float: right;">字数：{{ postadd.content.length }}/500</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" style="float: right">取消</button>
                        <button type="button" class="btn btn-primary" @click="add_post" style="float: right;">发布</button>
                        <div class="error-message" style="float: right">{{ postadd.message }}</div>
                    </div>
                </div>
            </div>
        </div>
        <br><br>
        <div class="card-list">
            <div class="card" v-for="post in posts" :key="post.post.id" style="margin-bottom: 20px;">
                <div class="card-header">
                    <img :src="post.photo" alt="" class="post-photo">
                    &nbsp;
                    <span class="post-username">{{ post.username }}</span>
                    <span style="float: right;">{{ post.post.createtime }}</span>
                </div>
                <div class="card-body">
                    <div>{{ post.post.content }}</div>
                </div>
                <div class="card-footer">
                    <div class="row" style="text-align: center;">
                        <div class="col-4">
                            <span @click="remove_like(post)" class="iconfont icon-aixin" style="color: red;cursor: pointer;" v-if="like_posts.indexOf(post.post.id)!=-1"></span>
                            <span @click="add_like(post)" class="iconfont icon-aixinD" style="cursor: pointer;" v-else></span>
                            &nbsp;
                            <span>{{ post.likes_count }}</span>
                        </div>
                        <div class="col-4">
                            <span @click="pull_comment_page(post,1)" class="iconfont icon-pinglun" style="cursor: pointer;" data-bs-toggle="modal" data-bs-target="#comment" ></span>
                            
                            <div class="modal fade" id="comment" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog modal-lg modal-dialog-scrollable">
                                    <div class="modal-content" style="text-align: left;">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel">评论</h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="card-list">
                                                <div class="card" v-for="comment in comments" :key="comment.comment.id" style="margin-bottom: 20px;">
                                                    <div class="card-header">
                                                        <img :src="comment.photo" alt="" class="comment-photo">
                                                        &nbsp;
                                                        <span class="comment-username">{{ comment.username }}</span>
                                                        &nbsp;
                                                        <span @click="remove_comment(comment)" class="iconfont icon-shanchu" style="cursor: pointer;" v-if="parseInt(comment.comment.userId)===parseInt($store.state.user.id)"></span>
                                                        <span style="float: right;">{{ comment.comment.createtime }}</span>
                                                    </div>
                                                    <div class="card-body">
                                                        <div>{{ comment.comment.content }}</div>
                                                    </div>
                                                </div>
                                            </div>
                                            <nav aria-label="Page navigation example">
                                                <ul class="pagination" style="float: right;">
                                                    <li @click="click_comment_page(-2)" class="page-item"><a class="page-link" href="#">首页</a></li>
                                                    <li @click="click_comment_page(comment_page.number)" :class="'page-item '+comment_page.is_active" v-for="comment_page in comment_pages" :key="comment_page.number">
                                                        <a class="page-link" href="#">{{ comment_page.number }}</a>
                                                    </li>
                                                    <li @click="click_comment_page(-1)" class="page-item"><a class="page-link" href="#">尾页</a></li>
                                                </ul>
                                            </nav>          
                                        </div>
                                        <div class="modal-footer">
                                            <label for="exampleFormControlTextarea1" class="form-label" style="padding-top: 5px;">发布评论</label>
                                            <textarea v-model="commentadd.content" class="form-control" id="exampleFormControlTextarea1" rows="6" placeholder="请输入内容"></textarea>
                                            <div style="float: right;">字数：{{ commentadd.content.length }}/500</div>
                                            <br>
                                            <br>
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" style="float: right;">取消</button>
                                            <button type="button" class="btn btn-primary" @click="add_comment()" style="float: right;">发布</button>
                                            <div class="error-message" style="float: right;">{{ commentadd.message }}</div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            &nbsp;
                            <span>{{ post.comments_count }}</span>
                        </div>
                        <div class="col-4">
                            <span @click="remove_post(post)" class="iconfont icon-shanchu" style="cursor: pointer;" v-if="parseInt(post.post.userId)===parseInt($store.state.user.id)"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <nav aria-label="Page navigation example">
            <ul class="pagination" style="float: right;">
                <li @click="click_page(-2)" class="page-item"><a class="page-link" href="#">首页</a></li>
                <li @click="click_page(page.number)" :class="'page-item '+page.is_active" v-for="page in pages" :key="page.number">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>
                <li @click="click_page(-1)" class="page-item"><a class="page-link" href="#">尾页</a></li>
            </ul>
        </nav>
    </div>
</template>

<script>
import '../../assets/iconfont/iconfont.css'
import $ from 'jquery';
import { useStore } from "vuex";
import { ref,reactive } from "vue";
import { Modal } from 'bootstrap/dist/js/bootstrap'

export default{
    components:{
    },
    setup(){
        const store=useStore();
        let posts=ref([]);
        let comments=ref([]);
        let total_posts=0;
        let total_comments=0;
        let pages=ref([]);
        let comment_pages=ref([]);
        let current_page=1;
        let current_comment_page=1;
        let now_post;
        let like_posts=ref([]);//已点赞的post列表
        const postadd=reactive({
            content:"",
            message:"",
        });
        const commentadd=reactive({
            content:"",
            message:"",
        });

        const click_page=page=>{
            let max_pages=parseInt(Math.ceil(total_posts/10));
            if(page===-2)page=1;
            else if(page===-1)page=max_pages;
            if(page>=1&&page<=max_pages){
                pull_page(page);
            }
        }
        const update_pages=()=>{
            let max_pages=parseInt(Math.ceil(total_posts/10));
            let new_pages=[];
            for(let i=current_page-2;i<=current_page+2;++i){
                if(i>=1&&i<=max_pages){
                    new_pages.push({
                        number:i,
                        is_active:i===current_page?"active":"",
                    });
                }
            }
            pages.value=new_pages;
        }
        const pull_page=page=>{
            current_page=page;
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/getlist/",
                type:"get",
                data:{
                    page,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    posts.value=resp.posts;
                    total_posts=resp.posts_count;
                    like_posts.value=resp.like_posts;
                    update_pages();
                },
            })
        }
        pull_page(current_page);
        const add_post=()=>{
            postadd.message="";
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/add/",
                type:"post",
                data:{
                    content:postadd.content,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.message==="successful"){
                        postadd.content="";
                        Modal.getInstance("#add").hide();
                        current_page=1;
                        pull_page(current_page);
                    }
                    else{
                        postadd.message=resp.message;
                    }
                },
            })
        }
        const remove_post=(post)=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/remove/",
                type:"post",
                data:{
                    post_id:post.post.id,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(){
                    current_page=1;
                    pull_page(current_page);
                },
            })
        }
        const click_comment_page=(comment_page)=>{
            let max_comment_pages=parseInt(Math.ceil(total_comments/5));
            if(comment_page===-2)comment_page=1;
            else if(comment_page===-1)comment_page=max_comment_pages;
            if(comment_page>=1&&comment_page<=max_comment_pages){
                pull_comment_page(now_post,comment_page);
            }
        }
        const update_comment_pages=()=>{
            let max_comment_pages=parseInt(Math.ceil(total_comments/5));
            let new_comment_pages=[];
            for(let i=current_comment_page-2;i<=current_comment_page+2;++i){
                if(i>=1&&i<=max_comment_pages){
                    new_comment_pages.push({
                        number:i,
                        is_active:i===current_comment_page?"active":"",
                    });
                }
            }
            comment_pages.value=new_comment_pages;
        }
        const pull_comment_page=(post,comment_page)=>{
            now_post=post;
            current_comment_page=comment_page;
            commentadd.message="";
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/comment/getlist/",
                type:"get",
                data:{
                    post_id:post.post.id,
                    page:comment_page,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    comments.value=resp.comments;
                    total_comments=resp.comments_count;
                    now_post.comments_count=total_comments;
                    update_comment_pages();
                },
            })
        }
        const add_comment=()=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/comment/add/",
                type:"post",
                data:{
                    post_id:now_post.post.id,
                    content:commentadd.content,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.message==="successful"){
                        commentadd.content="";
                        current_comment_page=1;
                        pull_comment_page(now_post,current_comment_page);
                        now_post.comments_count++;
                    }
                    else{
                        commentadd.message=resp.message;
                    }
                },
            })
        }
        const remove_comment=(comment)=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/comment/remove/",
                type:"post",
                data:{
                    comment_id:comment.comment.id,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(){
                    current_comment_page=1;
                    pull_comment_page(now_post,current_comment_page);
                    now_post.comments_count--;
                },
            })
        }
        const remove_like=(post)=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/likerecord/remove/",
                type:"post",
                data:{
                    post_id:post.post.id,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.message=="successful"){
                        like_posts.value.splice(like_posts.value.indexOf(post.post.id),1);
                        post.likes_count--;
                    }
                },
            })
        }
        const add_like=(post)=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/community/likerecord/add/",
                type:"post",
                data:{
                    post_id:post.post.id,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.message=="successful"){
                        like_posts.value.push(post.post.id);
                        post.likes_count++;
                    }
                },
            })
        }
        return{
            posts,
            total_posts,
            pages,
            postadd,
            comments,
            total_comments,
            comment_pages,
            commentadd,
            like_posts,
            click_page,
            add_post,
            remove_post,
            click_comment_page,
            pull_comment_page,
            add_comment,
            remove_comment,
            add_like,
            remove_like,
        }
    }
}
</script>

<style scoped>
img.post-photo{
    width: 4vh;
    border-radius: 50%;
}
img.comment-photo{
    width: 4vh;
    border-radius: 50%;
}
div.error-message{
    color:red;
}
div.modal-footer{
    display: block;
}
</style>