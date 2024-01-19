
<template>
    <div class="back-btn" v-if="$store.state.record.is_record">
        <button @click="backlist" type="button" class="btn btn-info btn-lg">返回对战列表</button>
    </div>
    <div v-else>
        <div class="user-position" v-if="parseInt($store.state.user.id)===parseInt($store.state.pk.a_id)">您在左下角</div>
        <div class="user-position" v-else-if="parseInt($store.state.user.id)===parseInt($store.state.pk.b_id)">您在右上角</div>
        <div class="counter">
            <div v-if="$store.state.pk.winner!='none'||$store.state.pk.counter<0">对战结束</div>
            <div v-else-if="$store.state.pk.counter>5">等待对手</div>
            <div v-else>{{$store.state.pk.counter}}</div>
        </div>
    </div>
    <div class="playground">
        <GameMap></GameMap>
    </div>
    <div style="text-align: center;">

    </div>
    <div style="text-align: center;" v-if="!$store.state.record.is_record">
        <i @click="move_up" class="iconfont icon-shangxiazuoyouTriangle17 move"></i>
        <br>
        <i @click="move_left" class="iconfont icon-shangxiazuoyouTriangle18 move"></i>
        &nbsp;&nbsp;
        <i @click="move_down" class="iconfont icon-shangxiazuoyouTriangle15 move"></i>
        &nbsp;&nbsp;
        <i @click="move_right" class="iconfont icon-shangxiazuoyouTriangle16 move"></i>
    </div>
</template>

<script>
import GameMap from './GameMap.vue'
import router from "@/router/index"
import { useStore } from 'vuex'
import '@/assets/iconfont/iconfont.css'

export default{
    components:{
        GameMap,
    },
    setup(){
        const store=useStore();
        const backlist=()=>{
            router.push({
                name:"record_index",
            })
        }
        const move_up=()=>{
            if(store.state.pk.winner==="none"){
                store.state.pk.socket.send(JSON.stringify({
                    event:"move",
                    direction:0,
                }))
            }
        }
        const move_right=()=>{
            if(store.state.pk.winner==="none"){
                store.state.pk.socket.send(JSON.stringify({
                    event:"move",
                    direction:1,
                }))
            }
        }
        const move_down=()=>{
            if(store.state.pk.winner==="none"){
                store.state.pk.socket.send(JSON.stringify({
                    event:"move",
                    direction:2,
                }))
            }
        }
        const move_left=()=>{
            if(store.state.pk.winner==="none"){
                store.state.pk.socket.send(JSON.stringify({
                    event:"move",
                    direction:3,
                }))
            }
        }
        return{
            backlist,
            move_up,
            move_right,
            move_down,
            move_left,
        }
    }
}
</script>

<style scoped>
div.playground{
    width: 100vw;
    height: 60vh;
    margin: auto;
}
div.back-btn{
    text-align: center;
    padding-top: 2vh;
}
div.user-position{
    text-align: center;
    color: black;
    font-size: 25px;
    font-weight: 600;
}
div.counter{
    text-align: center;
    color: black;
    font-size: 25px;
    font-weight: 600;
}
.move{
    cursor: pointer;
    font-size: 50px;
    color: white;
}
</style>