<template>
    <ContentField>
        <div class="table-responsive">
            <table class="table table-hover" style="text-align: center;">
                <thead>
                    <tr>
                        <th>A</th>
                        <th>B</th>
                        <th>结果</th>
                        <th>对战时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="record in records" :key="record.record.id">
                        <td>
                            <img :src="record.a_photo" alt="" class="record-user-photo">
                            &nbsp;
                            <span class="record-user-username">{{ record.a_username }}</span>
                        </td>
                        <td>
                            <img :src="record.b_photo" alt="" class="record-user-photo">
                            &nbsp;
                            <span class="record-user-username">{{ record.b_username }}</span>
                        </td>
                        <td>{{ record.result }}</td>
                        <td>
                            <span>{{ record.record.createtime.slice(0,10) }}</span>
                            <br>
                            <span>{{ record.record.createtime.slice(11) }}</span>
                        </td>
                        <td>
                            <button @click="open_record_content(record.record.id)" type="button" class="btn btn-secondary">查看录像</button>                                                
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <nav aria-label="Page navigation example">
            <ul class="pagination" style="float: right;">
                <li @click="click_page(-2)" class="page-item"><a class="page-link" href="#">首页</a></li>
                <li @click="click_page(page.number)" :class="'page-item '+page.is_active" v-for="page in pages" :key="page.number">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>
                <li @click="click_page(-1)" class="page-item"><a class="page-link" href="#">尾页</a></li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from "../../components/ContentField.vue"
import { useStore } from "vuex";
import { ref } from "vue";
import $ from 'jquery';
import router from "@/router/index";

export default{
    components:{
        ContentField
    },
    setup(){
        const store=useStore();
        let records=ref([]);
        let total_records=0;
        let pages=ref([]);
        let current_page=1;

        const click_page=page=>{
            let max_pages=parseInt(Math.ceil(total_records/20));
            if(page===-2)page=1;
            else if(page===-1)page=max_pages;
            if(page>=1&&page<=max_pages){
                pull_page(page);
            }
        }
        const update_pages=()=>{
            let max_pages=parseInt(Math.ceil(total_records/20));
            let new_pages=[];
            for(let i=current_page-2;i<=current_page+2;++i){
                if(i>=1&&i<=max_pages){
                    new_pages.push({
                        number:i,
                        is_active:i===current_page?"active":"",
                    });
                }
            }
            pages.value=new_pages;
        }
        const pull_page=page=>{
            current_page=page;
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/record/getlist/",
                type:"get",
                data:{
                    page,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    records.value=resp.records;
                    total_records=resp.records_count;
                    update_pages();
                },
            })
        }
        pull_page(current_page);
        
        const stringTo2D=map=>{
            let g=[];
            for(let i=0,k=0;i<13;++i){
                let line=[];
                for(let j=0;j<14;++j,++k){
                    if(map[k]==='0')line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }

        const open_record_content=recordId=>{
            for(const record of records.value){
                if(record.record.id===recordId){
                    store.commit("updateIsRecord",true);
                    store.commit("updateSteps",{
                        a_steps:record.record.asteps,
                        b_steps:record.record.bsteps,
                    });
                    store.commit("updateRecordWinner",record.record.winner);
                    store.commit("updateGame",{
                        map:stringTo2D(record.record.map),
                        a_id:record.record.aid,
                        a_sx:record.record.asx,
                        a_sy:record.record.asy,
                        b_id:record.record.bid,
                        b_sx:record.record.bsx,
                        b_sy:record.record.bsy,
                    });
                    router.push({
                        name:"record_content",
                        params:{
                            recordId:recordId,
                        }
                    })
                    break;
                }
            }
        }
        return{
            records,
            total_records,
            pages,
            open_record_content,
            click_page,
        }
    }
}
</script>

<style scoped>
div.table-responsive>table>thead>tr>th {
    white-space: nowrap;
}
div.table-responsive>table>tbody>tr>td {
    white-space: nowrap;
}
img.record-user-photo{
    width: 4vh;
    border-radius: 50%;
}
</style>