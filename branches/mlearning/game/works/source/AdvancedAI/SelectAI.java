package AdvancedAI;

import java.util.ArrayList;

import utils.BetState;
import utils.Constants;
import utils.Poker;
import utils.SuperAI;

/**
 * 根据当前局玩家人数来选择不同的AI，小于或等于4人时使用LessAI，大于4人时使用MoreAI
 * 
 * @author wenzhuu
 * 
 */
public class SelectAI extends SuperAI {

	public final static int MORE = 0;
	public final static int LESS = 1;
	private MoreAI moreAI = null;
	private LessAI lessAI = null;
	private int type;	// AI Type

	public SelectAI(String playerID) {
		super(playerID);
	}

	/**
	 * 设置AI类型
	 * @param type
	 */
	public void setAIType(int type) {
		this.type = type;
		if (type == MORE) {
			if (moreAI == null)
				moreAI = new MoreAI(this.getPlayerID());
		}
		else if (type == LESS) {
			if (lessAI == null)
				lessAI = new LessAI(this.getPlayerID());
				
		}
	}
	public int getAIType() {
		return this.type;
	}
	
	public void setPlayerID(String playerID) {
    	if (type == MORE)
    		moreAI.setPlayerID(playerID);
    	else if (type == LESS)
    		lessAI.setPlayerID(playerID);
    }
    /**
     * 设置玩家人数
     * @param number
     */
    public void setPlayersNumber(int number) {
    	if (type == MORE)
    		moreAI.setPlayersNumber(number);
    	else if (type == LESS)
    		lessAI.setPlayersNumber(number);
    }
    
    /**
     * 设置该局的盲注金额，即最小押注金额
     * @param blind
     */
    public void setBlind(int blind) {
    	if (type == MORE)
    		moreAI.setBlind(blind);
    	else if (type == LESS)
    		lessAI.setBlind(blind);
    }
    
    /**
     * 设置剩余筹码，剩余金币，玩家人数，自己的位置，当前局数
     * @param jetton
     * @param money
     */
    public void setInitInfo(int jetton, int money, int playerNum, 
            int position, int handNum) {
    	if (playerNum > Constants.PLAYER_NUM) {
    		setAIType(MORE);
    		moreAI.setInitInfo(jetton, money, playerNum, position, handNum);
    	}
        else {
        	setAIType(LESS);
        	lessAI.setInitInfo(jetton, money, playerNum, position, handNum);
        }
    }
    
    public boolean getFolded() {
    	if (type == MORE)
    		return moreAI.getFolded();
    	else
    		return lessAI.getFolded();
    }
    
    
    public int getTotalJetton() {
    	if (type == MORE)
    		return moreAI.getTotalJetton();
    	else
			return lessAI.getTotalJetton();
    }
    public int getTotalMoney() {
    	if (type == MORE)
    		return moreAI.getTotalMoney();
    	else
    		return lessAI.getTotalMoney();
    }
    public int getTotalMoneyAndJetton() {
    	if (type == MORE)
    		return moreAI.getTotalMoneyAndJetton();
    	else
    		return lessAI.getTotalMoneyAndJetton();
    }
    public int getPlayerNum() {
    	if (type == MORE)
    		return moreAI.getPlayerNum();
    	else
    		return lessAI.getPlayerNum();
    }
    public int getHandNum() {
    	if (type == MORE)
    		return moreAI.getHandNum();
    	else
    		return lessAI.getHandNum();
    }
    public ArrayList<Poker> getHoldPokers() {
    	if (type == MORE)
    		return moreAI.getHoldPokers();
    	else
    		return lessAI.getHoldPokers();
    }
    
    public ArrayList<Poker> getPublicPokers() {
    	if (type == MORE)
    		return moreAI.getPublicPokers();
    	else
    		return lessAI.getPublicPokers();
    }
    
    public int getBlind() {
    	if (type == MORE)
    		return moreAI.getBlind();
    	else
    		return lessAI.getBlind();
    }
    
    /**
     * 玩家playerID需要下筹码为jet的盲注
     */
    public void postBlind(String playerID, int jet) {
    	if (type == MORE)
    		moreAI.postBlind(playerID, jet);
    	else
    		lessAI.postBlind(playerID, jet);
    }
    
    /**
     * 添加两张底牌
     */
    public void addHoldPokers(Poker p1, Poker p2) {
    	if (type == MORE)
    		moreAI.addHoldPokers(p1, p2);
    	else
    		lessAI.addHoldPokers(p1, p2);
    }
    
    /**
     * 添加三张公共牌
     */
    public void addFlopPokers(Poker p1, Poker p2, Poker p3) {
    	if (type == MORE)
    		moreAI.addFlopPokers(p1, p2, p3);
    	else
    		lessAI.addFlopPokers(p1, p2, p3);
    }
    
    /**
     * 添加转牌
     */
    public void addTurnPoker(Poker p) {
        if (type == MORE)
            moreAI.addTurnPoker(p);
        else
            lessAI.addTurnPoker(p);
    }
    
    /**
     * 添加河牌
     */
    public void addRiverPoker(Poker p) {
        if (type == MORE)
            moreAI.addRiverPoker(p);
        else
            lessAI.addRiverPoker(p);
    }
    
	@Override
	public String thinkAfterHold(ArrayList<BetState> betStates) {
		if (type == MORE)
			return moreAI.thinkAfterHold(betStates);
		else
			return lessAI.thinkAfterHold(betStates);
	}

	@Override
	public String thinkAfterFlop(ArrayList<BetState> betStates) {
		if (type == MORE)
			return moreAI.thinkAfterFlop(betStates);
		else
			return lessAI.thinkAfterFlop(betStates);
	}

	@Override
	public String thinkAfterTurn(ArrayList<BetState> betStates) {
		if (type == MORE)
			return moreAI.thinkAfterTurn(betStates);
		else
			return lessAI.thinkAfterTurn(betStates);
	}

	@Override
	public String thinkAfterRiver(ArrayList<BetState> betStates) {
		if (type == MORE)
			return moreAI.thinkAfterRiver(betStates);
		else
			return lessAI.thinkAfterRiver(betStates);
	}

}
