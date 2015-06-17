package simpleAI;

import java.util.ArrayList;
import java.util.Set;

import betPredict.Pair;
import utils.BetState;
import utils.CardGroup;
import utils.CardType;
import utils.Color;
import utils.MaxCardComputer;
import utils.Poker;
import utils.SuperAI;

/**
 * 一个简单的AI算法，单纯地模拟初学者
 * @author wangtao
 *
 */
public class SimpleAI extends SuperAI {  
    public SimpleAI(String playerID) {
        super(playerID);
    }

    @Override
    public String thinkAfterHold(ArrayList<BetState> betStates) {
        for (BetState state: betStates) {
            if (state.getPlayerID().equals(this.getPlayerID()))
                break;
            if (state.getAction().equals("blind"))
                continue;
            System.out.println(state.getPlayerID() + ": ");
            Set<Pair<String,Float>> pairs = this.computePlayerCardTypeByMl(state);
            for (Pair<String, Float> pair: pairs) {
                System.out.println(pair.first + " " + pair.second);
            }
        }
        
        ArrayList<Poker> hp = this.getHoldPokers();
        int diff = this.computeDifference(betStates);
        // minBet表示最少加注到的金额
        // maxBet表示最大可接受的金额，超过则弃牌
        int minBet = 0, maxBet = 0;
        
        if (this.shouldFold(hp)) {
            minBet = 0;
            maxBet = 0;
        }
        else {
            minBet = 0;
            maxBet = 1;
        }
        
        if (this.isHoldBigPair(hp)) {
            if (hp.get(0).getValue() >= 12) {
                if (this.getPlayerNum() > 4) {
                    minBet += 0;
                    maxBet += 100;
                }
                else {
                    minBet += 0;
                    maxBet += 10000;
                }
            }
            else {
                if (this.getPlayerNum() > 4) {
                    minBet += 0;
                    maxBet += 50;
                }
                else {
                    minBet += 0;
                    maxBet += 10000;
                }
            }
        }
        if (this.isHoldFlush(hp) || this.isHoldSmallPair(hp)) {
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 20;
            }
            else {
                minBet += 0;
                maxBet += 50;
            }
        }
        if (this.isHoldTwoHighPokers(hp)) {
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 15;
            }
            else {
                minBet += 0;
                maxBet += 100;
            }
        }
        
        return this.computeReturnStrategy(minBet, maxBet, diff);
    }

    /**
     * 判断底牌是否是大于等于9的大对子
     * @param hp
     * @return
     */
    private boolean isHoldBigPair(ArrayList<Poker> hp) {
        Poker p1 = hp.get(0);
        Poker p2 = hp.get(1);
        if (p1.getValue() == p2.getValue() && p1.getValue() >= 9)
            return true;
        return false;
    }
    
    /**
     * 判断底牌是否是同花
     * @param hp
     * @return
     */
    private boolean isHoldFlush(ArrayList<Poker> hp) {
        Poker p1 = hp.get(0);
        Poker p2 = hp.get(1);
        if (p1.getColor() == p2.getColor())
            return true;
        return false;
    }
    
    /**
     * 判断底牌是否是小对子
     * @param hp
     * @return
     */
    private boolean isHoldSmallPair(ArrayList<Poker> hp) {
        Poker p1 = hp.get(0);
        Poker p2 = hp.get(1);
        if (p1.getValue() == p2.getValue() && p1.getValue() < 9)
            return true;
        return false;
    }
    
    /**
     * 判断底牌是否是两张大于10的大牌
     * @param hp
     * @return
     */
    private boolean isHoldTwoHighPokers(ArrayList<Poker> hp) {
        Poker p1 = hp.get(0);
        Poker p2 = hp.get(1);
        if (p1.getValue() > 10 && p2.getValue() > 10)
            return true;
        return false;
    }
    
    @Override
    public String thinkAfterFlop(ArrayList<BetState> betStates) {
        for (BetState state: betStates) {
            if (state.getPlayerID().equals(this.getPlayerID()))
                break;
            if (state.getAction().equals("blind"))
                continue;
            System.out.println(state.getPlayerID() + ": ");
            Set<Pair<String,Float>> pairs = this.computePlayerCardTypeByMl(state);
            for (Pair<String, Float> pair: pairs) {
                System.out.println(pair.first + " " + pair.second);
            }
        }
        
        ArrayList<Poker> hp = this.getHoldPokers();
        ArrayList<Poker> pp = this.getPublicPokers();
        CardGroup maxGroup = (new MaxCardComputer(hp, pp))
                .getMaxCardGroup();
        
        int diff = this.computeDifference(betStates);
        int minBet = 0, maxBet = 0;
        long MAXN = (long)Math.pow(10, 10);
        
        if (maxGroup.getPower() > 7 * MAXN) {
            // 葫芦及葫芦以上
            if (this.getPlayerNum() > 4) {
                minBet += 50;
                maxBet += 10000;
            }
            else {
                minBet += 100;
                maxBet += 10000;
            }
        }
        else if (maxGroup.getPower() > 5 * MAXN) {
            // 顺子及顺子以上
            if (this.getPlayerNum() > 4) {
                minBet += 10;
                maxBet += 50;
            }
            else {
                minBet += 30;
                maxBet += 10000;
            }
        }
        else if (maxGroup.getPower() > 3 * MAXN) {
            // 两对及两对以上
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 30;
            }
            else {
                minBet += 5;
                maxBet += 10000;
            }
        }
        else if (this.isOneMaxPair(maxGroup, hp)) {
            // 大对子
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 20;
            }
            else {
                minBet += 3;
                maxBet += 100;
            }
        }
        else if (maxGroup.getPower() > 2 * MAXN) {
            // 小对子
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 5;
            }
            else {
                minBet += 0;
                maxBet += 10;
            }
        }
        else if (this.isHoldTwoHighPokers(hp)){
            // 单牌，但是两张底牌比较大
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 5;
            }
            else {
                minBet += 0;
                maxBet += 10;
            }
        }
        else {
            // 小的单牌
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 3;
            }
            else {
                minBet += 0;
                maxBet += 5;
            }
        }
        
        if (this.computeFlush(hp, pp) == 1 || 
                this.computeStraight(hp, pp) == 1) {
            // 同花或顺子差一张
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 30;
            }
            else {
                minBet += 0;
                maxBet += 10000;
            }
        }
        if (this.computeFlush(hp, pp) == 2 || 
                this.computeStraight(hp, pp) == 2){
            // 同花或顺子差两张
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 3;
            }
            else {
                minBet += 0;
                maxBet += 5;
            }
        }
        
        return this.computeReturnStrategy(minBet, maxBet, diff);
    }

    @Override
    public String thinkAfterTurn(ArrayList<BetState> betStates) {
        for (BetState state: betStates) {
            if (state.getPlayerID().equals(this.getPlayerID()))
                break;
            if (state.getAction().equals("blind"))
                continue;
            System.out.println(state.getPlayerID() + ": ");
            Set<Pair<String,Float>> pairs = this.computePlayerCardTypeByMl(state);
            for (Pair<String, Float> pair: pairs) {
                System.out.println(pair.first + " " + pair.second);
            }
        }
        
        ArrayList<Poker> hp = this.getHoldPokers();
        ArrayList<Poker> pp = this.getPublicPokers();
        CardGroup maxGroup = (new MaxCardComputer(hp, pp))
                .getMaxCardGroup();
        
        int diff = this.computeDifference(betStates);
        int minBet = 0, maxBet = 0;
        long MAXN = (long)Math.pow(10, 10);
        
        if (maxGroup.getPower() > 7 * MAXN) {
            // 葫芦及葫芦以上
            if (this.getPlayerNum() > 4) {
                minBet += 40;
                maxBet += 10000;
            }
            else {
                minBet += 50;
                maxBet += 10000;
            }
        }
        else if (maxGroup.getPower() > 5 * MAXN) {
            // 顺子及顺子以上
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 50;
            }
            else {
                minBet += 20;
                maxBet += 10000;
            }
        }
        else if (maxGroup.getPower() > 3 * MAXN) {
            // 两对及两对以上
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 30;
            }
            else {
                minBet += 3;
                maxBet += 10000;
            }
        }
        else if (this.isOneMaxPair(maxGroup, hp)) {
            // 大对子
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 20;
            }
            else {
                minBet += 3;
                maxBet += 100;
            }
        }
        else if (maxGroup.getPower() > 2 * MAXN) {
            // 小对子
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 5;
            }
            else {
                minBet += 0;
                maxBet += 10;
            }
        }
        else if (this.isHoldTwoHighPokers(hp)){
            // 单牌，但是两张底牌比较大
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 3;
            }
            else {
                minBet += 0;
                maxBet += 10;
            }
        }
        else {
            // 小的单牌
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 1;
            }
            else {
                minBet += 0;
                maxBet += 5;
            }
        }
        
        if (this.computeFlush(hp, pp) == 1 || 
                this.computeStraight(hp, pp) == 1) {
            // 同花或顺子差一张
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 10;
            }
            else {
                minBet += 0;
                maxBet += 15;
            }
        }
        
        return this.computeReturnStrategy(minBet, maxBet, diff);
    }

    @Override
    public String thinkAfterRiver(ArrayList<BetState> betStates) {
        for (BetState state: betStates) {
            if (state.getPlayerID().equals(this.getPlayerID()))
                break;
            if (state.getAction().equals("blind"))
                continue;
            System.out.println(state.getPlayerID() + ": ");
            Set<Pair<String,Float>> pairs = this.computePlayerCardTypeByMl(state);
            for (Pair<String, Float> pair: pairs) {
                System.out.println(pair.first + " " + pair.second);
            }
        }
        
        ArrayList<Poker> hp = this.getHoldPokers();
        ArrayList<Poker> pp = this.getPublicPokers();
        CardGroup maxGroup = (new MaxCardComputer(hp, pp))
                .getMaxCardGroup();
        
        int diff = this.computeDifference(betStates);
        int minBet = 0, maxBet = 0;
        long MAXN = (long)Math.pow(10, 10);
        
        if (maxGroup.getPower() > 7 * MAXN) {
            // 葫芦及葫芦以上
            if (this.getPlayerNum() > 4) {
                minBet += 40;
                maxBet += 10000;
            }
            else {
                minBet += 50;
                maxBet += 10000;
            }
        }
        else if (maxGroup.getPower() > 5 * MAXN) {
            // 顺子及顺子以上
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 50;
            }
            else {
                minBet += 30;
                maxBet += 10000;
            }
        }
        else if (maxGroup.getPower() > 3 * MAXN) {
            // 两对及两对以上
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 30;
            }
            else {
                minBet += 5;
                maxBet += 10000;
            }
        }
        else if (this.isOneMaxPair(maxGroup, hp)) {
            // 大对子
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 10;
            }
            else {
                minBet += 0;
                maxBet += 10000;
            }
        }
        else if (maxGroup.getPower() > 2 * MAXN) {
            // 小对子
            if (this.getPlayerNum() > 4) {
                minBet += 0;
                maxBet += 5;
            }
            else {
                minBet += 0;
                maxBet += 10;
            }
        }
        else {
            // 单牌
            minBet += 0;
            maxBet += 0;
        }
        
        return this.computeReturnStrategy(minBet, maxBet, diff);
    }

    /**
     * 计算自己与其他押最大注玩家的差距，用于决定自己押注的多少
     * @param betStates
     * @return
     */
    private int computeDifference(ArrayList<BetState> betStates) {
        int maxBet = 0, selfBet = 0;
        for (int i = 0; i < betStates.size(); i++) {
            if (betStates.get(i).getBet() > maxBet)
                maxBet = betStates.get(i).getBet();
            if (betStates.get(i).getPlayerID() == this.getPlayerID())
                selfBet = betStates.get(i).getBet();
        }
        return (maxBet - selfBet);
    }
    
    /**
     * 根据两张底牌判断是否应该弃牌
     * @param holdPokers
     * @return
     */
    private boolean shouldFold(ArrayList<Poker> hp) {
        if (hp.get(0).getColor() == hp.get(1).getColor())
            return false;
        int v1 = hp.get(0).getValue();
        int v2 = hp.get(1).getValue();
        // 两张牌都小于10且不可能组成顺子，弃牌
        if ((v1 < 10 && v2 < 10) && Math.abs(v1 - v2) > 4)
            return true;
        
        return false;
    }
    
    /**
     * 计算当前牌组成同花最少还差多少张
     * @param backPokers
     * @param publicPokers
     * @return
     */
    private int computeFlush(ArrayList<Poker> holdPokers, 
            ArrayList<Poker> publicPokers) {
        int count[] = new int[4];
        for (Poker p: holdPokers) {
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
        for (Poker p: publicPokers) {
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
        return 5 - maxCount;
    }
    
    /**
     * 计算当前牌组成顺子最少需要多少张牌
     * @param holdPokers
     * @param publicPokers
     * @return
     */
    private int computeStraight(ArrayList<Poker> holdPokers, 
            ArrayList<Poker> publicPokers) {
        boolean visited[] = new boolean[15];
        for (int i = 0; i < visited.length; i++) 
            visited[i] = false;
        
        //将所有出现的牌值标记
        for (Poker poker: holdPokers) {
            if (poker.getValue() == 14) {
                visited[1] = visited[14] = true;
            }
            else {
                visited[poker.getValue()] = true;
            }
        }
        for (Poker poker: publicPokers) {
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
        
        return 5 - maxCount;
    }
    
    /**
     * 如果为一对，判断是否为最大对
     * @param group
     * @return
     */
    private boolean isOneMaxPair(CardGroup group, ArrayList<Poker> hp) {
        if ((group.getPower() / (long)Math.pow(10, 10)) != 2) {
            return false;
        }
        
        Poker pairPoker = group.getPokers().get(0);
        if (pairPoker.getValue() != hp.get(0).getValue() && 
                pairPoker.getValue() != hp.get(1).getValue())
            return false;

        ArrayList<Poker> pp = this.getPublicPokers();
        for (Poker poker: pp) {
            if (poker.getValue() > pairPoker.getValue())
                return false;
        }
        return true;
    }
    
    /**
     * 根据最小和最大押注金额及差额确定押注策略
     * @param minBet
     * @param maxBet
     * @param diff
     * @return
     */
    private String computeReturnStrategy(int minBet, int maxBet, int diff) {
        minBet *= this.getBlind();
        maxBet *= this.getBlind();
        
        int bet = 0;
        if (diff > maxBet) {
            bet = -1;
        }
        else if (diff <= maxBet && diff >= minBet){
            bet = diff;
        }
        else
            bet = minBet;
        
        if (bet == -1) {
            return "fold";
        }
        else if (bet == 0) {
            return "check";
        }
        else if (bet >= this.getTotalJetton()) {
            return "all_in";
        }
        else if (bet == diff) {
            return "call";
        }
        else if (bet > diff) {
            return "raise " + bet;
        }
        else {
            return "call";
        }
    }
}
