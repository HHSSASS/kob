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
            type:"post",
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
            type:"post",
            data:{
                bot_id:1,
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
            url:"http://127.0.0.1:3000/user/bot/update/",
            type:"post",
            data:{
                bot_id:4,
                title:"HH的Bot2",
                description:"2",
                content:"HH的代码2",
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
            url:"http://127.0.0.1:3000/user/bot/getlist/",
            type:"get",
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