<template>
    <button type="button" class="btn btn-primary btn-lg float-end" data-bs-toggle="modal" data-bs-target="#rule" ref="rule_show">规则</button>
    <div class="modal fade" id="rule" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">规则</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="title">游戏规则：</div>
                    <div>游戏地图大小为13行14列，四周被墙体包围，地图内部随机产生墙体，对战双方分别出生在地图左下角和右上角。</div>
                    <div>对战双方分别控制一条蛇，玩家可选择亲自上阵或使用bot控制自己蛇的移动，双方蛇头碰到墙体、自己或对手的蛇身视为失败，5秒内不操作视为失败，双方同一回合失败视为平局。</div>
                    <div>双方蛇身前十回合每回合伸长一格，十回合后每三回合伸长一格。</div>
                    &nbsp;
                    <div class="title">天梯分规则：</div>
                    <div>胜利+5分，失败-2分，平局+0，人机训练结果不影响天梯分。</div>
                    &nbsp;
                    <div class="title">亲自上阵：</div>
                    <div>使用键盘WSAD分别控制蛇的上下左右移动。</div>
                    &nbsp;
                    <div class="title">bot出战：</div>
                    <div>bot为一段代码（目前仅支持java），输入为目前对战局面（包括墙体位置、对战双方蛇身位置），输出为一个整数代表下一步移动方向（0123分别代表上右下左）。每名玩家最多拥有10个bot，可在右上角账户中心->我的bot中管理。</div>
                    &nbsp;
                    <div class="title">bot代码格式：</div>
                    <div>无需看懂完整代码，请将下面代码复制，并在对应处修改。您可能会用到的变量：</div>
                    <div>g[][]表示墙体，g[x][y]==1表示坐标(x,y)为墙体</div>
                    <div>aCells为List&lt;Cell>表示自己蛇身，bCells表示对手蛇身，aCells列表中每个Cell包含成员变量x,y，表示坐标(x,y)为自己蛇身，aCells.get(aCells.size()-1)为目前蛇头，bCells同理</div>
                    &nbsp;
                    <pre>
package com.kob.botrunningsystem.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//如需要import，请在此编写
////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////               
public class BotTest implements java.util.function.Supplier&lt;Integer>{

    static class Cell{ //蛇身每个块
        public int x,y;
        public Cell(int x,int y){
            this.x=x;
            this.y=y;
        }
    }

    private boolean check_tail_increasing(int step){ //判断下一回合蛇身是否伸长
        return step &lt;= 10 || step % 3 == 1;
    }

    public List&lt;Cell> getCells(int sx,int sy,String steps){ //解码字符串，提取出双方蛇身信息
        steps=steps.substring(1,steps.length()-1);
        List&lt;Cell> res=new ArrayList&lt;>();
        int[] dx={-1,0,1,0},dy={0,1,0,-1};
        int x=sx,y=sy;
        int step=0;
        res.add(new Cell(x,y));
        for(int i=0;i&lt;steps.length();++i){
            int d=steps.charAt(i)-'0';
            x+=dx[d];
            y+=dy[d];
            res.add(new Cell(x,y));
            if(!check_tail_increasing(++step)){
                res.remove(0);
            }
        }
        return res;
    }

    public Integer nextMove(String input) { //返回下一步移动方向
        String[] strs=input.split("#");
        int[][] g=new int[13][14]; //解码字符串，提取出地图墙体信息
        for(int i=0,k=0;i&lt;13;++i){
            for(int j=0;j&lt;14;++j,++k){
                if(strs[0].charAt(k)=='1'){
                    g[i][j]=1; //g[x][y]==1表示坐标(x,y)为墙体
                }
            }
        }
        int aSx=Integer.parseInt(strs[1]),aSy=Integer.parseInt(strs[2]); //自己蛇起点
        int bSx=Integer.parseInt(strs[4]),bSy=Integer.parseInt(strs[5]); //对手蛇起点
        List&lt;Cell> aCells=getCells(aSx,aSy,strs[3]); //自己蛇身，aCells列表中每个Cell包含成员变量x,y表示坐标(x,y)为自己蛇身
        List&lt;Cell> bCells=getCells(bSx,bSy,strs[6]); //对手蛇身，含义同上
        //请在此编写您的代码，如需要import请在代码开头处编写
        //////////////////////////////////////////////////////////////////
        int direction=0;




        return direction;//返回下一步移动方向（0，1，2，3）
        //////////////////////////////////////////////////////////////////
    }

    @Override
    public Integer get() { //读入对战局面文件，内容为将对战局面编码后的字符串
        File file=new File("input.txt");
        try {
            Scanner sc=new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
                    </pre>
                    <div class="title">作者水平有限，如遇到BUG或有任何问题可至社区反馈，感谢您的游玩((ヾ(｡･ω･)ﾉ☆ﾟ+.</div>
                </div>
                <div class="modal-footer">
                    <div class="form-check" style="margin-left: 10px;">
                        <input v-model="check" class="form-check-input" type="checkbox" value="" id="flexCheckDefault">
                        <label class="form-check-label" for="flexCheckDefault">本次不再提醒</label>
                    </div>
                    <button @click="confirm" type="button" class="btn btn-primary" data-bs-dismiss="modal">确定</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { onMounted } from 'vue';
import { useStore } from 'vuex';

export default{
    setup(){
        const store=useStore();
        let rule_show=ref(null);
        let check=ref(!store.state.user.show_rule);
        onMounted(()=>{
            if(store.state.user.show_rule){
                rule_show.value.click();
            } 
        })
        const confirm=()=>{
            store.commit("updateShowRule",!check.value);
        }
        return{
            rule_show,
            check,
            confirm,
        }
    }
}
</script>

<style scoped>
div.title{
    font-size: 24px;
    font-weight: 600;
}
</style>