<template>
    <button type="button" class="btn btn-primary btn-lg float-end" data-bs-toggle="modal" data-bs-target="#error" ref="error_show" style="display: none;">错误</button>
    <div class="modal fade" id="error" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">错误</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    该账号在另一台设备上登录，请重新登录！
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal">确定</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { onMounted } from 'vue';

export default{
    setup(){
        let error_show=ref(null);
        onMounted(()=>{
            error_show.value.click();
        })
        return{
            error_show,
        }
    }
}
</script>

<style scoped>
</style>