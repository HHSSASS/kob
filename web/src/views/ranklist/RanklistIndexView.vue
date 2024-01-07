<template>
    <ContentField>
        <table class="table table-hover" style="text-align: center;">
            <thead>
                <tr>
                    <th>玩家</th>
                    <th>天梯分</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in users" :key="user.id">
                    <td>
                        <img :src="user.photo" alt="" class="user-photo">
                        &nbsp;
                        <span class="user-username">{{ user.username }}</span>
                    </td>
                    <td>{{ user.rating }}</td>
                </tr>
            </tbody>
        </table>
        <nav aria-label="Page navigation example">
            <ul class="pagination" style="float: right;">
                <li @click="click_page(-2)" class="page-item"><a class="page-link" href="#">前一页</a></li>
                <li @click="click_page(page.number)" :class="'page-item '+page.is_active" v-for="page in pages" :key="page.number">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>
                <li @click="click_page(-1)" class="page-item"><a class="page-link" href="#">后一页</a></li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from "../../components/ContentField.vue"
import { useStore } from "vuex";
import { ref } from "vue";
import $ from 'jquery';

export default{
    components:{
        ContentField
    },
    setup(){
        const store=useStore();
        let users=ref([]);
        let total_users=0;
        let pages=ref([]);
        let current_page=1;

        const click_page=page=>{
            if(page===-2)page=current_page-1;
            else if(page===-1)page=current_page+1;
            let max_pages=parseInt(Math.ceil(total_users/10));
            if(page>=1&&page<=max_pages){
                pull_page(page);
            }
        }
        const update_pages=()=>{
            let max_pages=parseInt(Math.ceil(total_users/10));
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
                url:"https://app6418.acapp.acwing.com.cn/api/ranklist/getlist/",
                type:"get",
                data:{
                    page,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    users.value=resp.users;
                    total_users=resp.users_count;
                    update_pages();
                },
            })
        }
        pull_page(current_page);
        
        return{
            users,
            total_users,
            pages,
            click_page,
        }
    }
}
</script>

<style scoped>
img.user-photo{
    width: 4vh;
    border-radius: 50%;
}
</style>