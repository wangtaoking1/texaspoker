package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import probability.ProbabilityComputer;

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
    private int initJetton;         //初始总筹码
    private int totalJetton;        //剩余筹码
    private int totalMoney;         //剩余金币
    private int playerNum;          //玩家人数
    private float winProb;          //战胜一个对手的概率
    private ArrayList<String> activePlayers;        //当前没有弃牌的玩家
    private String buttonID;            //庄家ID
    private boolean isTheLastOne;       //是否是最后一个玩家
    private boolean isLastHalf;         //是否是位置偏后的一半玩家
    private boolean hasAllIn;           //是否有all_in娃
//    private int position;           //自己的位置，即押注的顺序
    private int handNum;            //当前局数
    private boolean folded;      //是否已弃牌
    
    private boolean hasRaised;      //标记本环节是否已加过注
    
    private ArrayList<InitState> initStates;    //用于记录所有玩家的初始状态
    private ArrayList<BetState> betStates;     //用于记录所有未弃牌玩家的押注状态
    
//    private BetPredict predictor;               //机器学习算法
    
    
    public SuperAI(String playerID) {
        this.holdPokers = new ArrayList<Poker>();
        this.publicPokers = new ArrayList<Poker>();
        this.activePlayers = new ArrayList<String>();
        this.playerID = playerID;
        this.playerNum = -1;
        this.folded = false;
        
//        this.predictor = BetPredict.getInstance();
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
    
    public int getInitJetton() {
        return this.initJetton;
    }
    
    /**
     * 设置剩余筹码，剩余金币，玩家人数，自己的位置，当前局数
     * @param jetton
     * @param money
     */
    public void setInitInfo(int jetton, int money, int playerNum, 
            int position, int handNum, ArrayList<InitState> states) {
        this.totalJetton = jetton;
        this.totalMoney = money;
        if (this.playerNum != playerNum) {
            this.playerNum = playerNum;
            this.hasAllIn = false;
        }
//        this.position = position;
        this.handNum = handNum;
        if (this.handNum == 1)
            this.initJetton = this.totalJetton + this.totalMoney;
        this.winProb = 0;
        this.holdPokers.clear();
        this.publicPokers.clear();
        this.activePlayers.clear();
        this.initStates = states;
        for (InitState state: this.initStates) {
            this.activePlayers.add(state.getPlayerID());
        }
        this.buttonID = this.activePlayers.get(0);
        if (this.buttonID.equals(this.playerID)) {
            this.isTheLastOne = true;
        }
        else {
            this.isTheLastOne = false;
        }
        this.isLastHalf = false;
        
        this.folded = false;
        
        this.betStates = null;
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
    public int getActiverPlayerNum() {
        return this.activePlayers.size();
    }
    public void computeLastHalf() {
        if (this.isTheLastOne) {
            this.isLastHalf =  true;
            return ;
        }
        
        int count = 1;
        for (String player: this.activePlayers) {
            if (player.equals(this.playerID))
                break;
            if (!player.equals(this.buttonID))
                count ++;
        }
        if (count > this.activePlayers.size() / 2) {
            this.isLastHalf = true;
            return ;
        }
        this.isLastHalf = false;
    }
    public boolean isLastHalf() {
        return this.isLastHalf;
    }
    public boolean IsTheLastOne() {
        return this.isTheLastOne;
    }
    public boolean hasAllIn() {
        return this.hasAllIn;
    }
    public String getButtonID() {
        return this.buttonID;
    }
    public float getWinOnePlayerProb() {
        return this.winProb;
    }
    public float getWinAllPlayerProb() {
        float res = 1;
        for (int i = 0; i < this.getActiverPlayerNum() - 1; i++)
            res *= this.winProb;
        return res;
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
    
    public void setBetStates(ArrayList<BetState> states) {
        this.betStates = states;
        
        for (BetState state: states) {
            if (state.getAction().equals("fold"))
                this.activePlayers.remove(state.getPlayerID());
        }
        
        if (this.buttonID.equals(this.playerID)) {
            this.isTheLastOne = true;
        }
        else if (!this.activePlayers.get(0).equals(this.buttonID) && 
                this.activePlayers.get(this.activePlayers.size() - 1)
                .equals(this.playerID)) {
            this.isTheLastOne = true;
        }
        
        this.computeLastHalf();
        
        if (!this.hasAllIn && this.publicPokers.size() == 0) {
            for (BetState state: states) {
                if (state.getAction().equals("all_in")) {
                    this.hasAllIn = true;
                    break;
                }
            }
        }
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
     * 在发送下注策略之前必须通过该方法获得策略
     * @param diff
     * @param jetton
     * @return
     */
    public String getResponse(int diff, int jetton) {
        if (this.activePlayers.size() == 1) {
            return "check";
        }
        
        if(this.getHasRaised() && jetton > diff) {
            jetton = diff;
        }
        
        if (jetton == 0 && diff > 0) {
            return "fold";
        }
        else if (jetton == 0 && diff == 0) {
            return "check";
        }
        else if (jetton >= this.getTotalJetton()) {
            this.totalJetton = 0;
            this.setHasRaised(true);
            return "all_in";
        }
        else if (jetton == diff) {
            this.totalJetton -= jetton;
            return "call";
        }
        else if (jetton > diff) {
            Random random = new Random();
            int sum = jetton + (random.nextInt(5) + 1);
            this.totalJetton -= sum;
            this.setHasRaised(true);
            return "raise " + (sum);
        }
        else {
            this.totalJetton -= diff;
            return "call";
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
        
        this.winProb = ProbabilityComputer.computeProbability(
                this.holdPokers, this.publicPokers);
        
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
        this.winProb = ProbabilityComputer.computeProbability(
                this.holdPokers, this.publicPokers);
        
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
        
        this.winProb = ProbabilityComputer.computeProbability(
                this.holdPokers, this.publicPokers);
        
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
     * 跟注
     * 
     * @param diff
     * @param maxMultiple 最大可容忍跟注倍数
     * @return
     */
    public String callByDiff(int diff, int maxMultiple) {       
        if (this.getPublicPokers().size() == 0) {
            ArrayList<Poker> hp = this.getHoldPokers();
            if (this.isHoldBigPair(hp)) {
                if (hp.get(0).getValue() >= 12)
                    return this.getResponse(diff, diff);
                else {
                    if (diff >= this.getTotalJetton())
                        return this.getResponse(diff, 0);
                    return this.getResponse(diff, diff);
                }
            }
            
            if (diff > maxMultiple * this.getBlind())
                return this.getResponse(diff, 0);
            
            return this.getResponse(diff, diff);
        }
        else if (this.getPublicPokers().size() == 3) {
            float prob = this.getWinAllPlayerProb();
            
            if (prob < 0.10f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 4 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 6 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.75f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 8 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.95f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 2 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 10 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.98f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                if (diff > 15 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.99f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                if (diff > 30 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            return this.getResponse(diff, diff);
        }
        else if (this.getPublicPokers().size() == 4) {
            float prob = this.getWinAllPlayerProb();
            
            if (prob < 0.20f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 4 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 8 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.75f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 >= this.getTotalMoneyAndJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 10 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.95f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 2 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 15 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.97f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 15 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.985f) {
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 30 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            return this.getResponse(diff, diff);
        }
        else if (this.getPublicPokers().size() == 5) {
            float prob = this.getWinAllPlayerProb();
            
            if (prob < 0.25f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 4 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 5 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.80f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 6 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.95f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 2 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 8 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.97f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 12 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.985f) {
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 50 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            return this.getResponse(diff, diff);
        }
        return this.getResponse(diff, diff);
    }
    
    /**
     * 加注：加注金额为mutiple * blind
     * 
     * @param diff
     *            根据前面玩家下注，需要跟注的最小数量
     * @param multiple
     * @param maxMultiple 最大可接受倍数
     * @return
     */
    public String raiseByDiff(int diff, int multiple, int maxMultiple) {
        // 本环节已经加过注，则选择跟注
        if (this.getHasRaised())
            return this.callByDiff(diff, maxMultiple);
        
        if (this.getPublicPokers().size() == 0) {
            ArrayList<Poker> hp = this.getHoldPokers();
            if (this.isHoldBigPair(hp)) {
                if (hp.get(0).getValue() >= 12)
                    return this.getResponse(diff, multiple * this.getBlind());
                else {
                    if (diff >= this.getTotalJetton())
                        return this.getResponse(diff, 0);
                    return this.getResponse(diff, multiple * this.getBlind());
                }
            }
            
            if (diff > maxMultiple * this.getBlind())
                return this.getResponse(diff, 0);
            
            return this.getResponse(diff, multiple * this.getBlind());
        }
        else if (this.getPublicPokers().size() == 3) {
            float prob = this.getWinAllPlayerProb();
            
            if (prob < 0.10f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.50f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 4 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 5 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                multiple = 1;
            }
            else if (prob < 0.75f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 6 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                multiple = 2;
            }
            else if (prob < 0.95f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 2 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 10 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.97f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 25 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.985f) {
                if (diff > 50 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
            }
            else {
                return this.getResponse(diff, 2 * multiple * this.getBlind());
            }
            return this.getResponse(diff, multiple * this.getBlind());
        }
        else if (this.getPublicPokers().size() == 4) {
            float prob = this.getWinAllPlayerProb();
            
            if (prob < 0.20f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.60f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 4 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 5 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                multiple = 1;
            }
            else if (prob < 0.80f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 6 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                multiple = 2;
            }
            else if (prob < 0.96f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 8 * this.getBlind())
                    return this.getResponse(diff, 0);
                multiple = 3;
            }
            else if (prob < 0.97f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 2 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 20 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.985f) {
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 50 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                return this.getResponse(diff, 2 * multiple * this.getBlind());
            }
            else {
                return this.getResponse(diff, this.getTotalJetton());
            }
            return this.getResponse(diff, multiple * this.getBlind());
        }
        else if (this.getPublicPokers().size() == 5) {
            float prob = this.getWinAllPlayerProb();
            
            if (prob < 0.25f) {
                return this.getResponse(diff, 0);
            }
            else if (prob < 0.65f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 4 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 5 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                multiple = 1;
            }
            else if (prob < 0.85f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 8 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                multiple = 2;
            }
            else if (prob < 0.95f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 3 > this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 15 * this.getBlind())
                    return this.getResponse(diff, 0);
                multiple = 3;
            }
            else if (prob < 0.97f) {
                if (diff > maxMultiple * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff * 2 >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 20 * this.getBlind())
                    return this.getResponse(diff, 0);
            }
            else if (prob < 0.985f) {
                if (diff >= 55 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                if (diff >= this.getTotalJetton()) {
                    return this.getResponse(diff, 0);
                }
                
                if (diff > 50 * this.getBlind())
                    return this.getResponse(diff, 0);
                
                return this.getResponse(diff, 2 * multiple * this.getBlind());
            }
            else if (prob < 0.99f) {
                return this.getResponse(diff, 3 * multiple * this.getBlind());
            }
            else {
                return this.getResponse(diff, this.getTotalJetton());
            }
        }
        return this.getResponse(diff, multiple * this.getBlind());
    }

    /**
     * 判断手牌是否是大对：AA, KK, QQ, JJ, 1010等
     * 
     * @param hp
     *            手牌
     * @return 大对返回true, 否则返回false
     */
    private boolean isHoldBigPair(ArrayList<Poker> hp) {
        // 避免出错
        if (hp == null || hp.size() < 2)
            return false;
        // 手牌是大对：AA, KK, QQ, JJ, 1010等
        else if (hp.get(0).getValue() == hp.get(1).getValue()
                && hp.get(0).getValue() >= Constants.MORE_GAP_VALUE)
            return true;
        return false;
    }

//    /**
//     * 处理其他玩家的该局押注情况，用于机器学习
//     * @param holds
//     */
//    public void parseOtherPlayerInfo(HashMap<String, ArrayList<Poker>> holdsMap) {
//        ArrayList<BetRecord> records = new ArrayList<BetRecord>();
//        
//        for (BetState betstate: betStates) {
//            for (InitState initstate: initStates) {
//                if (!betstate.getPlayerID().equals(initstate.getPlayerID()))
//                    continue;
//                String id = betstate.getPlayerID();
//                int pos = this.computePlayerPosition(id);
//                int jet = initstate.getJetton();
//                MlCardType type = this.computeMaxMlCardType(holdsMap.get(id), 
//                        this.getPublicPokers());
//                float ost = this.computeMaxOtherStrategy();
//                float st = this.computeStrategy(id);
//                
//                records.add(new BetRecord(id, pos, jet, type, ost, st));
//                break;
//            }
//        }
//        
//        // 此处与机器学习模块对接
//        for (BetRecord record: records) {
//            this.predictor.addUserUnit(new UserUnit(record.getPlayerID(), 
//                    this.getHandNum(), record.getPosition(), 
//                    (float)record.getJetton() / this.getBlind(), 
//                    record.getType().toString(), record.getOtherStrategy(), 
//                    record.getStrategy()));
//        }
//    }
//    
//    /**
//     * 计算玩家在牌桌的位置，用于机器学习
//     * @param playerID
//     * @return "0|1|2" 分别表示"最后位置|首位|中间位置"
//     */
//    private int computePlayerPosition(String playerID) {
//        int pos = 0;
//        for (int i = 0; i < initStates.size(); i++) {
//            if (initStates.get(i).equals(playerID)) {
//                pos = i;
//                break;
//            }
//        }
//        if (pos == 0) {
//            //最后位置
//            return 2;
//        }
//        else if (pos == 1) {
//            //第一个位置
//            return 0;
//        }
//        else {
//            //中间位置
//            return 1;
//        }
//    }
//    
//    /**
//     * 计算玩家的最大机器学习牌型，用于机器学习
//     * @param holds
//     * @param publics
//     * @return
//     */
//    private MlCardType computeMaxMlCardType(ArrayList<Poker> holds, 
//            ArrayList<Poker> publics) {
//        CardGroup maxGroup = (new MaxCardComputer(holds, publics))
//                .getMaxCardGroup();
//        
//        switch (maxGroup.getType()) {
//        case STRAIGHT_FLUSH:
//        case FOUR_OF_A_KIND:
//        case FULL_HOUSE:
//            return MlCardType.FULL_HOUSE_UP;
//        case FLUSH:
//            return MlCardType.FLUSH;
//        case STRAIGHT:
//            return MlCardType.STRAIGHT;
//        case THREE_OF_A_KIND:
//            return MlCardType.THREE_OF_A_KIND;
//        case TWO_PAIR:
//            if (maxGroup.getPokers().get(0).getValue() >= 9)
//                return MlCardType.HIGH_TWO_PAIR;
//            else
//                return MlCardType.LOW_TWO_PAIR;
//        case ONE_PAIR:
//            if (maxGroup.getPokers().get(0).getValue() >= 9)
//                return MlCardType.HIGH_ONE_PAIR;
//            else
//                return MlCardType.LOW_ONE_PAIR;
//        case HIGH_CARD:
//            return MlCardType.HIGH_CARD;
//        }
//        return MlCardType.HIGH_CARD;
//    }
//    
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
//    
//    /**
//     * 计算玩家的押注策略，用于机器学习
//     * @param playerID
//     * @return
//     */
//    private float computeStrategy(String playerID) {
//        int jet = -1;
//        String st = "";
//        for (BetState state: betStates) {
//            if (!state.getPlayerID().equals(playerID))
//                continue;
//            jet = state.getBet();
//            st = state.getAction();
//        }
//        if (st.equals("all_in"))
//            return -1;
//        else if (st.equals("check"))
//            return 0;
//        else
//            return (float)jet / this.getBlind();
//    }
//    
//    /**
//     * 使用机器学习根据玩家的当前押注情况预测牌型
//     * @param state
//     * @return
//     */
//    public HashMap<MlCardType, Float> computePlayerCardTypeByMl(
//            BetState state) {
//        String id = state.getPlayerID();
//        int pos = this.computePlayerPosition(id);
//        int jet = 0;
//        for (InitState initstate: initStates) {
//            if (initstate.getPlayerID().equals(id))
//                jet = initstate.getJetton();
//        }
//        float ost = this.computeMaxOtherStrategy();
//        float st = this.computeStrategy(id);
//        
//        // TODO: 与机器学习模块对接
//        Set<Pair<String,Float>> res = this.predictor.getPredict(new UserUnit(
//                id, this.getHandNum(), pos, (float)jet / this.getBlind(), 
//                "", ost, st));
//        
//        HashMap<MlCardType, Float> map = new HashMap<MlCardType, Float>();
//        for (Pair<String, Float> pair: res) {
//            switch (pair.first) {
//            case "FULL_HOUSE_UP":
//                map.put(MlCardType.FULL_HOUSE_UP, pair.second);
//                break;
//            case "FLUSH":
//                map.put(MlCardType.FLUSH, pair.second);
//                break;
//            case "STRAIGHT":
//                map.put(MlCardType.STRAIGHT, pair.second);
//                break;
//            case "THREE_OF_A_KIND":
//                map.put(MlCardType.THREE_OF_A_KIND, pair.second);
//                break;
//            case "HIGH_TWO_PAIR":
//                map.put(MlCardType.HIGH_TWO_PAIR, pair.second);
//                break;
//            case "LOW_TWO_PAIR":
//                map.put(MlCardType.LOW_TWO_PAIR, pair.second);
//                break;
//            case "HIGH_ONE_PAIR":
//                map.put(MlCardType.HIGH_ONE_PAIR, pair.second);
//                break;
//            case "LOW_ONE_PAIR":
//                map.put(MlCardType.LOW_ONE_PAIR, pair.second);
//                break;
//            case "HIGH_CARD":
//                map.put(MlCardType.HIGH_CARD, pair.second);
//                break;
//            }
//        }
//        return map;
//    }
}
