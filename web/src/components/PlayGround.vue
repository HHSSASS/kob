
<template>
    <div class="content">
        <div>
            <div class="user-position" style="text-align: center;" v-if="$store.state.record.is_record">
                <button @click="backlist" type="button" class="btn btn-info btn-lg">返回对战列表</button>
            </div>
            <div class="user-position" v-else-if="parseInt($store.state.user.id)===parseInt($store.state.pk.a_id)">您在左下角</div>
            <div class="user-position" v-else-if="parseInt($store.state.user.id)===parseInt($store.state.pk.b_id)">您在右上角</div>
            <div class="counter" v-if="!$store.state.record.is_record&&$store.state.pk.winner!='none'">对战结束</div>
            <div class="counter" v-else-if="!$store.state.record.is_record&&$store.state.pk.is_bot||$store.state.pk.counter>5">等待对手</div>
            <div class="counter" v-else-if="!$store.state.record.is_record">{{$store.state.pk.counter}}</div>
        </div>
        <div class="playground">
            <GameMap></GameMap>
        </div>
    </div>
</template>

<script>
import GameMap from './GameMap.vue'
import router from "@/router/index"

export default{
    components:{
        GameMap,
    },
    setup(){
        const backlist=()=>{
            router.push({
                name:"record_index",
            })
        }
        return{
            backlist,
        }
    }
}
</script>

<style scoped>
div.content{
    display: flex;
}
div.playground{
    width:60vw;
    height: 70vh;
    margin: 40px 100px 40px 40px;
}
div.user-position{
    text-align: center;
    color: black;
    font-size: 30px;
    font-weight: 600;
    padding-top: 200px;
    padding-left: 150px;
}
div.counter{
    text-align: center;
    color: black;
    font-size: 30px;
    font-weight: 600;
    padding-top: 20px;
    padding-left: 150px;
}
</style>