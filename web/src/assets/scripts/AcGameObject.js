const AC_GAME_OBJECTS = []

export class AcGameObject{
    constructor(){
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0;//时间间隔,一帧几毫秒
        this.has_called_start = false;
    }
    start(){//只在创建时执行一次

    }
    update(){//每帧执行

    }
    on_destroy(){//删除前执行

    }
    destroy(){
        this.on_destroy();
        for(let i in AC_GAME_OBJECTS){
            if(AC_GAME_OBJECTS[i] === this){
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

let last_timestamp;//上一次执行时刻
const step = timestamp =>{
    for(let obj of AC_GAME_OBJECTS){
        if(!obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }
        else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step)
}
requestAnimationFrame(step)//在每一帧重绘之前调用