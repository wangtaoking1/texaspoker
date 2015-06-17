package utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 一套牌（包含5张），可计算该套牌的大小，表示为十进制数abcdef。
 * 1<=a<=9：用1～9分别表示高牌，一对，两对，三条，顺子，同花，葫芦，四条，同花顺
 * 2<=(b-f)<=14：用2～14分别表示牌的点数，从2-10和J-A
 * @author wangtao
 *
 */
public class CardGroup {
    private ArrayList<Poker> pokers;        //5张牌
    private CardType type;      //牌型
    private long power;      //牌的大小
    
    @SuppressWarnings("unchecked")
    public CardGroup(ArrayList<Poker> pokers) {
        assert pokers.size() == 5;
        
        this.pokers = pokers;
        Collections.sort(this.pokers, new PokerComparator());
        this.power = this.computePower();
    }
    
    public long getPower() {
        return this.power;
    }
    public ArrayList<Poker> getPokers() {
        return this.pokers;
    }
    public CardType getType() {
        return this.type;
    }
    
    /**
     * 为当前牌组计算power
     * @return
     */
    private long computePower() {
        //同花顺
        if (this.isFlush() && this.isStraight()) {
            this.type = CardType.STRAIGHT_FLUSH;
            return (long)(9 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        }
        
        //同花
        if (this.isFlush()) {
            this.type = CardType.FLUSH;
            return (long)(6 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        }
        //顺子
        if (this.isStraight()) {
            this.type = CardType.STRAIGHT;
            return (long)(5 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        }
        //其他牌型
        this.type = this.getOtherType();
        switch (this.type) {
        case FOUR_OF_A_KIND:
            return (long)(8 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        case FULL_HOUSE:
            return (long)(7 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        case THREE_OF_A_KIND:
            return (long)(4 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        case TWO_PAIR:
            return (long)(3 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        case ONE_PAIR:
            return (long)(2 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        case HIGH_CARD:
            return (long)(1 * Math.pow(10, 10)) + 
                    this.computeCardPoint();
        default:
            break;
        }
        return 0;
    }
    
    private long computeCardPoint() {
        long res = 0;
        for (Poker p: pokers) {
            res = res * 100 + p.getValue();
        }
        return res;
    }
    
    /**
     * 判断5张牌是否为同花
     * @return
     */
    private boolean isFlush() {
        boolean flag = true;
        for (int i = 1; i < 5; i++) {
            if (pokers.get(i).getColor() != 
                    pokers.get(i - 1).getColor()) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    
    /**
     * 判断5张牌是否为顺子
     * @return
     */
    private boolean isStraight() {
        boolean flag = true;
        for (int i = 1; i < 5; i++) {
            if (pokers.get(i).getValue() != 
                    pokers.get(i - 1).getValue() - 1) {
                flag = false;
                break;
            }
        }
        
        //A5432特殊情况
        if (pokers.get(0).getValue() == 14 && 
                pokers.get(1).getValue() == 5 &&
                pokers.get(2).getValue() == 4 &&
                pokers.get(3).getValue() == 3 &&
                pokers.get(4).getValue() == 2) {
            flag = true;
            //将A移动到最后，方便比较
            Poker pokerA = pokers.get(0);
            for (int i = 0; i < 4; i++) {
                pokers.set(i, pokers.get(i + 1));
            }
            pokers.set(4, pokerA);
        }
        
        return flag;
    }
    
    /**
     * 判断5张牌为同花和顺子以外的其他具体牌型
     * @return
     */
    private CardType getOtherType() {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                if (pokers.get(j).getValue() == pokers.get(i).
                        getValue()) {
                    count += 1;
                }
            }
        }
        if (count == 6) {
            this.changeCardPositionForFourKind();
            return CardType.FOUR_OF_A_KIND;
        }
        else if (count == 4) {
            this.changeCardPositionForFullHouse();
            return CardType.FULL_HOUSE;
        }
        else if (count == 3) {
            this.changeCardPositionForThreeKind();
            return CardType.THREE_OF_A_KIND;
        }
        else if (count == 2) {
            this.changeCardPositionForTwoPair();
            return CardType.TWO_PAIR;
        }
        else if (count == 1) {
            this.changeCardPositionForOnePair();
            return CardType.ONE_PAIR;
        }
        return CardType.HIGH_CARD;
    }
    
    /**
     * 为“四条“牌型修正牌的顺序
     */
    private void changeCardPositionForFourKind() {
        if (pokers.get(0).getValue() != pokers.get(1).getValue()) {
            Poker tmpPoker = pokers.get(0);
            pokers.set(0, pokers.get(4));
            pokers.set(4, tmpPoker);
        }
    }
    
    /**
     * 为“葫芦“牌型修正牌的顺序
     */
    private void changeCardPositionForFullHouse() {
        if (pokers.get(1).getValue() != pokers.get(2).getValue()) {
            Poker tmpPoker = pokers.get(0);
            pokers.set(0, pokers.get(3));
            pokers.set(3, tmpPoker);
            
            tmpPoker = pokers.get(1);
            pokers.set(1, pokers.get(4));
            pokers.set(4, tmpPoker);
        }
    }
    
    /**
     * 为“三条“牌型修正牌的顺序
     */
    private void changeCardPositionForThreeKind() {
        if (pokers.get(0).getValue() != pokers.get(2).getValue()) {
            Poker tmpPoker = pokers.get(0);
            pokers.set(0, pokers.get(3));
            pokers.set(3, tmpPoker);
        }
        if (pokers.get(1).getValue() != pokers.get(2).getValue()) {
            Poker tmpPoker = pokers.get(1);
            pokers.set(1, pokers.get(4));
            pokers.set(4, tmpPoker);
        }
    }
    
    /**
     * 为“两对“牌型修正牌的顺序
     */
    private void changeCardPositionForTwoPair() {
        if (pokers.get(0).getValue() != pokers.get(1).getValue()) {
            Poker tmpPoker = pokers.get(0);
            pokers.set(0, pokers.get(2));
            pokers.set(2, tmpPoker);
        }
        if (pokers.get(2).getValue() != pokers.get(3).getValue()) {
            Poker tmpPoker = pokers.get(2);
            pokers.set(2, pokers.get(4));
            pokers.set(4, tmpPoker);
        }
    }
    
    /**
     * 为“一对“牌型修正牌的顺序
     */
    private void changeCardPositionForOnePair() {
        int c = 0;
        for (; c < 4; c++) {
            if (pokers.get(c).getValue() == pokers.get(c + 1)
                    .getValue()) {
                break;
            }
        }
        Poker tp1 = pokers.get(c);
        Poker tp2 = pokers.get(c + 1);
        for (int i = c - 1; i >= 0; i--) {
            pokers.set(i + 2, pokers.get(i));
        }
        pokers.set(0, tp1);
        pokers.set(1, tp2);
    }
}
