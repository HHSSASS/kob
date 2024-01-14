<template>
    <ContentField>
        <div style="text-align: center;">
            <div>该功能测试中</div>
            <div>微信扫描二维码，关注测试公众号后注册登录（二维码等待数秒后未出现请刷新页面）</div>
            <img :src=qr_url alt="" width="300">
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
        let scenestr;
        const apply_qr=()=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/wechat/applyqr/",
                type:"get",
                success(resp){
                    if(resp.message==="successful"){
                        qr_url.value="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+resp.ticket;
                        scenestr=resp.scenestr;
                        interval_id=setInterval(apply_info,1000);
                    }
                }
            })
        }
        const apply_info=()=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/account/wechat/applyinfo/",
                type:"get",
                data:{
                    scenestr:scenestr,
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
        onUnmounted(()=>{
            clearInterval(interval_id);
        })
        return{
            qr_url,
        }
    }
}
</script>

<style scoped>
</style>