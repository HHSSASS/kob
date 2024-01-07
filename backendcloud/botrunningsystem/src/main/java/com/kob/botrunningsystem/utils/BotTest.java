package com.kob.botrunningsystem.utils;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BotTest implements java.util.function.Supplier<Integer>{
    static class Cell{
        public int x,y;
        public Cell(int x,int y){
            this.x=x;
            this.y=y;
        }
    }
    private boolean check_tail_increasing(int step){
        return step <= 10 || step % 3 == 1;
    }
    public List<Cell> getCells(int sx,int sy,String steps){
        steps=steps.substring(1,steps.length()-1);
        List<Cell> res=new ArrayList<>();
        int[] dx={-1,0,1,0},dy={0,1,0,-1};
        int x=sx,y=sy;
        int step=0;
        res.add(new Cell(x,y));
        for(int i=0;i<steps.length();++i){
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
    public Integer nextMove(String input) {
        String[] strs=input.split("#");
        int[][] g=new int[13][14];
        for(int i=0,k=0;i<13;++i){
            for(int j=0;j<14;++j,++k){
                if(strs[0].charAt(k)=='1'){
                    g[i][j]=1;
                }
            }
        }
        int aSx=Integer.parseInt(strs[1]),aSy=Integer.parseInt(strs[2]);
        int bSx=Integer.parseInt(strs[4]),bSy=Integer.parseInt(strs[5]);
        List<Cell> aCells=getCells(aSx,aSy,strs[3]);
        List<Cell> bCells=getCells(bSx,bSy,strs[6]);
        for(Cell c:aCells) g[c.x][c.y]=1;
        for(Cell c:bCells) g[c.x][c.y]=1;

        int[] dx={-1,0,1,0},dy={0,1,0,-1};
        int[] vis={0,0,0,0};
        Random random=new Random();
        for(int i=0;i<4;++i){
            int d=random.nextInt(4);
            while(vis[d]==1) d=random.nextInt(4);
            vis[d]=1;
            int x=aCells.get(aCells.size()-1).x+dx[d];
            int y=aCells.get(aCells.size()-1).y+dy[d];
            if(x>=0&&x<13&&y>=0&&y<14&&g[x][y]==0){
                return d;
            }
        }
        return random.nextInt(4);
    }

    @Override
    public Integer get() {
        File file=new File("input.txt");
        try {
            Scanner sc=new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

