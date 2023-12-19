package com.kob.backend.consumer.utils;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx={-1,0,1,0},dy={0,1,0,-1};
    private final Player playerA,playerB;
    public Game(Integer rows, Integer cols, Integer inner_walls_count,Integer idA,Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        playerA =new Player(idA,rows-2,1,new ArrayList<>());
        playerB =new Player(idB,1,cols-2,new ArrayList<>());
    }
    public Player getPlayerA(){
        return playerA;
    }
    public Player getPlayerB(){
        return playerB;
    }
    public int[][] getG(){
        return g;
    }

    public boolean check_connectivity(int sx,int sy,int tx,int ty){
        if(sx==tx&&sy==ty) return true;
        g[sx][sy]=1;
        for(int i=0;i<4;i++){
            int x=sx+dx[i],y=sy+dy[i];
            if(g[x][y]==0&&this.check_connectivity(x,y,tx,ty)){
                g[sx][sy]=0;
                return true;
            }
        }
        return false;
    }
    public boolean draw(){
        for(int r=0;r<this.rows;r++){//初始化
            for(int c=0;c<this.cols;c++){
                g[r][c]=0;
            }
        }

        for(int r=0;r<this.rows;r++){//四周创建墙
            g[r][0]=g[r][this.cols-1]=1;
        }
        for(int c=0;c<this.cols;c++){
            g[0][c]=g[this.rows-1][c]=1;
        }

        Random random=new Random();
        for(int i=0;i<this.inner_walls_count/2;i++){//随机创建墙
            for(int j=0;j<1000;j++){
                int r=random.nextInt(this.rows);
                int c=random.nextInt(this.cols);
                if(g[r][c]==1||g[this.rows-1-r][this.cols-1-c]==1||r==this.rows-2&&c==1||r==1&&c==this.cols-2) continue;
                g[r][c]=g[this.rows-1-r][this.cols-1-c]=1;
                break;
            }
        }

        return check_connectivity(this.rows-2,1,1,this.cols-2);//检查连通
    }
    public void createMap(){
        for(int i=0;i<1000;++i){
            if(draw()) break;
        }
    }
}
