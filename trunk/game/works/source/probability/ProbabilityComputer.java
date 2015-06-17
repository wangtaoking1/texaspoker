package probability;

import java.util.ArrayList;

import utils.Color;
import utils.MaxCardComputer;
import utils.Poker;

public class ProbabilityComputer {
    /**
     * 赢牌(大于或等于一个玩家)的概率
     * 只能在flop, turn, river三个环节使用
     * 考虑到性能问题，flop和turn环节的概率并不为精确概率
     * @param holds
     * @param publics
     * @return
     */
    public static float computeProbability(ArrayList<Poker> holds, 
            ArrayList<Poker> publics) {
        if (publics.size() == 3)
            return computeFlopProb(holds, publics);
        else if (publics.size() == 4)
            return computeTurnProb(holds, publics);
        else if (publics.size() == 5)
            return computeRiverProb(holds, publics);
        else
            return 0;
    }
    
    
    /**
     * 赢牌(大于或等于所有活跃玩家)的概率
     * 只能在flop, turn, river三个环节使用
     * 考虑到性能问题，flop和turn环节的概率并不为精确概率
     * @param holds
     * @param publics
     * @return
     */
    public static float computeProbability(int activePlayerNum, 
            ArrayList<Poker> holds, ArrayList<Poker> publics) {
        float prob = 0;
        if (publics.size() == 3)
            prob = computeFlopProb(holds, publics);
        else if (publics.size() == 4)
            prob = computeTurnProb(holds, publics);
        else if (publics.size() == 5)
            prob = computeRiverProb(holds, publics);
        else
            prob = 0;
        float res = 1;
        for (int i = 0; i < activePlayerNum - 1; i++)
            res *= prob;
        return res;
    }
    
    private static float computeFlopProb(ArrayList<Poker> holds, 
            ArrayList<Poker> publics) {
        long selfPower = (new MaxCardComputer(holds, publics))
                .getMaxCardGroup().getPower();
        ArrayList<Poker> otherHolds = new ArrayList<Poker>();
        int count = 0;
        for (Color c1: Color.values()) {
            for (int v1 = 2; v1 <= 14; v1++) {
                Poker poker1 = new Poker(c1, v1);
                if (existed(poker1, holds, publics))
                    continue;
                otherHolds.add(poker1);
                for (Color c2: Color.values()) {
                    for (int v2 = 2; v2 <= 14; v2++) {
                        Poker poker2 = new Poker(c2, v2);
                        if (existed(poker2, holds, publics) ||
                                (c2 == c1 && v2 == v1))
                            continue;
                        otherHolds.add(poker2);
                        long power = (new MaxCardComputer(otherHolds, publics))
                                .getMaxCardGroup().getPower();
                        if (selfPower >= power)
                            count++;
                        otherHolds.remove(poker2);
                    }
                }
                otherHolds.remove(poker1);
            }
        }
        return (float) count / (47 * 46);
    }
    
    private static float computeTurnProb(ArrayList<Poker> holds, 
            ArrayList<Poker> publics) {
        long selfPower = (new MaxCardComputer(holds, publics))
                .getMaxCardGroup().getPower();
        ArrayList<Poker> otherHolds = new ArrayList<Poker>();
        int count = 0;
        for (Color c1: Color.values()) {
            for (int v1 = 2; v1 <= 14; v1++) {
                Poker poker1 = new Poker(c1, v1);
                if (existed(poker1, holds, publics))
                    continue;
                otherHolds.add(poker1);
                MaxCardComputer computer1 = new MaxCardComputer(otherHolds, publics);
                for (Color c2: Color.values()) {
                    for (int v2 = 2; v2 <= 14; v2++) {
                        Poker poker2 = new Poker(c2, v2);
                        if (existed(poker2, holds, publics) ||
                                (c2 == c1 && v2 == v1))
                            continue;
                        otherHolds.add(poker2);
                        long power = (new MaxCardComputer(computer1, poker2))
                                .getMaxCardGroup().getPower();
                        if (selfPower >= power)
                            count++;
                        otherHolds.remove(poker2);
                    }
                }
                otherHolds.remove(poker1);
            }
        }
        return (float) count / (46 * 45);
    }
    
    private static float computeRiverProb(ArrayList<Poker> holds, 
            ArrayList<Poker> publics) {
        long selfPower = (new MaxCardComputer(holds, publics))
                .getMaxCardGroup().getPower();
        ArrayList<Poker> otherHolds = new ArrayList<Poker>();
        MaxCardComputer computer = new MaxCardComputer(otherHolds, publics);
        int count = 0;
        for (Color c1: Color.values()) {
            for (int v1 = 2; v1 <= 14; v1++) {
                Poker poker1 = new Poker(c1, v1);
                if (existed(poker1, holds, publics))
                    continue;
                MaxCardComputer computer1 = new MaxCardComputer(computer, poker1);
                for (Color c2: Color.values()) {
                    for (int v2 = 2; v2 <= 14; v2++) {
                        Poker poker2 = new Poker(c2, v2);
                        if (existed(poker2, holds, publics) ||
                                (c2 == c1 && v2 == v1))
                            continue;
                        long power = (new MaxCardComputer(computer1, poker2))
                                .getMaxCardGroup().getPower();
                        if (selfPower >= power)
                            count++;
                    }
                }
            }
        }
        return (float) count / (45 * 44);
    }
    
    /**
     * 判断poker牌是否已经出现过
     * @param poker
     * @param holds
     * @param publics
     * @return
     */
    private static boolean existed(Poker poker, ArrayList<Poker> holds, 
            ArrayList<Poker> publics) {
        for (Poker p: holds) {
            if (poker.getColor() == p.getColor() && 
                    poker.getValue() == p.getValue())
                return true;
        }
        for (Poker p: publics) {
            if (poker.getColor() == p.getColor() && 
                    poker.getValue() == p.getValue())
                return true;
        }
        return false;
    }
}
