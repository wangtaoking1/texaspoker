package betPredict;


import java.util.*;

/**
 * Created by makun on 2015/5/31.
 */
public class BetPredict {
    Map<String,Set<UserUnit>> userMap = new TreeMap<String,Set<UserUnit>>();    //对于每一个player，每一局的结果集合
    //    Map<String,Map<String,Map<String,Integer>>> cardTypeCounter = new TreeMap<>();      //统计对于每一个player的每一种牌型，在每一局(hold,flop..)中出现的次数
    Map<String,Map<String,Map<String,Pair<Integer,Float>>>> cardTypeRatio = new TreeMap<String,Map<String,Map<String,Pair<Integer,Float>>>>();    //统计每一个用户在每一局中每一个牌型的平均押注比重

    private static BetPredict instance = null;
    public static BetPredict getInstance() {
        if (instance == null) {
            synchronized (BetPredict.class) {
                if (instance == null) {
                    instance = new BetPredict();
                }
            }
        }
        return instance;
    }

    public void addUserUnit(UserUnit userUnit) {
        String playerId = userUnit.getPlayerId();

        if (userMap.containsKey(playerId)) {
            Set<UserUnit> set = userMap.get(playerId);
            set.add(userUnit);
            userMap.put(playerId,set);
        }
        else {
            Set<UserUnit> set = new HashSet<UserUnit>();
            set.add(userUnit);
            userMap.put(playerId,set);
        }

        if (cardTypeRatio.containsKey(playerId)) {
            Map<String,Map<String,Pair<Integer,Float>>> map = cardTypeRatio.get(playerId);
            if (map.containsKey("hold")) {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getHoldCard();
                setUserCardRatio("hold",map,pairPair);
            }
            else {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getHoldCard();
                if (pairPair!=null && pairPair.first != null && pairPair.second !=null) {
                    Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                    map1.put(pairPair.first,new Pair<Integer, Float>(1,pairPair.second.second));
                    map.put("hold",map1);
                }
            }


            if (map.containsKey("flop")) {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getFlopCard();
                setUserCardRatio("flop",map,pairPair);
            }
            else {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getFlopCard();
//                System.out.println("cardTypeAdd:"+map+" "+pairPair);
                if (pairPair!=null && pairPair.first != null && pairPair.second !=null) {
                    Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                    map1.put(pairPair.first,new Pair<Integer, Float>(1,pairPair.second.second));
                    map.put("flop",map1);
                }
            }

            if (map.containsKey("turn")) {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getTurnCard();
                setUserCardRatio("turn",map,pairPair);
            }
            else {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getTurnCard();
                if (pairPair!=null && pairPair.first != null && pairPair.second !=null) {
                    Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                    map1.put(pairPair.first,new Pair<Integer, Float>(1,pairPair.second.second));
                    map.put("turn",map1);
                }
            }

            if (map.containsKey("river")) {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getRiverCard();
                setUserCardRatio("river",map,pairPair);
            }
            else {
                Pair<String,Pair<Float,Float>> pairPair = userUnit.getRiverCard();
                if (pairPair!=null && pairPair.first != null && pairPair.second !=null) {
                    Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                    map1.put(pairPair.first,new Pair<Integer, Float>(1,pairPair.second.second));
                    map.put("river",map1);
                }
            }
        }
        else {
            Map<String,Map<String,Pair<Integer,Float>>> map = new HashMap<String,Map<String,Pair<Integer,Float>>>();

            Pair<String,Pair<Float,Float>> pairPair = userUnit.getHoldCard();
            if (pairPair!=null && pairPair.first != null && pairPair.second !=null) {
                Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                map1.put(pairPair.first,new Pair<Integer, Float>(1,pairPair.second.second));
                map.put("hold",map1);
            }

            Pair<String,Pair<Float,Float>> flopCard = userUnit.getFlopCard();
            if (flopCard!=null && flopCard.first != null && flopCard.second!=null) {
                Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                map1.put(flopCard.first,new Pair<Integer, Float>(1,flopCard.second.second));
                map.put("flop",map1);
            }

            Pair<String,Pair<Float,Float>> turnCard = userUnit.getTurnCard();
            if (turnCard!=null && turnCard.first != null && turnCard.second!=null) {
                Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                map1.put(turnCard.first,new Pair<Integer, Float>(1,turnCard.second.second));
                map.put("turn",map1);
            }

            Pair<String,Pair<Float,Float>> riverCard = userUnit.getRiverCard();
            if (riverCard!=null && riverCard.first != null && riverCard.second!=null) {
                Map<String,Pair<Integer,Float>> map1 = new HashMap<String,Pair<Integer,Float>>();
                map1.put(riverCard.first,new Pair<Integer, Float>(1,riverCard.second.second));
                map.put("river",map1);
            }

            cardTypeRatio.put(playerId,map);
        }
    }

