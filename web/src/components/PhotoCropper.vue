<template>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#cropper_model" ref="show" style="display: none;"></button>
    <div class="modal fade" id="cropper_model" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">头像裁剪</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <input ref="input" type="file" accept="image/*" @change="setImage" style="display: none;">
                    <div class="row">
                        <div class="col-6">
                            <div v-show="imgSrc===''">
                                <img src="https://app6418.acapp.acwing.com.cn/images/user.png" alt="" width="100%">
                            </div>
                            <div class="img-cropper" v-show="imgSrc!=''">
                                <vue-cropper
                                    ref="cropper"
                                    :aspect-ratio="1"
                                    :src="imgSrc"
                                    containerStyle="height:40vh;width:20vw;"
                                    preview=".preview"
                                />
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="preview"></div>
                            <div style="text-align: center;padding-top: 10px; font-size: 20px;">头像预览</div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div style="margin-right: 10px;">*头像上传成功后可能需要一段时间更新</div>
                    <div class="error-message">{{ message }}</div>
                    <button @click="showFileChooser" type="button" class="btn btn-secondary">选取图片</button>
                    <button @click="cropImage" type="button" class="btn btn-primary">上传头像</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import { onMounted } from 'vue';
import VueCropper from 'vue-cropperjs';
import 'cropperjs/dist/cropper.css';
import $ from "jquery"

export default {
    components: {
        VueCropper,
    },
    setup(){
        const store=useStore();
        let imgSrc=ref("");
        let input=ref(null);
        let cropper=ref(null);
        let show=ref(null);
        let message=ref("");
        onMounted(()=>{
            show.value.click();
        })
        const cropImage=()=>{
            message.value="";
            var canvas=cropper.value.getCroppedCanvas();
            if(canvas==null){
                message.value="请选取图片";
                return;
            }
            var cropImg = canvas.toDataURL('image/jpeg');
            var arr = cropImg.split(',');
            var type = arr[0].match(/:(.*?);/)[1];
            var fileExt = type.split('/')[1];
            var bstr = atob(arr[1]);
            var n = bstr.length;
            var u8arr = new Uint8Array(n);
            while (n--) {
                u8arr[n] = bstr.charCodeAt(n);
            }
            var file=new File([u8arr], 'photo.' + fileExt, {type: type,});
            if(file.size/1024/1000>1){
                message.value="图片大小不能超过1MB";
                return;
            }else{
                let formData=new FormData();
                formData.append("file",file);
                $.ajax({
                    url:"https://app6418.acapp.acwing.com.cn/api/user/info/photo/",
                    type:"post",
                    data:formData,
                    contentType:false,
                    processData: false,
                    headers:{
                        Authorization:"Bearer "+store.state.user.token,
                    },
                    success(resp){
                        if(resp.message==="successful"){
                            location.reload();
                        }else{
                            message.value="上传失败";
                        }
                    },
                    error(){
                        message.value="上传失败";
                    }
                })
            }
        }
        const setImage=(e)=>{
            message.value="";
            const file = e.target.files[0];
            if(file==null) return;
            if(file.size/1024/1024>1){
                message.value="图片大小不能超过1MB";
                return;
            }
            if (file.type.indexOf('image/') === -1) {
                message.value="请选择图片类型文件";
                return;
            }
            if (typeof FileReader === 'function') {
            const reader = new FileReader();
            reader.onload = (event) => {
                imgSrc.value = event.target.result;
                cropper.value.replace(event.target.result);
            }
            reader.readAsDataURL(file);
            } else {
                message.value="文件读取API不支持";
            }
        }
        const showFileChooser=()=>{
            input.value.click();
        }
        return {
            imgSrc,
            show,
            input,
            cropper,
            message,
            cropImage,
            setImage,
            showFileChooser,
        };
    }
}
</script>

<style scoped>
div.img-cropper{
    margin: auto;
    width: 100%;
    height: 100%;
}
div.preview {
    margin: auto;
    margin-top: 5vh;
    width: 100%;
    height: 30vh;
    border-radius: 50%;
    overflow: hidden;
}
div.error-message{
    color:red;
}
</style>