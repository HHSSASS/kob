<template>
    <PlayGround v-if="$store.state.record.is_record"></PlayGround>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import { useStore } from 'vuex';
import router from '@/router';

export default{
    components:{
        PlayGround,
    },
    setup(){
        const store=useStore();
        if(!store.state.record.is_record){
            router.push({name:"record_index"});
        }
    }
}
</script>

<style scoped>
</style>