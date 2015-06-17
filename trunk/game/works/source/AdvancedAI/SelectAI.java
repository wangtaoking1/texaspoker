package AdvancedAI;

import java.util.ArrayList;

import utils.BetState;
import utils.Constants;
import utils.InitState;
import utils.Poker;
import utils.SuperAI;

/**
 * 根据当前局玩家人数来选择不同的AI，小于或等于4人时使用LessAI，大于4人时使用MoreAI
 * 
 * @author wenzhuu
 * 
 */
public class SelectAI extends SuperAI {

	public final static int NUM_6_8 = 0;
	public final static int NUM_4_5 = 1;
	public final static int NUM_3 = 2;
	public final static int NUM_2 = 3;
	private SuperAI ai = null;
//	private MoreAI moreAI = null;
//	private LessAI lessAI = null;
	private int type = -1;	// AI Type

	public SelectAI(String playerID) {
		super(playerID);
	}

	/**
	 * 设置AI类型
	 * @param type
	 */
	public void setAIType(int type) {
	    // 当与之前类型一样的时候，不需重置AI
	    if (this.type == type)
	        return;
		this.type = type;
		if (type == NUM_6_8) {
			this.ai = new MoreAI(this.getPlayerID());
		}
		else if (type == NUM_4_5) {
			this.ai = new LessAI(this.getPlayerID());
		}
		else if (type == NUM_3) {
		    this.ai = new ThreeAI(this.getPlayerID());
		}
		else {
		    this.ai = new TwoAI(this.getPlayerID());
		}
	}
	public int getAIType() {
		return this.type;
	}
	
	public void setPlayerID(String playerID) {
	    this.ai.setPlayerID(playerID);
    }
	
    /**
     * 设置玩家人数
     * @param number
     */
    public void setPlayersNumber(int number) {
    	this.ai.setPlayersNumber(number);
    }
    
    /**
     * 设置该局的盲注金额，即最小押注金额
     * @param blind
     */
    public void setBlind(int blind) {
        this.ai.setBlind(blind);
    }
    
    public int getInitJetton() {
        return this.ai.getInitJetton();
    }
    
    /**
     * 设置剩余筹码，剩余金币，玩家人数，自己的位置，当前局数
     * @param jetton
     * @param money
     */
    public void setInitInfo(int jetton, int money, int playerNum, 
            int position, int handNum, ArrayList<InitState> states) {
        if (playerNum >= 6) {
            this.setAIType(NUM_6_8);
        }
        else if (playerNum >= 4) {
            this.setAIType(NUM_4_5);
        }
        else if (playerNum == 3) {
            this.setAIType(NUM_3);
        }
        else {
            this.setAIType(NUM_2);
        }
        this.ai.setInitInfo(jetton, money, playerNum, position, handNum, states);
    }
    
    public boolean getFolded() {
    	return this.ai.getFolded();
    }
    
    
    public int getTotalJetton() {
        return this.ai.getTotalJetton();
    }
    
    public int getTotalMoney() {
        return this.ai.getTotalMoney();
    }
    
    public int getTotalMoneyAndJetton() {
    	return this.ai.getTotalMoneyAndJetton();
    }
    
    public int getPlayerNum() {
    	return this.ai.getPlayerNum();
    }
    
    public int getActiverPlayerNum() {
        return this.ai.getActiverPlayerNum();
    }
    public boolean IsTheLastOne() {
        return this.ai.IsTheLastOne();
    }
    public String getButtonID() {
        return this.ai.getButtonID();
    }
    public float getWinOnePlayerProb() {
        return this.ai.getWinOnePlayerProb();
    }
    public float getWinAllPlayerProb() {
        return this.ai.getWinAllPlayerProb();
    }
    public int getHandNum() {
    	return this.ai.getHandNum();
    }
    
    public ArrayList<Poker> getHoldPokers() {
    	return this.ai.getHoldPokers();
    }
    
    public ArrayList<Poker> getPublicPokers() {
    	return this.ai.getPublicPokers();
    }
    
    public int getBlind() {
    	return this.ai.getBlind();
    }
    
    public boolean getHasRaised() {
        return this.ai.getHasRaised();
    }
    
    public void setHasRaised(boolean flag) {
        this.ai.setHasRaised(flag);
    }
    
    public void setBetStates(ArrayList<BetState> states) {
        this.ai.setBetStates(states);
    }
    public void setInitStates(ArrayList<InitState> states) {
        this.ai.setInitStates(states);
    }
    
    /**
     * 玩家playerID需要下筹码为jet的盲注
     */
    public void postBlind(String playerID, int jet) {
    	this.ai.postBlind(playerID, jet);
    }
    /**
     * 在发送下注策略之前必须通过该方法获得策略
     * @param diff
     * @param jetton
     * @return
     */
    public String getResponse(int diff, int jetton) {
        return this.ai.getResponse(diff, jetton);
    }
    
    public String callByDiff(int diff, int maxMultiple) {
        return this.ai.callByDiff(diff, maxMultiple);
    }
    
    public String raiseByDiff(int diff, int multiple, int maxMultiple) {
        return this.ai.raiseByDiff(diff, multiple, maxMultiple);
    }
    
    /**
     * 添加两张底牌
     */
    public void addHoldPokers(Poker p1, Poker p2) {
    	this.ai.addHoldPokers(p1, p2);
    }
    
    /**
     * 添加三张公共牌
     */
    public void addFlopPokers(Poker p1, Poker p2, Poker p3) {
    	this.ai.addFlopPokers(p1, p2, p3);
    }
    
    /**
     * 添加转牌
     */
    public void addTurnPoker(Poker p) {
        this.ai.addTurnPoker(p);
    }
    
    /**
     * 添加河牌
     */
    public void addRiverPoker(Poker p) {
        this.ai.addRiverPoker(p);
    }

	@Override
	public String thinkAfterHold(ArrayList<BetState> betStates) {
		return this.ai.thinkAfterHold(betStates);
	}

	@Override
	public String thinkAfterFlop(ArrayList<BetState> betStates) {
		return this.ai.thinkAfterFlop(betStates);
	}

	@Override
	public String thinkAfterTurn(ArrayList<BetState> betStates) {
		return this.ai.thinkAfterTurn(betStates);
	}

	@Override
	public String thinkAfterRiver(ArrayList<BetState> betStates) {
		return this.ai.thinkAfterRiver(betStates);
	}

}
