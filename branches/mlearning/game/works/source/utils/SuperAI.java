package utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import betPredict.BetPredict;
import betPredict.Pair;
import betPredict.UserUnit;

/**
 * 抽象类SuperAI实现了一个AI算法
 * 包含了一个AI算法应该包含的属性及方法，用于客户端主程序与算法程序的交互
 * @author wangtao
 *
 */
public abstract class SuperAI {
    private ArrayList<Poker> holdPokers;       //底牌
    private ArrayList<Poker> publicPokers;     //公共牌
    private String playerID;        //自己的注册ID
    private int blind;          //盲注金额或最小押注金额
    private int totalJetton;        //剩余筹码
    private int totalMoney;         //剩余金币
    private int playerNum;          //玩家人数
    private int position;           //自己的位置，即押注的顺序
    private int handNum;            //当前局数
    private boolean folded;      //是否已弃牌
    
    private boolean hasRaised;      //标记本环节是否已加过注
    
    private ArrayList<InitState> initStates;    //用于记录所有玩家的初始状态
  //用于记录所有未弃牌玩家的押注状态
    private ArrayList<BetState> holdState, flopState, turnState, riverState;
    private BetPredict predictor;               //机器学习算法
    
    
    public SuperAI(String playerID) {
        this.holdPokers = new ArrayList<Poker>();
        this.publicPokers = new ArrayList<Poker>();
        this.playerID = playerID;
        this.folded = false;
        
        this.predictor = BetPredict.getInstance();
    }
    
    public void setPlayerID(String playerID) {
    	this.playerID = playerID;
    }
    /**
     * 设置玩家人数
     * @param number
     */
    public void setPlayersNumber(int number) {
        this.playerNum = number;
    }
    
    /**
     * 设置该局的盲注金额，即最小押注金额
     * @param blind
     */
    public void setBlind(int blind) {
        this.blind = blind;
    }
    
    /**
     * 设置剩余筹码，剩余金币，玩家人数，自己的位置，当前局数
     * @param jetton
     * @param money
     */
    public void setInitInfo(int jetton, int money, int playerNum, 
            int position, int handNum) {
        this.totalJetton = jetton;
        this.totalMoney = money;
        this.playerNum = playerNum;
        this.position = position;
        this.handNum = handNum;
        this.holdPokers.clear();
        this.publicPokers.clear();
        this.folded = false;
        
        this.holdState = null;
        this.flopState = null;
        this.turnState = null;
        this.riverState = null;
    }
    
    public boolean getFolded() {
        return this.folded;
    }
    
    public String getPlayerID() {
        return this.playerID;
    }
    
    public int getTotalJetton() {
        return this.totalJetton;
    }
    public int getTotalMoney() {
    	return this.totalMoney;
    }
    public int getTotalMoneyAndJetton() {
    	return this.totalMoney + this.totalJetton;
    }
    public int getPlayerNum() {
    	return this.playerNum;
    }
    public int getHandNum() {
    	return this.handNum;
    }
    public ArrayList<Poker> getHoldPokers() {
        return this.holdPokers;
    }
    
    public ArrayList<Poker> getPublicPokers() {
        return this.publicPokers;
    }
    
    public int getBlind() {
        return this.blind;
    }
    
    public boolean getHasRaised() {
        return this.hasRaised;
    }
    
    public void setHasRaised(boolean flag) {
        this.hasRaised = flag;
    }
    

    
    public void setInitStates(ArrayList<InitState> states) {
        this.initStates = states;
    }
    
    /**
     * 玩家playerID需要下筹码为jet的盲注
     */
    public void postBlind(String playerID, int jet) {
        if (this.playerID == playerID) {
            this.totalJetton -= jet;
        }
    }
    
    /**
     * 添加两张底牌
     */
    public void addHoldPokers(Poker p1, Poker p2) {
        this.holdPokers.add(p1);
        this.holdPokers.add(p2);
        
        this.hasRaised = false;
    }
    
    /**
     * 发出两张底牌之后思考策略
     * @param betStates 各玩家的当前押注状态
     * @return 押注策略 "check|call|raise num|all_in|fold"
     */
    public abstract String thinkAfterHold(
            ArrayList<BetState> betStates);
    
    /**
     * 添加三张公共牌
     */
    public void addFlopPokers(Poker p1, Poker p2, Poker p3) {
        this.publicPokers.add(p1);
        this.publicPokers.add(p2);
        this.publicPokers.add(p3);
        this.hasRaised = false;
    }
    
