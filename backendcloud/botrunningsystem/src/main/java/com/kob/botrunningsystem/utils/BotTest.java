package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BotTest implements java.util.function.Supplier<Integer> {
    int[] dx={-1,0,1,0},dy={0,1,0,-1};
    static class Cell { // 蛇身每个块
        public int x, y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private boolean check_tail_increasing(int step) { // 判断下一回合蛇身是否伸长
        return step <= 10 || step % 3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps) { // 解码字符串，提取出双方蛇身信息
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int i = 0; i < steps.length(); ++i) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                res.remove(0);
            }
        }
        return res;
    }

    public Integer nextMove(String input) { // 返回下一步移动方向
        String[] strs = input.split("#");
        int[][] g = new int[13][14]; // 解码字符串，提取出地图墙体信息
        for (int i = 0, k = 0; i < 13; ++i) {
            for (int j = 0; j < 14; ++j, ++k) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1; // g[x][y]==1表示坐标(x,y)为墙体
                }
            }
        }
        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]); // 自己蛇起点
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]); // 对手蛇起点
        List<Cell> aCells = getCells(aSx, aSy, strs[3]); // 自己蛇身
        List<Cell> bCells = getCells(bSx, bSy, strs[6]); // 对手蛇身

        int direction = 0;
        int ans=Integer.MIN_VALUE;
        int depth=10;
        for(Cell c:aCells) g[c.x][c.y]=1;
        for(Cell c:bCells) g[c.x][c.y]=1;
        int step=strs[3].length()-2;
        if (!check_tail_increasing(++step)) {
            g[aCells.get(0).x][aCells.get(0).y]=0;
            aCells.remove(0);
        }
        int ax=aCells.get(aCells.size()-1).x,ay=aCells.get(aCells.size()-1).y;
        for(int i=0;i<4;++i){
            int nx=ax+dx[i],ny=ay+dy[i];
            if(nx>=0&&nx<13&&ny>=0&&ny<14&&g[nx][ny]==0){
                aCells.add(new Cell(nx,ny));
                g[nx][ny]=1;
                int now=alphaBetaPruning(g,aCells,bCells,step,Integer.MAX_VALUE,Integer.MIN_VALUE,2,depth);
                if(now>ans){
                    direction=i;
                    ans=now;
                }
                aCells.remove(aCells.size()-1);
                g[nx][ny]=0;
            }
        }
        return direction;
    }

    private int alphaBetaPruning(int[][] g_, List<Cell> aCells_, List<Cell> bCells_, int step, int alpha, int beta, int player,int depth) {
        int[][] g=new int[13][14];
        for(int i=0;i<g.length;++i){
            g[i]= Arrays.copyOf(g_[i],g_[i].length);
        }
        List<Cell> aCells=new ArrayList<>(aCells_);
        List<Cell> bCells=new ArrayList<>(bCells_);
        if(player==1){
            if (!check_tail_increasing(step)) {
                g[aCells.get(0).x][aCells.get(0).y]=0;
                aCells.remove(0);
            }
            int ax=aCells.get(aCells.size()-1).x,ay=aCells.get(aCells.size()-1).y;
            if(depth==0){
                int cnt=4;
                int bx=bCells.get(bCells.size()-1).x,by=bCells.get(bCells.size()-1).y;
                for(int i=0;i<4;++i) {
                    int nx=ax+dx[i],ny=ay+dy[i];
                    if(nx>=0&&nx<13&&ny>=0&&ny<14&&g[nx][ny]==0) cnt++;
                }
                for(int i=0;i<4;++i){
                    int nx=bx+dx[i],ny=by+dy[i];
                    if(nx>=0&&nx<13&&ny>=0&&ny<14&&g[nx][ny]==0) cnt--;
                }
                return cnt;
            }
            boolean flag=false;
            int ans=beta;
            for(int i=0;i<4;++i){
                int nx=ax+dx[i],ny=ay+dy[i];
                if(nx>=0&&nx<13&&ny>=0&&ny<14&&g[nx][ny]==0){
                    flag=true;
                    aCells.add(new Cell(nx,ny));
                    g[nx][ny]=1;
                    int now=alphaBetaPruning(g,aCells,bCells,step,alpha,ans,2,depth);
                    aCells.remove(aCells.size()-1);
                    g[nx][ny]=0;
                    if(now>=alpha) return alpha;
                    else ans=Math.max(ans,now);
                }
            }
            if(!flag){
                return Integer.MIN_VALUE+step;
            }
            return ans;
        }else{
            if (!check_tail_increasing(step)) {
                g[bCells.get(0).x][bCells.get(0).y]=0;
                bCells.remove(0);
            }
            int bx=bCells.get(bCells.size()-1).x,by=bCells.get(bCells.size()-1).y;
            boolean flag=false;
            int ans=alpha;
            for(int i=0;i<4;++i){
                int nx=bx+dx[i],ny=by+dy[i];
                if(nx>=0&&nx<13&&ny>=0&&ny<14&&g[nx][ny]==0){
                    flag=true;
                    bCells.add(new Cell(nx,ny));
                    g[nx][ny]=1;
                    int now=alphaBetaPruning(g,aCells,bCells,step+1,ans,beta,1,depth-1);
                    bCells.remove(bCells.size()-1);
                    g[nx][ny]=0;
                    if(now<=beta) return beta;
                    else ans=Math.min(ans,now);
                }
            }
            if(!flag){
                return Integer.MAX_VALUE-step;
            }
            return ans;
        }
    }

    @Override
    public Integer get() { // 读入对战局面文件，内容为将对战局面编码后的字符串
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
