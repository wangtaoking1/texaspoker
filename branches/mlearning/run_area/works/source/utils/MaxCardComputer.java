package utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 用于从当前牌中提取最大牌
 * @author wangtao
 *
 */
public class MaxCardComputer {
    private ArrayList<Poker> pokers;        //自己可用的所有牌
    private CardGroup maxGroup;             //组成最大牌型的五张牌
    
    public MaxCardComputer(ArrayList<Poker> pokers) {
        this.pokers = pokers;
        this.computeMaxCardGroup();
    }
    
    public MaxCardComputer(ArrayList<Poker> holdPokers, 
            ArrayList<Poker> publicPokers) {
        pokers = new ArrayList<Poker>();
        pokers.addAll(holdPokers);
        pokers.addAll(publicPokers);
        
        this.computeMaxCardGroup();
    }
    
    public CardGroup getMaxCardGroup() {
        return this.maxGroup;
    }
    
    /**
     * 计算最大的五张牌
     */
    @SuppressWarnings("unchecked")
    private void computeMaxCardGroup() {
        if (pokers.size() == 5) {
            CardGroup group = new CardGroup(pokers);
            this.maxGroup = group;
        }
        else if (pokers.size() == 6) {
            ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
            for (int i = 0; i < 6; i++) {
                ArrayList<Poker> pokerList = new ArrayList<Poker>(
                        pokers);
                pokerList.remove(i);
                groups.add(new CardGroup(pokerList));
            }
            Collections.sort(groups, new CardGroupComparator());
            this.maxGroup = groups.get(0);
        }
        else if (pokers.size() == 7) {
            ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    if (i == j) continue;
                    ArrayList<Poker> pokerList = new ArrayList<Poker>(
                            pokers);
                    Poker pi = pokerList.get(i);
                    Poker pj = pokerList.get(j);
                    pokerList.remove(pi);
                    pokerList.remove(pj);
                    
                    groups.add(new CardGroup(pokerList));
                }
            }
            Collections.sort(groups, new CardGroupComparator());
            this.maxGroup = groups.get(0);
        }
    
    }
}
