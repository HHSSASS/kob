<template>
    <PlayGround v-if="$store.state.pk.status==='playing'"></PlayGround>
    <MatchGround v-else></MatchGround>
    <ResultBoard v-if="$store.state.pk.winner!='none'"></ResultBoard>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'
import { onMounted,onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default{
    components:{
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup(){
        const store=useStore();
        const socketurl=`ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;
        let socket=null;
        onMounted(()=>{
            store.commit("updateOpponent",{
                username:"我的对手",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
            socket=new WebSocket(socketurl);
            socket.onopen=()=>{
                console.log("connected!");
                store.commit("updateSocket",socket);
            }
            socket.onmessage=msg=>{
                const data=JSON.parse(msg.data);
                if(data.event==="start-playing"){
                    store.commit("updateOpponent",{
                        username:data.opponent_username,
                        photo:data.opponent_photo,
                    })
                    setTimeout(()=>{
                        store.commit("updateStatus","playing");
                    },2000);
                    store.commit("updateGame",data.game);
                }else if(data.event==="move"){
                    const game=store.state.pk.gameObject;
                    const [snake0,snake1]=game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                }else if(data.event==="result"){
                    const game=store.state.pk.gameObject;
                    if(game!=null){
                        const [snake0,snake1]=game.snakes;
                        if(data.winner==="all"||data.winner==="B"){
                            snake0.status="die";
                        }
                        if(data.winner==="all"||data.winner==="A"){
                            snake1.status="die";
                        }
                        store.commit("updateWinner",data.winner);
                    }
                }else if(data.event==="heartbeat"){
                    console.log(data);
                     socket.send(JSON.stringify({
                         event:"heartbeat",
                     }))
                }
            }
            socket.onclose=()=>{
                console.log("disconnected!");
            }
        })
        onUnmounted(()=>{
            socket.close();
            store.commit("updateStatus","matching");
        })
    }
}
</script>

<style scoped>
</style>