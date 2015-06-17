package utils;

/**
 * 玩家的初始状态
 *
 */
public class InitState {
    private String playerID;        //玩家ID
    private int jetton;             //玩家剩余筹码
    
    public InitState(String playerID, int jetton) {
        this.playerID = playerID;
        this.jetton = jetton;
    }
    
    public String getPlayerID() {
        return this.playerID;
    }
    
    public int getJetton() {
        return this.jetton;
    }
}
