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
        let interval_id;
        store.commit("updateWinner","none");
        store.commit("updateIsBot",false);
        store.commit("updateIsRecord",false);
        const start_counter=()=>{
            store.commit("updateCounter",5);
            interval_id=setInterval(()=>{
                let time=store.state.pk.counter;
                if(time>5) clearInterval(interval_id);
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
                }else if(data.event==="move"){
                    const game=store.state.pk.gameObject;
                    const [snake0,snake1]=game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                    clearInterval(interval_id);
                    start_counter();
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
                        store.commit("updateRating",{
                            a_rating:data.a_rating,
                            b_rating:data.b_rating,
                        })
                    }
                    store.commit("updateCounter",10000);
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
            store.commit("updateStatus","menu");
        })
    }
}
</script>

<style scoped>
</style>