<template>
    <div class="matchground">
        <div class="row">
            <div class="col-12">
                <RuleBoard></RuleBoard>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="" :style="proxy.equipment==null?'width:40%':'width:90%'">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select disabled" aria-label="Default select example" :disabled="$store.state.pk.status==='menu'?false:true">
                    <option value="-1" selected>亲自上阵</option>
                    <option v-for="bot in bots" :key="bot.id" :value="bot.id">{{ bot.title }}</option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="" :style="{'width':proxy.equipment==null?'40%':'90%'}">
                </div>
                <div class="user-username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center;padding-top: 10vh;">
                <button @click="click_match_btn" type="button" class="btn btn-info btn-lg" v-if="$store.state.pk.status==='menu'">开始匹配</button>
                <button @click="click_match_btn" type="button" class="btn btn-info btn-lg" v-if="$store.state.pk.status==='matching'">取消</button>
                <button type="button" class="btn btn-info btn-lg" v-if="$store.state.pk.status==='matched'" disabled>匹配成功</button>
            </div>
            <div class="col-12" style="text-align: center;padding-top: 2vh;">
                <button @click="click_pve_btn" type="button" class="btn btn-info btn-lg" :disabled="$store.state.pk.status==='menu'?false:true">人机训练</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import $ from 'jquery';
import RuleBoard from '@/components/RuleBoard.vue'
import { getCurrentInstance } from 'vue'


export default{
    components:{
        RuleBoard,
    },
    setup(){
        const { proxy } = getCurrentInstance();
        const store=useStore();
        let bots=ref([]);
        let select_bot=ref("-1");
        const click_match_btn=()=>{
            if(store.state.pk.status=="menu"){
                store.commit("updateStatus","matching");
                store.state.pk.socket.send(JSON.stringify({
                    event:"start-matching",
                    bot_id:select_bot.value,
                }))
            }
            else if(store.state.pk.status=="matching"){
                store.commit("updateStatus","menu");
                store.state.pk.socket.send(JSON.stringify({
                    event:"stop-matching",
                }))
            }
        }
        const click_pve_btn=()=>{
            store.state.pk.socket.send(JSON.stringify({
                event:"start-pve",
                bot_id:select_bot.value,
            }))
        }
        const refresh_bots=()=>{ 
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/bot/getlist/",
                type:"get",
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    bots.value=resp;
                },
            })
        }
        refresh_bots();
        return{
            click_match_btn,
            click_pve_btn,
            bots,
            select_bot,
            proxy,
        }
    }
}
</script>

<style scoped>
div.matchground{
    width: 100%;
    height: 70vh;
    margin: 40px auto;
    background-color: rgb(50,50,50,0.5);
}
div.user-photo{
    text-align: center;
    padding-top: 10vh;
}
div.user-photo-phone{
    text-align: center;
    padding-top: 10vh;
}
div.user-photo>img{
    border-radius: 50%;
}
div.user-username{
    text-align: center;
    font-size: 2vw;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
}
div.user-select-bot{
    padding-top: 20vh;
}
div.user-select-bot>select{
    width: 60%;
    margin: 0 auto;
}
</style>