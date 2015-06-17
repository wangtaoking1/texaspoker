package probability;

import java.util.ArrayList;
import java.util.Random;

import utils.BetState;
import utils.Constants;
import utils.Poker;
import utils.SuperAI;

public class ProbAI extends SuperAI {
    private static final int PROB_NUM = 20;         //模仿大牌
    private static final int PROB_NUM_MAX = 50;     //模仿小牌
    
    Random random = null;
    
    public ProbAI(String playerID) {
        super(playerID);
        random = new Random();
    }

    @Override
    public String thinkAfterHold(ArrayList<BetState> betStates) {
        ArrayList<Poker> hp = this.getHoldPokers();
        
        // 计算自己与最大押注的差距，得出需要押注的大小
        int maxBet = this.getMaxBet(betStates);
        int selfBet = this.getSelfBet(betStates);
        int diff = (maxBet - selfBet);
        
        // 如果手牌是大对：AA, KK, QQ, JJ, 1010等
        if (this.isHoldBigPair(hp)) {
         
            
            if (hp.get(0).getValue() >= 12) {
             // 有all_in娃，当不会全下的时候会选择跟注
                if (this.hasAllIn()) {
                    return this.getResponse(diff, diff);
                }
                return raiseByDiff(diff, random.nextInt(3) + 7, 100);
            }
            else {
             // 有all_in娃，当不会全下的时候会选择跟注
                if (this.hasAllIn() && !(this.getTotalMoney() == 0 && diff >= this.getTotalJetton())) {
                    return this.getResponse(diff, diff);
                }
                return raiseByDiff(diff, random.nextInt(3) + 5, 100);
            }
        }
        // 手牌是小对：2~9中的一对
        else if (this.isHoldSmallPair(hp)) {
            if (hp.get(0).getValue() > 5) {               
                //诈唬
                if (!this.hasAllIn() && diff <= this.getBlind()) {
                    if (random.nextInt(100) + 1 <= PROB_NUM) {
                        return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                    }
                }
                return raiseByDiff(diff, 1, 10);
            }
            else {
                //诈唬
                if (!this.hasAllIn() && diff <= this.getBlind()) {
                    if (random.nextInt(100) + 1 <= PROB_NUM) {
                        return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                    }
                }
                return callByDiff(diff, 3);
            }
        }
        // 手牌不相等且都大于GAP_VALUE
        else if (this.isHoldBig(hp)) {
            //有all_in娃，当不会全下的时候会选择跟注
            if (this.hasAllIn() && !(this.getTotalMoney() == 0 && diff >= this.getTotalJetton())) {
                if (hp.get(0).getValue() >= 12 && hp.get(1).getValue() >= 12)
                    return this.getResponse(diff, diff);
            }
            
            //同花
            if (this.isHoldSameColor(hp)) {
              //诈唬
                if (!this.hasAllIn() && diff <= this.getBlind()) {
                    if (random.nextInt(100) + 1 <= PROB_NUM) {
                        return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                    }
                }
                return raiseByDiff(diff, 3, 11);
            }
          //诈唬
            if (!this.hasAllIn() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            return callByDiff(diff, 9);
        }
        // 手牌其中有一个大于GAP_VALUE
        else if (hp.get(0).getValue() >= 12
                || hp.get(0).getValue() >= 12) {
            // 如果需要下注小于可接受下等下注金额且(手牌同花色(有可能组成同花)或者相差小于4(有可能组成顺子))
            if (this.isHoldSameColor(hp) || this.isHoldLessThanFour(hp)) {
                //诈唬
                if (!this.hasAllIn() && diff <= this.getBlind()) {
                    if (random.nextInt(100) + 1 <= PROB_NUM) {
                        return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                    }
                }
                return callByDiff(diff, 2);
            }
            //诈唬
            if (!this.hasAllIn() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            return callByDiff(diff, 1);
        }
        // 手牌都小于10
        else {
            // 手牌同花色或者相差小于4
            if (this.isHoldSameColor(hp)) {
              //诈唬
                if (!this.hasAllIn() && diff <= this.getBlind()) {
                    if (random.nextInt(100) + 1 <= PROB_NUM) {
                        return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                    }
                }
                return callByDiff(diff, 1);
            }
        }
        return this.getResponse(diff, 0);
    }

    @Override
    public String thinkAfterFlop(ArrayList<BetState> betStates) {
        ArrayList<Poker> hp = this.getHoldPokers();
        
        // 计算自己与最大押注的差距，得出需要押注的大小
        int maxBet = this.getMaxBet(betStates);
        int selfBet = this.getSelfBet(betStates);
        int diff = (maxBet - selfBet);
        
        float prob = this.getWinAllPlayerProb();
        
        if (prob < 0.25f) {
            return this.getResponse(diff, 0);
        }
        else if (prob < 0.50f) {
            if (diff >= 3 * this.getBlind())
                return this.getResponse(diff, 0);
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.80f) {
            if (diff >= 5 * this.getBlind())
                return this.getResponse(diff, 0);
            
            //诈唬
            if (this.IsTheLastOne() && diff == 0) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.90f) {
            if (diff >= 9 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
                }
            }
            
          //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.95f) {
            if (diff >= 16 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
                }
            }
            
          //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, (random.nextInt(3) + 3) *this.getBlind());
        }
        else if (prob < 0.99f) {
            if (diff >= 21 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (diff <= 2 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(3) + 8) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
        }
        else if (prob < 0.995f) {
            if (diff >= 26 * this.getBlind()
                    && this.getTotalMoneyAndJetton() > 25 * this.getBlind())
                return this.getResponse(diff, 0);
            
            return this.getResponse(diff, (random.nextInt(3) + 7) * this.getBlind());
        }
        else if (prob < 0.998f) {
            if (diff >= 51 * this.getBlind() 
                    && this.getTotalMoneyAndJetton() > 50 * this.getBlind())
                return this.getResponse(diff, 0);
            
            //诈唬 模仿小牌
            if (diff <= 5 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(5) + 8) * this.getBlind());
        }
        else {
          //诈唬 模仿小牌
            if (diff <= 5 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(5) + 12) * this.getBlind());
        }
    }

    @Override
    public String thinkAfterTurn(ArrayList<BetState> betStates) {
        ArrayList<Poker> hp = this.getHoldPokers();
        
        // 计算自己与最大押注的差距，得出需要押注的大小
        int maxBet = this.getMaxBet(betStates);
        int selfBet = this.getSelfBet(betStates);
        int diff = (maxBet - selfBet);
        
        float prob = this.getWinAllPlayerProb();
        
        if (prob < 0.30f) {
            return this.getResponse(diff, 0);
        }
        else if (prob < 0.70f) {
            if (diff >= 3 * this.getBlind())
                return this.getResponse(diff, 0);
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.80f) {
            if (diff >= 6 * this.getBlind())
                return this.getResponse(diff, 0);
            
            //诈唬
            if (this.IsTheLastOne() && diff == 0) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.90f) {
            if (diff >= 9 * this.getBlind())
                return this.getResponse(diff, 0);
            
            //诈唬
            if (diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
                }
            }
            
          //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
        }
        else if (prob < 0.95f) {
            if (diff >= 16 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(3) + 10) * this.getBlind());
                }
            }
            
          //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 10, 2);
                }
            }
            
            return this.getResponse(diff, (random.nextInt(3) + 7) * this.getBlind());
        }
        else if (prob < 0.98f) {
            if (diff >= 21 * this.getBlind())
                return this.getResponse(diff, 0);
            
            return this.getResponse(diff, 12 * this.getBlind());
        }
        else if (prob < 0.99f) {
            if (diff >= 26 * this.getBlind()
                    && this.getTotalMoneyAndJetton() > 25 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬 模仿小牌
            if (diff <= 5 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.getResponse(diff, (random.nextInt(3) + 3) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(5) + 12) * this.getBlind());
        }
        else if (prob < 0.995f) {
            if (diff >= 51 * this.getBlind() 
                    && this.getTotalMoneyAndJetton() > 50 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬 模仿小牌
            if (diff <= 5 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.getResponse(diff, (random.nextInt(3) + 4) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(8) + 15) * this.getBlind());
        }
        else if (prob < 0.998f){
          //诈唬 模仿小牌
            if (diff <= 5 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
                }
            }
            return this.getResponse(diff, (random.nextInt(15) + 20) * this.getBlind());
        }
        else {
            //诈唬 模仿小牌
              if (diff <= 5 * this.getBlind()) {
                  if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                      return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
                  }
              }
                
                return this.getResponse(diff, (random.nextInt(25) + 25) * this.getBlind());
            }
    }

    @Override
    public String thinkAfterRiver(ArrayList<BetState> betStates) {
        ArrayList<Poker> hp = this.getHoldPokers();
        
        // 计算自己与最大押注的差距，得出需要押注的大小
        int maxBet = this.getMaxBet(betStates);
        int selfBet = this.getSelfBet(betStates);
        int diff = (maxBet - selfBet);
        
        float prob = this.getWinAllPlayerProb();
        
        if (prob < 0.40f) {
            return this.getResponse(diff, 0);
        }
        else if (prob < 0.60f) {
            if (diff >= 3 * this.getBlind())
                return this.getResponse(diff, 0);
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.80f) {
            if (diff >= 6 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.90f) {
            if (diff >= 9 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, diff);
        }
        else if (prob < 0.95f) {
            if (diff >= 16 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM_MAX) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
            return this.getResponse(diff, (random.nextInt(3) + 4) * this.getBlind());
        }
        else if (prob < 0.97f) {
            if (diff >= 26 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (diff <= 2 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(3) + 10) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(3) + 5) * this.getBlind());
        }
        else if (prob < 0.985f) {
            if (diff >= 31 * this.getBlind()
                    && this.getTotalMoneyAndJetton() > 25 * this.getBlind())
                return this.getResponse(diff, 0);
            
            //诈唬
            if (diff <= 2 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(3) + 15) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(3) + 7) * this.getBlind());
        }
        else if (prob < 0.99f) {
            if (diff >= 51 * this.getBlind() 
                    && this.getTotalMoneyAndJetton() > 50 * this.getBlind())
                return this.getResponse(diff, 0);
            
          //诈唬
            if (diff <= 2 * this.getBlind()) {
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.getResponse(diff, (random.nextInt(5) + 20) * this.getBlind());
                }
            }
            
            return this.getResponse(diff, (random.nextInt(5) + 8) * this.getBlind());
        }
        else if (prob < 0.995f) {
            return this.getResponse(diff, (random.nextInt(15) + 15) * this.getBlind());
        }
        else {
            return this.getResponse(diff, (random.nextInt(25) + 30) * this.getBlind());
        }
    }
    
    /**
     * 获取本手牌已下注的玩家下的最大注
     * 
     * @param betStates
     * @return
     */
    private int getMaxBet(ArrayList<BetState> betStates) {
        int maxBet = 0;
        for (int i = 0; i < betStates.size(); i++) {
            if (betStates.get(i).getBet() > maxBet)
                maxBet = betStates.get(i).getBet();
        }
        return maxBet;
    }
    
    /**
     * 获取本手牌自己已下注的筹码
     * 
     * @param betStates
     * @return
     */
    private int getSelfBet(ArrayList<BetState> betStates) {
        for (int i = 0; i < betStates.size(); i++) {
            if (betStates.get(i).getPlayerID().equals(this.getPlayerID()))
                return betStates.get(i).getBet();
        }
        return 0;
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
                && hp.get(0).getValue() >= 10)
            return true;
        return false;
    }
    
    /**
     * 判断手牌是否是小对：2~9中的一对
     * 
     * @param hp
     *            手牌
     * @return 小对返回true，否则返回false
     */
    private boolean isHoldSmallPair(ArrayList<Poker> hp) {
        // 避免出错
        if (hp == null || hp.size() < 2)
            return false;
        // 手牌是大对：AA, KK, QQ, JJ, 1010等
        else if (hp.get(0).getValue() == hp.get(1).getValue()
                && hp.get(0).getValue() < Constants.MORE_GAP_VALUE)
            return true;
        return false;
    }
    
    /**
     * 判断手牌是否相差小于等于4(有可能组成顺子)
     * 
     * @param hp
     * @return
     */
    private boolean isHoldLessThanFour(ArrayList<Poker> hp) {
        // 其中有一张为A
        if (hp.get(0).getValue() == 14 || hp.get(1).getValue() == 14)
            return Math.abs(hp.get(0).getValue() - hp.get(1).getValue()) % 13 <= 4 ? true
                    : false;
        else if (Math.abs(hp.get(0).getValue() - hp.get(1).getValue()) <= 4)
            return true;
        return false;
    }

    /**
     * 手牌都大于或等于10
     * 
     * @param hp
     * @return
     */
    private boolean isHoldBig(ArrayList<Poker> hp) {
        if (hp.get(0).getValue() >= 10
                && hp.get(1).getValue() >= 10)
            return true;
        return false;
    }
    
    /**
     * 判断手牌是否同花色
     * 
     * @param hp
     * @return
     */
    private boolean isHoldSameColor(ArrayList<Poker> hp) {
        if (hp.get(0).getColor() == hp.get(1).getColor())
            return true;
        return false;
    }
}
