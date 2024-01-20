<template>
    <ContentField>
        <div class="row">
            <div :class="proxy.equipment==null?'col-3':'col-12'" style="margin: auto;">
                <form @submit.prevent="login">
                    <div style="text-align: center;">该功能内测中，仅支持内测手机号码</div>
                    &nbsp;
                    <div class="mb-3">
                        <label for="phone_number" class="form-label">手机号码</label>
                        <input v-model="phone_number" type="text" class="form-control" id="phone_number" placeholder="请输入手机号码">
                    </div>
                    <label for="verification_code" class="form-label">验证码</label>
                    <div class="input-group mb-3">
                        <input v-model="verification_code" type="text" class="form-control" id="verification_code" placeholder="请输入验证码">
                        <button @click="apply_code" class="btn btn-outline-secondary" type="button" id="button-addon" :disabled="counter>=0">{{counter<=0?'获取验证码':counter+'s后重新获取'}}</button>
                    </div>
                    <div class="error-message">{{message}}</div>
                    <div @click="password_login" class="password_login">用户名密码登录</div>
                    <button type="submit" class="btn btn-primary submit">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField.vue"
import { ref } from "vue";
import { useStore } from "vuex";
import { onUnmounted } from "vue";
import router from '@/router/index'
import $ from "jquery"
import { getCurrentInstance } from 'vue'

export default{
    components:{
        ContentField
    },
    setup(){
        const { proxy } = getCurrentInstance();
        const store=useStore();
        let phone_number=ref('');
        let verification_code=ref('');
        let message=ref('');
        let counter=ref(-1);
        let interval_id;
        const apply_code=()=>{
            message.value='';
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/phone/applycode/",
                type:"post",
                data:{
                    number:phone_number.value,
                },
                success(resp){
                    if(resp.message==="successful"){
                        counter.value=60;
                        interval_id=setInterval(()=>{
                            counter.value--;
                            if(counter.value<0) clearInterval(interval_id);
                        },1000)
                    }
                    else{
                        message.value=resp.message;
                    }
                },
            })
        }
        const login=()=>{
            message.value='';
            counter.value=0;
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/phone/login/",
                type:"post",
                data:{
                    number:phone_number.value,
                    code:verification_code.value,
                },
                success(resp){
                    if(resp.message==="successful"){
                        localStorage.setItem("jwt_token",resp.jwt_token);
                        store.commit("updateToken",resp.jwt_token);
                        router.push({name:"home"});
                    }
                    else{
                        message.value=resp.message;
                        counter.value=-1;
                    }
                },
            })
        }
        const password_login=()=>{
            router.push({name:'user_account_login'});
        }
        onUnmounted(()=>{
            clearInterval(interval_id);
        })
        return{
            phone_number,
            verification_code,
            message,
            counter,
            apply_code,
            login,
            password_login,
            proxy,
        }
    }
}
</script>

<style scoped>
button.submit{
    width:100%;
}
div.error-message{
    color:red;
}
div.password_login{
    color:blue;
    cursor: pointer;
}
</style>