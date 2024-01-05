import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject{
    constructor(info,gamemap){
        super();
        this.id=info.id;
        this.color=info.color;
        this.gamemap=gamemap;
        this.cells=[new Cell(info.r,info.c)];//蛇的身体
        this.next_cell=null;//下一回合目标位置
        this.speed=5;//蛇每秒走5格
        this.direction=-1;//蛇移动方向，-1无指令，0123上右下左
        this.status='idle';//蛇的状态，idle静止，move正在移动，die死亡
        this.dr=[-1,0,1,0];//行偏移量
        this.dc=[0,1,0,-1];//列偏移量
        this.step=0//回合数
        this.eps=1e-2;//允许误差
        this.eye_direction=0;//眼睛朝向
        if(this.id===1)this.eye_direction=2;
        this.eye_dx=[//两只眼睛x方向偏移量
            [-1,1],
            [1,1],
            [1,-1],
            [-1,-1],
        ];
        this.eye_dy=[
            [-1,-1],
            [-1,1],
            [1,1],
            [1,-1],
        ];
    }
    start(){

    }
    check_tail_increasing(){//检测当前回合蛇长度是否增加
        if(this.step<=10||this.step%3===1) return true;
        return false;
    }
    set_direction(d){//设置方向
        this.direction=d;
    }
    next_step(){//更新下一回合状态
        const d=this.direction;
        this.next_cell=new Cell(this.cells[0].r+this.dr[d],this.cells[0].c+this.dc[d]);
        this.direction=-1;
        this.status="move";
        this.step++;
        this.eye_direction=d;

        const k=this.cells.length;
        for(let i=k;i>0;i--){//除蛇头每个cell移动一格，此时0、1都为原蛇头
            this.cells[i]=JSON.parse(JSON.stringify(this.cells[i-1]));
        }
    }
    update_move(){//更新移动
        const dx=this.next_cell.x-this.cells[0].x;
        const dy=this.next_cell.y-this.cells[0].y;
        const distance=Math.sqrt(dx*dx+dy*dy);
        if(distance<this.eps){
            this.status="idle";
            this.cells[0]=this.next_cell;
            this.next_cell=null;
            
            if(!this.check_tail_increasing()) this.cells.pop();
        }
        else{
            const move_distance=this.speed*this.timedelta/1000;//每帧走的距离
            this.cells[0].x+=move_distance*dx/distance;//蛇头移动
            this.cells[0].y+=move_distance*dy/distance;

            if(!this.check_tail_increasing()){//蛇尾移动
                const k=this.cells.length;
                const tail=this.cells[k-1];
                const tail_target=this.cells[k-2];
                const tail_dx=tail_target.x-tail.x;
                const tail_dy=tail_target.y-tail.y;
                tail.x+=move_distance*tail_dx/distance;
                tail.y+=move_distance*tail_dy/distance;
            }
        }
    }
    update(){
        if(this.status==="move") this.update_move();
        this.render();
    }
    render(){
        const L=this.gamemap.L;
        const ctx=this.gamemap.ctx;
        ctx.fillStyle=this.color;
        if(this.status==="die"){
            ctx.fillStyle="white";
        }

        for(const cell of this.cells){
            ctx.beginPath();
            ctx.arc(cell.x*L,cell.y*L,L/2*0.8,0,Math.PI*2);
            ctx.fill();
        }

        for(let i=1;i<this.cells.length;i++){//长方形美化
            const a=this.cells[i-1],b=this.cells[i];
            if(Math.abs(a.x-b.x)<this.eps&&Math.abs(a.y-b.y)<this.eps) continue;//重合
            if(Math.abs(a.x-b.x)<this.eps){
                ctx.fillRect((a.x-0.4)*L,Math.min(a.y,b.y)*L,L*0.8,Math.abs(a.y-b.y)*L);
            }
            else{
                ctx.fillRect(Math.min(a.x,b.x)*L,(a.y-0.4)*L,Math.abs(a.x-b.x)*L,L*0.8);
            }
        }

        ctx.fillStyle="black";//画蛇眼睛
        for(let i=0;i<2;i++){
            const eye_x=(this.cells[0].x+this.eye_dx[this.eye_direction][i]*0.15)*L;
            const eye_y=(this.cells[0].y+this.eye_dy[this.eye_direction][i]*0.15)*L;
            ctx.beginPath();
            ctx.arc(eye_x,eye_y,L*0.05,0,Math.PI*2);
            ctx.fill();
        }
    }
}