    /**
     * 发出三张公共牌之后思考策略
     * @param betStates 各玩家的当前押注状态
     * @return 押注策略 "check|call|raise num|all_in|fold"
     */
    public abstract String thinkAfterFlop(
            ArrayList<BetState> betStates);
    
    /**
     * 添加一张转牌
     */
    public void addTurnPoker(Poker p) {
        this.publicPokers.add(p);
        this.hasRaised = false;
    }
    
    /**
     * 发出一张转牌之后思考策略
     * @param betStates 各玩家的当前押注状态
     * @return 押注策略 "check|call|raise num|all_in|fold"
     */
    public abstract String thinkAfterTurn(
            ArrayList<BetState> betStates);
    
    /**
     * 添加一张河牌
     */
    public void addRiverPoker(Poker p) {
        this.publicPokers.add(p);
        
        this.hasRaised = false;
    }
    
    /**
     * 发出一张河牌之后思考策略
     * @param betStates 各玩家的当前押注状态
     * @return 押注策略 "check|call|raise num|all_in|fold"
     */
    public abstract String thinkAfterRiver(
            ArrayList<BetState> betStates);
    
    
    /**
     * 处理其他玩家的该局押注情况，用于机器学习
     * @param holds
     */
    public void parseOtherPlayerInfo(HashMap<String, ArrayList<Poker>> holdsMap) {
        ArrayList<BetRecord> records = new ArrayList<BetRecord>();
        
        for (String id: holdsMap.keySet()) {
            if (this.getPlayerID().equals(id))
                continue;
            int pos = this.computePlayerPosition(id);
            InitState init = this.getInitState(id);
            float jet = (float) init.getJetton() / this.getBlind();
            
            BetRecord record = new BetRecord(id, pos, jet);
             
            String holdType = this.computeMaxMlCardType(holdsMap.get(id), "hold");
            float holdSt = this.computeStrategy(id, this.holdState);
            record.setHoldState(holdType, holdSt);
            
            String flopType = this.computeMaxMlCardType(holdsMap.get(id), "flop");
            float flopSt = this.computeStrategy(id, this.flopState);
            record.setFlopState(flopType, flopSt);
            
            String turnType = this.computeMaxMlCardType(holdsMap.get(id), "turn");
            float turnSt = this.computeStrategy(id, this.turnState);
            record.setTurnState(turnType, turnSt);
            
            String riverType = this.computeMaxMlCardType(holdsMap.get(id), "river");
            float riverSt = this.computeStrategy(id, this.riverState);
            record.setRiverState(riverType, riverSt);
            
//            try {
//                FileWriter writter = new FileWriter("data.txt");
//                System.out.println(id + " " + pos + " " + jet);
//                System.out.println("hold: " + holdType + " " + holdSt);
//                System.out.println("flop: " + flopType + " " + flopSt);
//                System.out.println("turn: " + turnType + " " + turnSt);
//                System.out.println("river: " + riverType + " " + riverSt);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
            
            
            records.add(record);
        }
        
        // 此处与机器学习模块对接
        for (BetRecord record: records) {
            UserUnit unit = new UserUnit(record.getPlayerID(), this.handNum, 
                    record.getPosition(), record.getJetton());
            unit.setHoldCard(record.getHoldType(), record.getHoldStrategy());
            unit.setFlopCard(record.getFlopType(), record.getFlopStrategy());
            unit.setTurnCard(record.getTurnType(), record.getTurnStrategy());
            unit.setRiverCard(record.getRiverType(), record.getRiverStrategy());
            this.predictor.addUserUnit(unit);
        }
        
    }
    
    /**
     * 返回指定玩家的的初始状态信息
     * @param id
     * @return
     */
    private InitState getInitState(String id) {
        for (InitState state: initStates) {
            if (state.getPlayerID().equals(id)) {
                return state;
            }
        }
        return null;
    }
    
    /**
     * 计算玩家在牌桌的位置，用于机器学习
     * @param playerID
     * @return "0|1|2" 分别表示"最后位置|首位|中间位置"
     */
    private int computePlayerPosition(String playerID) {
        int pos = 0;
        for (int i = 0; i < initStates.size(); i++) {
            if (initStates.get(i).equals(playerID)) {
                pos = i;
                break;
            }
        }
        if (pos == 0) {
            //最后位置
            return 2;
        }
        else if (pos == 1) {
            //第一个位置
            return 0;
        }
        else {
            //中间位置
            return 1;
        }
    }
    
