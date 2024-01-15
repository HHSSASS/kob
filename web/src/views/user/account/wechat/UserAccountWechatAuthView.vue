<template>
    <ContentField>
        <div style="text-align: center;font-size: 50px;">
            授权成功
        </div>
    </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField.vue"
import { useRoute } from 'vue-router';
import $ from 'jquery'

export default {
    components:{
        ContentField
    },
    setup() {
        const myRoute = useRoute();
        $.ajax({
            url: "https://app6418.acapp.acwing.com.cn/api/user/account/wechat/applyinfo/",
            type: "post",
            data: {
                code: myRoute.query.code,
                state: myRoute.query.state,
            },
        })
    }
}
</script>


<style scoped>
</style>