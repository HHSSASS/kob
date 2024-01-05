
export default{
    state: {
        status:"menu",//menu菜单，matching正在匹配，matched匹配成功，playing正在对战
        socket:null,
        opponent_username:"",
        opponent_photo:"",
        gamemap:null,
        a_id:0,
        a_sx:0,
        a_sy:0,
        b_id:0,
        b_sx:0,
        b_sy:0,
        gameObject:null,
        winner:"none",
        a_rating:"",
        b_rating:"",
        counter:0,
        is_bot:false,
    },
    getters: {
    },
    mutations: {
        updateSocket(state,socket){
            state.socket=socket;
        },
        updateOpponent(state,opponent){
            state.opponent_username=opponent.username;
            state.opponent_photo=opponent.photo;
        },
        updateStatus(state,status){
            state.status=status;
        },
        updateGame(state,game){
            state.gamemap=game.map;
            state.a_id=game.a_id;
            state.a_sx=game.a_sx;
            state.a_sy=game.a_sy;
            state.b_id=game.b_id;
            state.b_sx=game.b_sx;
            state.b_sy=game.b_sy;
        },
        updateGameObject(state,gameObject){
            state.gameObject=gameObject;
        },
        updateWinner(state,winner){
            state.winner=winner;
        },
        updateRating(state,data){
            state.a_rating=data.a_rating;
            state.b_rating=data.b_rating;
        },
        updateCounter(state,counter){
            state.counter=counter;
        },
        updateIsBot(state,is_bot){
            state.is_bot=is_bot;
        },
    },
    actions: {
    },
    modules: {
    }
}