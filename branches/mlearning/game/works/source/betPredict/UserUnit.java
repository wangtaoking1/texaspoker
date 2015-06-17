package betPredict;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by makun on 2015/5/31.
 */
public class UserUnit {
    private String playerId = "";
    private int handNum = -1;
    private int position = -1;
    private float jetton = -1;
    private float remainJetton = -1;

    private Pair<String,Pair<Float,Float>> holdCard = null;
    private Pair<String,Pair<Float,Float>> flopCard = null;
    private Pair<String,Pair<Float,Float>> turnCard = null;
    private Pair<String,Pair<Float,Float>> riverCard = null;

    public UserUnit() {}

    public UserUnit(String playerId,int handNum,int position,float jetton) {
        this.playerId = playerId;
        this.handNum = handNum;
        this.position = position;
        this.jetton = jetton;
        this.remainJetton = this.jetton;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setHandNum(int handNum) {
        this.handNum = handNum;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setRemainJetton(float jetton) {
        this.remainJetton = jetton;
    }

    public void setJetton(float jetton) {
        this.jetton = jetton;
        this.remainJetton = this.jetton;
    }

    public void setHoldCard(String type,float strategy) {
        holdCard = new Pair<String,Pair<Float,Float>>(new String (),new Pair<Float,Float>());
        holdCard.first = type;
        holdCard.second.first = strategy;
        float ratio = 1;
        if (strategy != -1) {
            ratio = strategy/this.remainJetton;
            this.remainJetton = this.remainJetton - strategy ;
        }
        else {
            this.remainJetton = 0;
        }
        holdCard.second.second = ratio;
    }

    public void setFlopCard(String type,float strategy) {
        if (this.remainJetton == 0 && strategy != -1) {
            return;
        }
        flopCard = new Pair<String,Pair<Float,Float>>(new String (),new Pair<Float,Float>());
        flopCard.first = type;
        flopCard.second.first = strategy;
        float ratio = 1;
        if (strategy != -1) {
            ratio = strategy/this.remainJetton;
            this.remainJetton = this.remainJetton - strategy ;
        }
        else {
            this.remainJetton = 0;
        }
        flopCard.second.second = ratio;
    }

    public void setTurnCard(String type,float strategy) {
        if (this.remainJetton == 0 && strategy != -1) {
            return;
        }
        turnCard = new Pair<String,Pair<Float,Float>>(new String (),new Pair<Float,Float>());
        turnCard.first = type;
        turnCard.second.first = strategy;
        float ratio = 1;
        if (strategy != -1) {
            ratio = strategy/this.remainJetton;
            this.remainJetton = this.remainJetton - strategy ;
        }
        else {
            this.remainJetton = 0;
        }
        turnCard.second.second = ratio;
    }

    public void setRiverCard(String type,float strategy) {
        if (this.remainJetton == 0 && strategy != -1) {
            return;
        }
        riverCard = new Pair<String,Pair<Float,Float>>(new String (),new Pair<Float,Float>());
        riverCard.first = type;
        riverCard.second.first = strategy;
        float ratio = 1;
        if (strategy != -1) {
            ratio = strategy/this.remainJetton;
            this.remainJetton = this.remainJetton - strategy ;
        }
        else {
            this.remainJetton = 0;
        }
        riverCard.second.second = ratio;
    }

    public Pair<String,Pair<Float,Float>> getHoldCard() {
        return this.holdCard;
    }

    public Pair<String,Pair<Float,Float>> getFlopCard() {
        return this.flopCard;
    }

    public Pair<String,Pair<Float,Float>> getTurnCard() {
        return this.turnCard;
    }

    public Pair<String,Pair<Float,Float>> getRiverCard() {
        return this.riverCard;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public float getJetton() {
        return this.jetton;
    }

    public String toString() {
        String result = "";
        result += "{";
        result += "\"playerId\"="+this.playerId;
        result += ",\"handNum\"="+Integer.toString(this.handNum);
        result += ",\"position\"="+Integer.toString(this.position);
        result += ",\"jetton\"="+Float.toString(this.jetton);
        result += ",\"hold\"="+holdCard;
        result += ",\"flop\"="+flopCard;
        result += ",\"turn\"="+turnCard;
        result += ",\"river\"="+riverCard;
        result += "}\n";
        return result;
    }
}
