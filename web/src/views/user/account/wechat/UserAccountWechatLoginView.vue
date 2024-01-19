<template>
    <ContentField>
        <div style="text-align: center;">
            <div>该功能公测中</div>
            <div>微信扫描左侧二维码关注测试公众号，扫描右侧二维码注册/登录（未出现请刷新）</div>
            <img src="https://cdn.luogu.com.cn/upload/image_hosting/rvfu16mx.png" alt="" width="300">
            <img :src=qr_url alt="" width="300">
            <div @click="password_login" class="password_login">用户名密码登录</div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField.vue"
import { onUnmounted } from 'vue';
import { useStore } from "vuex"
import router from '@/router/index'
import { ref } from "vue"
import $ from "jquery"

export default{
    components:{
        ContentField
    },
    setup(){
        const store=useStore();
        let qr_url=ref("");
        let interval_id;
        let state;
        const apply_qr=()=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/wechat/applyqr/",
                type:"post",
                success(resp){
                    if(resp.message==="successful"){
                        qr_url.value=resp.qr_url;
                        state=resp.state;
                        interval_id=setInterval(login,1000);
                    }
                }
            })
        }
        const login=()=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/wechat/login/",
                type:"get",
                data:{
                    state:state,
                },
                success(resp){
                    if(resp.message==="successful"){
                        clearInterval(interval_id);
                        localStorage.setItem("jwt_token",resp.jwt_token);
                        store.commit("updateToken",resp.jwt_token);
                        router.push({name:"home"});
                    }
                }
            })
        }
        apply_qr();
        const password_login=()=>{
            router.push({name:'user_account_login'});
        }
        onUnmounted(()=>{
            clearInterval(interval_id);
        })
        return{
            qr_url,
            password_login,
        }
    }
}
</script>

<style scoped>
div.password_login{
    color:blue;
    cursor: pointer;
}
</style>