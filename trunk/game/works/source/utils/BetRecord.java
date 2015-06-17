package utils;

public class BetRecord {
    private String playerID;    //玩家ID
    private int position;       //玩家的位置
    private int jetton;         //剩余筹码
    private MlCardType type;        //牌型
    private float otherStrategy;  //其他玩家的最大押注
    private float strategy;       //自己的押注
    
    public BetRecord(String playerID, int position, int jetton, 
            MlCardType type, float otherStrategy, float strategy) {
        this.playerID = playerID;
        this.position = position;
        this.jetton = jetton;
        this.type = type;
        this.otherStrategy = otherStrategy;
        this.strategy = strategy;
    }
    
    public String getPlayerID() {
        return this.playerID;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public int getJetton() {
        return this.jetton;
    }
    
    public MlCardType getType() {
        return this.type;
    }
    
    public float getOtherStrategy() {
        return this.otherStrategy;
    }
    
    public float getStrategy() {
        return this.strategy;
    }
}
