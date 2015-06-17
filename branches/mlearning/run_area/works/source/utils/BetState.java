package utils;


/**
 * 玩家的当前押注状态，用于Server的消息询问时
 * @author wangtao
 * 
 */
public class BetState {
    private String playerID;        //玩家ID
    private int jetton;             //手中筹码
    private int money;              //剩余金币数
    private int bet;                //累计投注额
    private String action;          //最近一次action
    
    public BetState(String playerID, int jetton, int money, int bet,
            String action) {
        this.playerID = playerID;
        this.jetton = jetton;
        this.money = money;
        this.bet = bet;
        this.action = action;
    }
    
    public String getPlayerID() {
        return this.playerID;
    }
    
    public int getJetton() {
        return this.jetton;
    }
    
    public int getMoney() {
        return this.money;
    }
    
    public int getBet() {
        return this.bet;
    }
    
    public String getAction() {
        return this.action;
    }
}
