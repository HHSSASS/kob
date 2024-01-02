import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
import { Snake } from "./Snake";

export class GameMap extends AcGameObject{
    constructor(ctx,parent,store){
        super();
        this.ctx=ctx;
        this.parent=parent;
        this.L=0;//一个格子单位绝对长度
        this.rows=13;
        this.cols=14;
        this.inner_walls_count=20;
        this.walls=[];
        this.snakes=[
            new Snake({id:0,color:"#4876EC",r:this.rows-2,c:1},this),
            new Snake({id:1,color:"#F94848",r:1,c:this.cols-2},this),
        ]
        this.store=store;
    }
    creat_walls(){//创建墙
        const g=this.store.state.pk.gamemap;

        for(let r=0;r<this.rows;r++){//创建墙
            for(let c=0;c<this.cols;c++){
                if(g[r][c]){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }
    }
    add_listening_events(){//绑定事件
        if(this.store.state.record.is_record){//录像
            let k=0;
            const a_steps=this.store.state.record.a_steps;
            const b_steps=this.store.state.record.b_steps;
            const winner=this.store.state.record.record_winner;
            const [snake0,snake1]=this.snakes;
            const interval_id=setInterval(()=>{
                if(k>=a_steps.length-1){
                    if(winner==="all"||winner==="B"){
                        snake0.status="die";
                    }
                    if(winner==="all"||winner==="A"){
                        snake1.status="die";
                    }
                    clearInterval(interval_id);
                }
                else{
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k++;
            },300)
        }
        else{
            this.ctx.canvas.focus();
            this.ctx.canvas.addEventListener("keydown",e=>{
                let d=-1;
                if(e.key==='w') d=0;
                else if(e.key==='d') d=1;
                else if(e.key==='s') d=2;
                else if(e.key==='a') d=3;
                if(d>=0){
                    this.store.state.pk.socket.send(JSON.stringify({
                        event:"move",
                        direction:d,
                    }))
                }
            });
        }
    }
    start(){
        this.creat_walls();
        this.add_listening_events();
    }
    check_valid(cell){//检测目标位置是否合法
        for(const wall of this.walls){//是否撞墙
            if(wall.r===cell.r&&wall.c===cell.c) return false;
        }
        
        for(const snake of this.snakes){//是否撞蛇
            let k=snake.cells.length;
            if(!snake.check_tail_increasing) k--;
            for(let i=0;i<k;i++){
                if(snake.cells[i].r===cell.r&&snake.cells[i].c===cell.c) return false;
            }
        }
        return true;
    }
    check_ready(){//双方是否准备好下一回合
        for(const snake of this.snakes){
            if(snake.status!=="idle"||snake.direction===-1) return false 
        }
        return true;
    }
    next_step(){//双方进入下一回合
        for(const snake of this.snakes){
            snake.next_step();
        }
    }
    update_size(){//更新地图大小
        this.L=parseInt(Math.min(this.parent.clientWidth/this.cols,this.parent.clientHeight/this.rows));
        this.ctx.canvas.width=this.L*this.cols;
        this.ctx.canvas.height=this.L*this.rows;
    }
    update(){
        this.update_size();
        if(this.check_ready()) this.next_step();
        this.render();
    }
    render(){//渲染
        const color_even="#AAD751",color_odd="#A2D149";
        for(let r=0;r<this.rows;r++){
            for(let c=0;c<this.cols;c++){
                if((r+c)%2===0){
                    this.ctx.fillStyle=color_even;
                }
                else{
                    this.ctx.fillStyle=color_odd;
                }
                this.ctx.fillRect(c*this.L,r*this.L,this.L,this.L);
            }
        }
    }
}