    /**
     * 计算玩家的最大机器学习牌型，用于机器学习
     * @param holds
     * @param type
     * @return
     */
    private String computeMaxMlCardType(ArrayList<Poker> holds, String type) {
        if (type.equals("hold")) {
            return this.computeHoldMlCardType(holds);
        }
        else if (type.equals("flop") || type.equals("turn")) {
            ArrayList<Poker> pokers = new ArrayList<Poker>();
            pokers.addAll(holds);
            pokers.add(this.getPublicPokers().get(0));
            pokers.add(this.getPublicPokers().get(1));
            pokers.add(this.getPublicPokers().get(2));
            
            if (type.equals("turn"))
                pokers.add(this.getPublicPokers().get(3));
            
            return this.computeFlopAndTurnMlCardType(pokers);
        }
        else if (type.equals("river")) {
            ArrayList<Poker> pokers = new ArrayList<Poker>();
            pokers.addAll(holds);
            pokers.addAll(this.getPublicPokers());
            
            return this.computeRiverMlCardType(pokers);
        }
        return "";
    }
    
    /**
     * 计算两张底牌组成的机器学习牌型
     * @param holds
     * @return
     */
    private String computeHoldMlCardType(ArrayList<Poker> holds) {
        Poker p1 = holds.get(0);
        Poker p2 = holds.get(1);
        if (p1.getValue() == p2.getValue()) {
            if (p1.getValue() >= 9)
                return MlHoldCardType.BIG_PAIR.toString();
            else
                return MlHoldCardType.SMALL_PAIR.toString();
        }
        if (p1.getColor() == p2.getColor()) {
            if (p1.getValue() >= 9 && p2.getValue() >= 9)
                return MlHoldCardType.BIG_FLUSH.toString();
            else 
                return MlHoldCardType.SMALL_FLUSH.toString();
        }
        
        if (p1.getValue() > 9 && p2.getValue() > 9)
            return MlHoldCardType.HIGH_SINGLE.toString();
        else
            return MlHoldCardType.LOW_SINGLE.toString();
    }
    
    /**
     * 计算Flop和Turn环节组成的机器学习牌型
     * @param holds
     * @return
     */
    private String computeFlopAndTurnMlCardType(ArrayList<Poker> pokers) {
        CardGroup maxGroup = (new MaxCardComputer(pokers))
                .getMaxCardGroup();
        
        switch (maxGroup.getType()) {
        case STRAIGHT_FLUSH:
        case FOUR_OF_A_KIND:
        case FULL_HOUSE:
            return MlCardType.FULL_HOUSE_UP.toString();
        case FLUSH:
            return MlCardType.FLUSH.toString();
        case STRAIGHT:
            return MlCardType.STRAIGHT.toString();
        case THREE_OF_A_KIND:
            return MlCardType.THREE_OF_A_KIND.toString();
        case TWO_PAIR:
            if (maxGroup.getPokers().get(0).getValue() >= 9)
                return MlCardType.HIGH_TWO_PAIR.toString();
            else
                return MlCardType.LOW_TWO_PAIR.toString();
        case ONE_PAIR:
            if (maxGroup.getPokers().get(0).getValue() >= 9)
                return MlCardType.HIGH_ONE_PAIR.toString();
            else
                return MlCardType.LOW_ONE_PAIR.toString();
        case HIGH_CARD:
            if (this.computeFlush_1(pokers))
                return MlCardType.FLUSH_1.toString();
            if (this.computeStraight_1(pokers))
                return MlCardType.STRAIGHT_1.toString();
            return MlCardType.HIGH_CARD.toString();
        }
        return MlCardType.HIGH_CARD.toString();
    }
    
    /**
     * 计算River环节组成的机器学习牌型
     * @param holds
     * @return
     */
    private String computeRiverMlCardType(ArrayList<Poker> pokers) {
        CardGroup maxGroup = (new MaxCardComputer(pokers))
                .getMaxCardGroup();
        
        switch (maxGroup.getType()) {
        case STRAIGHT_FLUSH:
        case FOUR_OF_A_KIND:
        case FULL_HOUSE:
            return MlCardType.FULL_HOUSE_UP.toString();
        case FLUSH:
            return MlCardType.FLUSH.toString();
        case STRAIGHT:
            return MlCardType.STRAIGHT.toString();
        case THREE_OF_A_KIND:
            return MlCardType.THREE_OF_A_KIND.toString();
        case TWO_PAIR:
            if (maxGroup.getPokers().get(0).getValue() >= 9)
                return MlCardType.HIGH_TWO_PAIR.toString();
            else
                return MlCardType.LOW_TWO_PAIR.toString();
        case ONE_PAIR:
            if (maxGroup.getPokers().get(0).getValue() >= 9)
                return MlCardType.HIGH_ONE_PAIR.toString();
            else
                return MlCardType.LOW_ONE_PAIR.toString();
        case HIGH_CARD:
            return MlCardType.HIGH_CARD.toString();
        }
        return MlCardType.HIGH_CARD.toString();
    }
    
