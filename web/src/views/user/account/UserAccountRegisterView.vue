<template>
    <ContentField>
        <div class="row">
            <div :class="proxy.equipment==null?'col-3':'col-12'" style="margin: auto;">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">确认密码</label>
                        <input v-model="confirmPassword" type="password" class="form-control" id="confirmPassword" placeholder="请再次输入密码">
                    </div>
                    <div class="error-message">{{message}}</div>
                    <div @click="login" class="login">已有账号？立即登录！</div>
                    <button type="submit" class="btn btn-primary">注册</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from "../../../components/ContentField.vue"
import { ref } from "vue";
import router from '../../../router/index'
import $ from 'jquery'
import { getCurrentInstance } from 'vue'

export default{
    components:{
        ContentField
    },
    setup(){
        const { proxy } = getCurrentInstance();
        let username=ref('');
        let password=ref('');
        let confirmPassword=ref('');
        let message=ref('');
        const register=()=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/register/",
                type:"post",
                data:{
                    username:username.value,
                    password:password.value,
                    confirmPassword:confirmPassword.value,
                },
                success(resp){
                    if(resp.message==="successful"){
                        router.push({name:"user_account_login"});
                    }
                    else{
                        message.value=resp.message;
                    }
                },
            })
        }
        const login=()=>{
            router.push({name:'user_account_login'});
        }
        return{
            username,
            password,
            confirmPassword,
            message,
            register,
            login,
            proxy,
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
div.login{
    color:blue;
    cursor: pointer;
}
</style>