<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{message}}</div>
                    <div @click="register" class="register">还没有账号？立即注册！</div>
                    <button type="submit" class="btn btn-primary">登录</button>
                </form>
                <div class="oauth">
                    <div style="padding-top: 10px;">一键注册登录:</div>
                    &nbsp;
                    <i @click="phone_login" class="iconfont icon-hongseshouji-X" style="cursor: pointer;font-size:35px;color: rgb(31, 92, 190);"></i>
                    &nbsp;
                    <div style="cursor: pointer;padding-top: 12px;">
                        <img @click="wechat_login" width="30" src="https://cdn.luogu.com.cn/upload/image_hosting/jvmtthfg.png" alt="">
                    </div>
                    &nbsp;
                    <div style="cursor: pointer;padding-top: 12px;">
                        <img @click="qq_login" width="28" src="https://cdn.luogu.com.cn/upload/image_hosting/8vl3e7h4.png" alt="">
                    </div>
                </div>
            </div>
        </div>
    </ContentField>
</template>

<script>
import '@/assets/iconfont/iconfont.css'
import ContentField from "../../../components/ContentField.vue"
import { useStore } from "vuex";
import { ref } from "vue";
import router from '../../../router/index'
import $ from "jquery";

export default{
    components:{
        ContentField
    },
    setup(){
        const store=useStore();
        let username=ref('');
        let password=ref('');
        let message=ref('');
        const login=()=>{
            message.value="";
            store.dispatch("login",{
                username:username.value,
                password:password.value,
                success(){
                    store.dispatch("getinfo",{
                        success(){
                            router.push({name:'home'});
                        }
                    })
                },
                error(){
                    message.value="用户名或密码错误";
                }
             })
        }
        const register=()=>{
            router.push({name:'user_account_register'});
        }
        const phone_login=()=>{
            router.push({name:'user_account_phone_login'});
        }
        const wechat_login=()=>{
            router.push({name:'user_account_wechat_login'});
        }
        const qq_login=()=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/qq/applyurl/",
                type:"post",
                success(resp){
                    if(resp.message==="successful"){
                        window.location.replace(resp.apply_url);
                    }
                }
            })
            //router.push({name:'user_account_qq_login'});
        }
        return{
            username,
            password,
            message,
            login,
            register,
            phone_login,
            wechat_login,
            qq_login,
        }
    }
}
</script>

<style scoped>
button{
    width:100%;
}
div.error-message{
    color:red;
}
div.register{
    color:blue;
    cursor: pointer;
}
div.oauth{
    display: flex;
    text-align: center; 
    margin-top: 20px; 
}
</style>