    /**
     * 计算当前牌型是否为同花缺一张
     * @param pokers
     * @return
     */
    private boolean computeFlush_1(ArrayList<Poker> pokers) {
        int count[] = new int[4];
        for (Poker p: pokers) {
            switch (p.getColor()) {
            case SPADES:
                count[0] ++;
                break;
            case HEARTS:
                count[1] ++;
                break;
            case CLUBS:
                count[2] ++;
                break;
            case DIAMONDS:
                count[3] ++;
                break;
            }
        }
        
        int maxCount = 0;
        for (int i = 0; i < count.length; i++)
            if (count[i] > maxCount)
                maxCount = count[i];
        
        if (maxCount == 4)
            return true;
        return false;
    }
    
    /**
     * 计算当前牌是否为顺子缺一张
     * @param pokers
     * @return
     */
    private boolean computeStraight_1(ArrayList<Poker> pokers) {
        boolean visited[] = new boolean[15];
        for (int i = 0; i < visited.length; i++) 
            visited[i] = false;
        
        //将所有出现的牌值标记
        for (Poker poker: pokers) {
            if (poker.getValue() == 14) {
                visited[1] = visited[14] = true;
            }
            else {
                visited[poker.getValue()] = true;
            }
        }
        int maxCount = 0;
        for (int i = 1; i <= 10; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (visited[i + j]) {
                    count ++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
            }
        }
        
        if (maxCount == 4)
            return true;
        return false;
    }
//    /**
//     * 计算其他玩家的最大押注策略，用于机器学习
//     * @return
//     */
//    private float computeMaxOtherStrategy() {
//        int maxJet = -1;
//        String maxSt = "";
//        for (BetState state: betStates) {
//            if (state.getPlayerID().equals(this.getPlayerID()))
//                continue;
//            if (state.getBet() > maxJet) {
//                maxJet = state.getBet();
//                maxSt = state.getAction();
//            }
//        }
//        if (maxSt.equals("all_in"))
//            return -1;
//        else if (maxSt.equals("check"))
//            return 0;
//        else
//            return (float)maxJet / this.getBlind();
//    }
    
    /**
     * 计算玩家的押注策略，用于机器学习
     * @param playerID
     * @return
     */
    private float computeStrategy(String playerID, 
            ArrayList<BetState> betStates) {       
        if (betStates == null)
            return -1;
        
        int jet = -1;
        String st = "";
        for (BetState state: betStates) {
            if (!state.getPlayerID().equals(playerID))
                continue;
            jet = state.getBet();
            st = state.getAction();
        }
        
        if (st.equals("all_in") || st.equals(""))
            return -1;
        else if (st.equals("check"))
            return 0;
        else
            return (float)jet / this.getBlind();
    }
    
    /**
     * 使用机器学习根据玩家的当前押注情况预测牌型
     * @param state
     * @return
     */
    public Set<Pair<String,Float>> computePlayerCardTypeByMl(
            BetState state) {
        String id = state.getPlayerID();
        int pos = this.computePlayerPosition(id);
        float jet = 0;
        for (InitState initstate: initStates) {
            if (initstate.getPlayerID().equals(id))
                jet = (float)initstate.getJetton() / this.getBlind();
        }
        float st = 0;
        if (state.getAction().equals("all_in"))
            st = -1;
        else
            st = (float)state.getBet() / this.getBlind();
        
        // 与机器学习模块对接
        Set<Pair<String,Float>> res = null;
        if (this.getPublicPokers().size() == 0)
            res = this.predictor.getPredict(id, "hold", jet, st);
        else if (this.getPublicPokers().size() == 3)
            res = this.predictor.getPredict(id, "flop", jet, st);
        else if (this.getPublicPokers().size() == 4)
            res = this.predictor.getPredict(id, "turn", jet, st);
        else if (this.getPublicPokers().size() == 5)
            res = this.predictor.getPredict(id, "river", jet, st);
        
        return res;
    }
    
    /**
     * 添加各玩家的各环节押注情况
     * @param betStates
     */
    public void addBetStates(ArrayList<BetState> betStates) {
        if (this.getPublicPokers().size() == 0) {
            this.holdState = betStates;
        }
        else if (this.getPublicPokers().size() == 3) {
            this.flopState = betStates;
        }
        else if (this.getPublicPokers().size() == 4) {
            this.turnState = betStates;
        }
        else {
            this.riverState = betStates;
        }
    }
}
