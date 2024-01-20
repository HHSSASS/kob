<template>
    <Cropper v-if="show"></Cropper>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div  class="card" style="margin-top: 20px;">
                    <div @mouseover="show_update_photo" @mouseleave="hide_update_photo"  class="card-body">
                        <div @click="update_photo" class="update_photo" v-show="show_update">
                            <div class="update_photo_text">修改头像</div>
                        </div>
                        <img :src="$store.state.user.photo" alt="" style="width:100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top:20px;">
                    <div class="card-header">
                        <span style="font-size:130%">我的Bot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add">创建Bot</button>
                        <div class="modal fade" id="add" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title fs-5" id="exampleModalLabel">创建Bot</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="exampleFormControlInput1" class="form-label">名称</label>
                                            <input v-model="botadd.title" type="text" class="form-control" id="exampleFormControlInput1" placeholder="请输入Bot名称">
                                        </div>
                                        <div class="mb-3">
                                            <label for="exampleFormControlTextarea1" class="form-label">简介</label>
                                            <textarea v-model="botadd.description" class="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="请输入Bot简介"></textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="exampleFormControlTextarea1" class="form-label">代码</label>
                                            <VAceEditor v-model:value="botadd.content" @init="editorInit" lang="c_cpp" theme="textmate" style="height: 300px"/>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error-message">{{ botadd.message }}</div>
                                        <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>名称</th>
                                        <th>创建时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="bot in bots" :key="bot.id">
                                        <td>{{ bot.title }}</td>
                                        <td>
                                            <span>{{ bot.createtime.slice(0,10) }}</span>
                                            <br>
                                            <span>{{ bot.createtime.slice(11) }}</span>
                                        </td>
                                        <td>
                                            <button type="button" class="btn btn-secondary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update'+bot.id">修改</button>
                                            <div class="modal fade" :id="'update'+bot.id" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                                <div class="modal-dialog modal-lg">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h1 class="modal-title fs-5" id="exampleModalLabel">修改Bot</h1>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="mb-3">
                                                                <label for="exampleFormControlInput1" class="form-label">名称</label>
                                                                <input v-model="bot.title" type="text" class="form-control" id="exampleFormControlInput1" placeholder="请输入Bot名称">
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="exampleFormControlTextarea1" class="form-label">简介</label>
                                                                <textarea v-model="bot.description" class="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="请输入Bot简介"></textarea>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="exampleFormControlTextarea1" class="form-label">代码</label>
                                                                <VAceEditor v-model:value="bot.content" @init="editorInit" lang="c_cpp" theme="textmate" style="height: 250px" />
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <div class="error-message">{{ bot.message }}</div>
                                                            <button type="button" class="btn btn-primary" @click="update_bot(bot)">修改</button>
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>                                 
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
   
<script>
import Cropper from "@/components/PhotoCropper.vue"
import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from "vuex"
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor'
import ace from 'ace-builds'
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';


export default{
    components:{
        VAceEditor,
        Cropper,
    },
    setup(){
        ace.config.set(
            "basePath", 
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/"
        )
        const store=useStore();
        let show=ref(false);
        let show_update=ref(false);
        let bots=ref([]);
        const botadd=reactive({
            title:"",
            description:"",
            content:"",
            message:"",
        });
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
        const add_bot=()=>{
            botadd.message="";
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/bot/add/",
                type:"post",
                data:{
                    title:botadd.title,
                    description:botadd.description,
                    content:botadd.content,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.message==="successful"){
                        botadd.title="";
                        botadd.description="";
                        botadd.content="";
                        Modal.getInstance("#add").hide();
                        refresh_bots();
                    }
                    else{
                        botadd.message=resp.message;
                    }
                },
            })
        }
        const remove_bot=(bot)=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/bot/remove/",
                type:"post",
                data:{
                    bot_id:bot.id,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.message==="successful"){
                        refresh_bots();
                    }
                },
            })
        }
        const update_bot=(bot)=>{
            $.ajax({
                url:"https://app6418.acapp.acwing.com.cn/api/user/bot/update/",
                type:"post",
                data:{
                    bot_id:bot.id,
                    title:bot.title,
                    description:bot.description,
                    content:bot.content,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if(resp.message==="successful"){
                        Modal.getInstance('#update'+bot.id).hide();
                        refresh_bots();
                    }
                    else{
                        bot.message=resp.message;
                    }
                },
            })
        }
        const show_update_photo=()=>{
            show_update.value=true;
        }
        const hide_update_photo=()=>{
            show_update.value=false;
        }
        const update_photo=()=>{
            show.value=false;
            setTimeout(()=>{show.value=true;},200);
        }
        return{
            show,
            show_update,
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            show_update_photo,
            hide_update_photo,
            update_photo,
        }
    }
}
</script>

<style scoped>
div.card-body{
    padding: 1vw;
}
div.table-responsive>table>thead>tr>th {
    white-space: nowrap;
}
div.table-responsive>table>tbody>tr>td {
    white-space: nowrap;
}
div.error-message{
    color:red;
}
div.update_photo{
    height: 100%;
    width: 100%;
    background-color: rgba(50,50,50,0.75);
    position: absolute;
    top: 0vh;
    left: 0vw;
    cursor: pointer;
}
div.update_photo_text{
    text-align: center;
    color:white;
    font-size: 3vw;
    font-weight: 500;
    padding-top: 30%;
}
</style>