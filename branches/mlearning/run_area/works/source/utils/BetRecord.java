package utils;

public class BetRecord {
    private String playerID;    //玩家ID
    private int position;       //玩家的位置
    private float jetton;         //剩余筹码
    private String holdType;    //底牌牌型
    private float holdStrategy;      //底牌圈策略
    private String flopType;    //三张公共牌之后的牌型
    private float flopStrategy;      //公共牌之后的策略
    private String turnType;    //转牌圈牌型
    private float turnStrategy;      //转牌圈策略
    private String riverType;   //河牌圈牌型
    private float riverStrategy;     //河牌圈策略

    
    public BetRecord(String playerID, int position, float jetton) {
        this.playerID = playerID;
        this.position = position;
        this.jetton = jetton;
    }
    
    public void setHoldState(String holdType, float holdStrategy) {
        this.holdType = holdType;
        this.holdStrategy = holdStrategy;
    }
    
    public void setFlopState(String flopType, float flopStrategy) {
        this.flopType = flopType;
        this.flopStrategy = flopStrategy;
    }
    
    public void setTurnState(String turnType, float turnStrategy) {
        this.turnType = turnType;
        this.turnStrategy = turnStrategy;
    }
    
    public void setRiverState(String riverType, float riverStrategy) {
        this.riverType = riverType;
        this.riverStrategy = riverStrategy;
    }
    
    public String getPlayerID() {
        return this.playerID;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public float getJetton() {
        return this.jetton;
    }
    
    public String getHoldType() {
        return this.holdType;
    }
    
    public float getHoldStrategy() {
        return this.holdStrategy;
    }
    
    public String getFlopType() {
        return this.flopType;
    }
    
    public float getFlopStrategy() {
        return this.flopStrategy;
    }
    
    public String getTurnType() {
        return this.turnType;
    }
    
    public float getTurnStrategy() {
        return this.turnStrategy;
    }
    
    public String getRiverType() {
        return this.riverType;
    }
    
    public float getRiverStrategy() {
        return this.riverStrategy;
    }
}
