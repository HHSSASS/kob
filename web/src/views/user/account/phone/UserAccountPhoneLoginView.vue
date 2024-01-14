<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="phone_number" class="form-label">手机号码</label>
                        <input v-model="phone_number" type="text" class="form-control" id="phone_number" placeholder="请输入手机号码">
                    </div>
                    <label for="verification_code" class="form-label">验证码</label>
                    <div class="input-group mb-3">
                        <input v-model="verification_code" type="text" class="form-control" id="verification_code" placeholder="请输入验证码">
                        <button @click="apply_code" class="btn btn-outline-secondary" type="button" id="button-addon">获取验证码</button>
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
import router from '@/router/index'

export default{
    components:{
        ContentField
    },
    setup(){
        let phone_number=ref('');
        let verification_code=ref('');
        let message=ref('');
        const apply_code=()=>{

        }
        const submit=()=>{

        }
        const password_login=()=>{
            router.push({name:'user_account_login'});
        }
        return{
            phone_number,
            verification_code,
            message,
            apply_code,
            submit,
            password_login,
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