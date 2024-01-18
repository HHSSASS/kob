<template>
    <RuleBoard></RuleBoard>
    <PlayGround v-if="$store.state.pk.status==='playing'"></PlayGround>
    <MatchGround v-else></MatchGround>
    <ResultBoard v-if="$store.state.pk.winner!='none'"></ResultBoard>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'
import RuleBoard from '../../components/RuleBoard.vue'
import { onMounted,onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default{
    components:{
        PlayGround,
        MatchGround,
        ResultBoard,
        RuleBoard,
    },
    setup(){
        const store=useStore();
        const socketurl=`wss://app6418.acapp.acwing.com.cn/websocket/${store.state.user.token}/`;
        let socket=null;
        let interval_id;
        let heartbeat_id;
        store.commit("updateWinner","none");
        store.commit("updateIsRecord",false);
        const start_counter=()=>{
            store.commit("updateCounter",5);
            interval_id=setInterval(()=>{
                let time=store.state.pk.counter;
                time--;
                store.commit("updateCounter",time);
            },1000)
        }
        onMounted(()=>{
            store.commit("updateOpponent",{
                username:"我的对手",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
            socket=new WebSocket(socketurl);
            socket.onopen=()=>{
                console.log("connected!");
                store.commit("updateSocket",socket);
                heartbeat_id=setInterval(()=>{
                    socket.send(JSON.stringify({
                         event:"heartbeat",
                     }));
                },1000);
            }
            socket.onmessage=msg=>{
                const data=JSON.parse(msg.data);
                if(data.event==="start-playing"){
                    store.commit("updateOpponent",{
                        username:data.opponent_username,
                        photo:data.opponent_photo,
                    })
                    store.commit("updateStatus","matched");
                    store.commit("updateGame",data.game);
                    setTimeout(()=>{
                        store.commit("updateStatus","playing");
                        start_counter();
                    },2000);
                }else if(data.event==="start-pve"){
                    store.commit("updateGame",data.game);
                    store.commit("updateStatus","playing");
                    start_counter();
                }else if(data.event==="receivemove"){
                    if(data.uuid===store.state.pk.uuid){
                        clearInterval(interval_id);
                        store.commit("updateCounter",10000);
                    }
                }else if(data.event==="move"){
                    if(data.uuid===store.state.pk.uuid){
                        const game=store.state.pk.gameObject;
                        const [snake0,snake1]=game.snakes;
                        snake0.set_direction(data.a_direction);
                        snake1.set_direction(data.b_direction);
                        clearInterval(interval_id);
                        start_counter();
                    }
                }else if(data.event==="result"){
                    if(data.uuid===store.state.pk.uuid){
                        const game=store.state.pk.gameObject;
                        const [snake0,snake1]=game.snakes;
                        if(data.winner==="all"||data.winner==="B"){
                            snake0.status="die";
                        }
                        if(data.winner==="all"||data.winner==="A"){
                            snake1.status="die";
                        }
                        store.commit("updateWinner",data.winner);
                        store.commit("updateRating",{
                            a_rating:data.a_rating,
                            b_rating:data.b_rating,
                        })
                        clearInterval(interval_id);
                    }
                }else if(data.event==="heartbeat"){
                    //console.log("heartbeat");
                }
            }
            socket.onclose=()=>{
                console.log("disconnected!");
                clearInterval(heartbeat_id);
            }
        })
        onUnmounted(()=>{
            socket.close();
            store.commit("updateStatus","menu");
            store.commit("updateUuid","");
            clearInterval(interval_id);
        })
    }
}
</script>

<style scoped>
</style>