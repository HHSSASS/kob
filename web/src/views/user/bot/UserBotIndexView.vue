<template>
    <ContentField>
        我的bot
    </ContentField>
</template>
   
<script>
import ContentField from "../../../components/ContentField.vue"
import $ from 'jquery'
import { useStore } from "vuex"

export default{
    components:{
        ContentField
    },
    setup(){
        const store=useStore();
        $.ajax({
            url:"http://127.0.0.1:3000/user/bot/add/",
            type:"POST",
            data:{
                title:"HH的Bot",
                description:"",
                content:"HH的代码",
            },
            headers:{
                Authorization:"Bearer "+store.state.user.token,
            },
            success(resp){
                console.log(resp);
            },
            error(resp){
                console.log(resp);
            },
        })
        $.ajax({
            url:"http://127.0.0.1:3000/user/bot/remove/",
            type:"POST",
            data:{
                bot_id:2,
            },
            headers:{
                Authorization:"Bearer "+store.state.user.token,
            },
            success(resp){
                console.log(resp);
            },
            error(resp){
                console.log(resp);
            },
        })
    }
}
</script>

<style scoped>
</style>