    private void setUserCardRatio(String type,Map<String,Map<String,Pair<Integer,Float>>> map,Pair<String,Pair<Float,Float>> pairPair) {
        Map<String,Pair<Integer,Float>> map1 = map.get(type);
        if (pairPair==null || pairPair.first == null || pairPair.second==null || map1.size()<=0) {
            return;
        }
        if (map1.containsKey(pairPair.first)) {
            Pair<Integer,Float> pair = map1.get(pairPair.first);
            System.out.println("setPair:"+pair+" "+type);
            pair.second = (pair.second*pair.first+pairPair.second.second)/(pair.first+1);
            pair.first += 1;
        }
        else {
            Pair<Integer,Float> pair = new Pair<Integer,Float>();
            pair.first = 1;
            pair.second = pairPair.second.second;
            map1.put(pairPair.first,pair);
        }
    }

    public Set<Pair<String,Float>> getPredict(String playerId,String type,float remainJetton,float strategy) {
        Set<Pair<String,Float>> result = new HashSet<Pair<String,Float>>();
        /**
         * 先选出在押注比重最接近的牌型，
         * 然后根据牌型在选出的牌型里的比重进行概率统计
         */
        if (strategy != -1) {
            strategy = strategy / remainJetton;
        }
        else {
            strategy = 1;
        }
        float min = 100000;
        if (cardTypeRatio.containsKey(playerId)) {
            Map<String,Map<String,Pair<Integer,Float>>> mapMap = cardTypeRatio.get(playerId);
            if (mapMap.containsKey(type)) {
                Map<String,Pair<Integer,Float>> map = mapMap.get(type);
                Set<String> cardSet = new HashSet<String>();
                for (Map.Entry<String,Pair<Integer,Float>> entry : map.entrySet()) {
                    Pair<Integer,Float> pair = entry.getValue();
                    float tmpMin = Math.abs(pair.second - strategy);
                    if (tmpMin<min) {
                        min = tmpMin;
                    }
                }

                //设定误差阈值
                min += 0.05;
                Set<Pair<String,Pair<Integer,Float>>> tmpSet = new HashSet<Pair<String,Pair<Integer,Float>>>();
                int sumCounter = 0;
                for (Map.Entry<String,Pair<Integer,Float>> entry : map.entrySet()) {
                    Pair<Integer,Float> pair = entry.getValue();
                    float tmpMin = Math.abs(pair.second - strategy);
                    if (tmpMin<=min) {
                        tmpSet.add(new Pair<String, Pair<Integer, Float>>(entry.getKey(),pair));
                        sumCounter += pair.first;
                    }
                }

//                System.out.println("sumCounter:"+sumCounter+" strategy:"+strategy);

                for (Pair<String,Pair<Integer,Float>> pairPair : tmpSet) {
                    float tmpStrategy = pairPair.second.second>strategy?pairPair.second.second:strategy;
                    float tmp = (float)pairPair.second.first*(1 - Math.abs(pairPair.second.second-strategy)/(tmpStrategy))/sumCounter;
                    System.out.println("tmpStrategy:"+tmpStrategy+" "+tmp+" "+pairPair.first);
                    result.add(new Pair<String, Float>(pairPair.first,tmp));
                }
            }
        }

        float sum = 0;
        for (Pair<String,Float> pair : result) {
            sum += pair.second;
        }
        sum = (float)1/sum;
        for (Pair<String,Float> pair : result) {
            pair.second = pair.second*sum;
        }

        return result;
    }

    public Map<String,Set<UserUnit>> getUserMap() {
        return this.userMap;
    }

    public Map<String,Map<String,Map<String,Pair<Integer,Float>>>> getCardTypeRatio() {
        return this.cardTypeRatio;
    }

}
