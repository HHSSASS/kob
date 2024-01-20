import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'

document.title='KOB';
const app=createApp(App)

let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i);
app.config.globalProperties.equipment=flag

app.use(router).use(store).use(store).mount('#app')