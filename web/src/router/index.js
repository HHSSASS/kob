import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from "../views/pk/PkIndexView"
import RecordIndexView from "../views/record/RecordIndexView"
import RecordContentView from "../views/record/RecordContentView"
import RanklistIndexView from "../views/ranklist/RanklistIndexView"
import CommunityIndexView from "../views/community/CommunityIndexView"
import UserBotIndexView from "../views/user/bot/UserBotIndexView"
import NotFound from "../views/error/NotFound"
import UserAccountLoginView from "../views/user/account/UserAccountLoginView"
import UserAccountRegisterView from "../views/user/account/UserAccountRegisterView"
import UserAccountPhoneLoginView from "../views/user/account/phone/UserAccountPhoneLoginView"
import UserAccountWechatLoginView from "../views/user/account/wechat/UserAccountWechatLoginView"
import UserAccountWechatAuthView from "../views/user/account/wechat/UserAccountWechatAuthView"
import UserAccountQQAuthView from "../views/user/account/qq/UserAccountQQAuthView"
import store from '../store/index'

const routes = [
  {
    path:"/",
    name:"home",
    redirect:"/pk/",
    meta:{
      requestAuth:true,
    },
  },
  {
    path:"/pk/",
    name:"pk_index",
    component:PkIndexView,
    meta:{
      requestAuth:true,
    },
  },
  {
    path:"/record/",
    name:"record_index",
    component:RecordIndexView,
    meta:{
      requestAuth:true,
    },
  },
  {
    path:"/record/:recordId/",
    name:"record_content",
    component:RecordContentView,
    meta:{
      requestAuth:true,
    },
  },
  {
    path:"/ranklist/",
    name:"ranklist_index",
    component:RanklistIndexView,
    meta:{
      requestAuth:true,
    },
  },
  {
    path:"/community/",
    name:"community_index",
    component:CommunityIndexView,
    meta:{
      requestAuth:true,
    },
  },
  {
    path:"/user/bot/",
    name:"user_bot_index",
    component:UserBotIndexView,
    meta:{
      requestAuth:true,
    },
  },
  {
    path:"/user/account/login/",
    name:"user_account_login",
    component:UserAccountLoginView,
    meta:{
      requestAuth:false,
    },
  },
  {
    path:"/user/account/register/",
    name:"user_account_register",
    component:UserAccountRegisterView,
    meta:{
      requestAuth:false
    },
  },
  {
    path:"/user/account/phone/login/",
    name:"user_account_phone_login",
    component:UserAccountPhoneLoginView,
    meta:{
      requestAuth:false
    }
  },
  {
    path:"/user/account/wechat/login/",
    name:"user_account_wechat_login",
    component:UserAccountWechatLoginView,
    meta:{
      requestAuth:false
    }
  },
  {
    path:"/user/account/wechat/auth/",
    name:"user_account_wechat_auth",
    component:UserAccountWechatAuthView,
    meta:{
      requestAuth:false
    }
  },
  {
    path:"/user/account/qq/auth/",
    name:"user_account_qq_auth",
    component:UserAccountQQAuthView,
    meta:{
      requestAuth:false
    }
  },
  {
    path:"/404/",
    name:"404",
    component:NotFound,
    meta:{
      requestAuth:false,
    },
  },
  {
    path:"/:catchAll(.*)",
    redirect:"/404/",
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to,from,next)=>{//通过router跳转页面前调用
  if(to.meta.requestAuth&&!store.state.user.is_login){
    const jwt_token=localStorage.getItem("jwt_token");
    if(jwt_token){
      store.commit("updateToken",jwt_token);
      store.dispatch("getinfo",{
          success(){
              next();
          },
          error(){
              next({name:"user_account_login"});
          },
      })
    }else{
      next({name:"user_account_login"});
    }
  }else{
    next();
  }
})

export default router
