<template>
    <ContentField>
        <div v-if="access===true">
            <div style="text-align: center;font-size: 50px;">授权登录成功</div>
            &nbsp;
            <div style="text-align: center;font-size: 20px;">即将前往首页</div>
        </div>
        <div v-else-if="access===false">
            <div style="text-align: center;font-size: 50px;">登录失败，请重试或使用其他登录方式</div>
            &nbsp;
            <div style="text-align: center;font-size: 20px;">即将返回登录界面</div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField.vue"
import { useStore } from "vuex";
import { useRoute } from 'vue-router';
import { ref } from "vue";
import $ from 'jquery'
import router from '@/router/index'

export default {
    components:{
        ContentField
    },
    setup() {
        const store=useStore();
        const myRoute = useRoute();
        let access=ref(null);
        $.ajax({
            url: "https://app6418.acapp.acwing.com.cn/api/user/account/qq/applyinfo/",
            type: "post",
            data: {
                code: myRoute.query.code,
                state: myRoute.query.state,
            },
            success(resp){
                if(resp.message==="successful"){
                    access.value=true;
                    setTimeout(()=>{
                        localStorage.setItem("jwt_token",resp.jwt_token);
                        store.commit("updateToken",resp.jwt_token);
                        router.push({name:"home"});
                    },2000);
                }else{
                    access.value=false;
                    setTimeout(()=>{
                        router.push({name:'user_account_login'});
                    },2000);
                }
            },
            error(){
                access.value=false;
                setTimeout(()=>{
                    router.push({name:'user_account_login'});
                },2000);
        }
        })
        return{
            access,
        }
    }
}
</script>

<style scoped>
</style>