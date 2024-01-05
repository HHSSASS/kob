<template>
    <div class="result-board">
        <div class="result-board-text" v-if="$store.state.pk.winner==='all'&&$store.state.pk.a_id==$store.state.user.id">
            <div>Draw</div>
            <div>天梯分：{{ $store.state.pk.a_rating }}</div>
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.winner==='all'&&$store.state.pk.b_id==$store.state.user.id">
            <div>Draw</div>
            <div>天梯分：{{ $store.state.pk.b_rating }}</div>
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.winner==='A'&&$store.state.pk.a_id==$store.state.user.id">
            <div>Win</div>
            <div>天梯分：{{ $store.state.pk.a_rating }}</div>
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.winner==='B'&&$store.state.pk.b_id==$store.state.user.id">
            <div>Win</div>
            <div>天梯分：{{ $store.state.pk.b_rating }}</div>
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.winner==='B'&&$store.state.pk.a_id==$store.state.user.id">
            <div>Lose</div>
            <div>天梯分：{{ $store.state.pk.a_rating }}</div>
        </div>
        <div class="result-board-text" v-else>
            <div>Lose</div>
            <div>天梯分：{{ $store.state.pk.b_rating }}</div>
        </div>
        <div class="result-board-btn">
            <button @click="restart" type="button" class="btn btn-info btn-lg">再来一局</button>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';

export default{
    setup(){
        const store=useStore();
        const restart=()=>{
            store.commit("updateStatus","menu");
            store.commit("updateWinner","none");
            store.commit("updateOpponent",{
                username:"我的对手",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
        }
        return{
            restart,
        }
    }
}
</script>

<style scoped>
div.result-board{
    height: 30vh;
    width: 30vw;
    background-color: rgba(50,50,50,0.5);
    position: absolute;
    top: 30vh;
    left: 35vw;
}
div.result-board-text{
    text-align: center;
    color:white;
    font-size: 40px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
}
div.result-board-btn{
    text-align: center;
    padding-top: 4vh;
}
</style>