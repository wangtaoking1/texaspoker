package AdvancedAI;

import java.util.ArrayList;
import java.util.Random;

import utils.BetState;
import utils.CardGroup;
import utils.Color;
import utils.Constants;
import utils.MaxCardComputer;
import utils.Poker;
import utils.SuperAI;

/**
 * 人数只有2人的时候使用该AI
 * 
 */
public class TwoAI extends SuperAI {
    private static final int PROB_NUM = 80; 

	public TwoAI(String playerID) {
		super(playerID);
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
		if (hp.get(0).getValue() >= Constants.GAP_VALUE
				&& hp.get(1).getValue() >= Constants.GAP_VALUE)
			return true;
		return false;
	}

	private boolean isHoldSmall(ArrayList<Poker> hp) {
		if (hp.get(0).getValue() < Constants.GAP_VALUE
				&& hp.get(1).getValue() < Constants.GAP_VALUE)
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

	private boolean isHoldPair(ArrayList<Poker> hp) {
		if (hp.get(0).getValue() == hp.get(1).getValue())
			return true;
		return false;
	}
	
	/**
	 * 获取公共牌与手牌组成的对子的value
	 * 
	 * @param hp
	 * @param pp
	 * @return
	 */
	private ArrayList<Integer> getHoldPubPairValue(ArrayList<Poker> hp,
			ArrayList<Poker> pp) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < hp.size(); i++) {
			for (int j = 0; j < pp.size(); j++) {
				if (hp.get(i).getValue() == pp.get(j).getValue()) {
					res.add(hp.get(i).getValue());
					break;
				}
			}
		}
		return res;
	}

	/**
	 * 获取公共牌中组成对子的值，返回的ArrayList包含的是这些对子的value
	 * 
	 * @param pp
	 * @return
	 */
	private ArrayList<Integer> getPubPairValue(ArrayList<Poker> pp) {
		int counter[] = new int[15];

		for (int i = 0; i < pp.size(); i++) {
			counter[pp.get(i).getValue()]++;
		}
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 2; i <= 14; i++) {
			if (counter[i] == 2)
				res.add(i);
		}
		return res;
	}

	/**
	 * 获取手牌与公共牌组成对子的值
	 * 
	 * @param hp
	 * @param pp
	 * @return
	 */
	private int getPairValue(ArrayList<Poker> hp, ArrayList<Poker> pp) {
		for (int i = 0; i < hp.size(); i++) {
			for (int j = 0; j < pp.size(); j++) {
				if (hp.get(i).getValue() == pp.get(j).getValue())
					return hp.get(i).getValue();
			}
		}
		return -1;
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
    		// 加注至 MIDDLE_RAISE_MULTIPLE * blind
    	    if (hp.get(0).getValue() >= 12)
                return raiseByDiff(diff, 2, 100);
    	    else
    	        return raiseByDiff(diff, 1, 100);
    	}
    	// 手牌是小对：2~9中的一对
    	else if (this.isHoldSmallPair(hp)) {
    		if (hp.get(0).getValue() >= 7)
    			return callByDiff(diff, 50);
    		else if (hp.get(0).getValue() >= 5)
    		    return callByDiff(diff, 30);
    		else {
    		    return callByDiff(diff, 15);
    		}
    	}
    	// 手牌不相等且都大于GAP_VALUE
    	else if (this.isHoldBig(hp)) {
    	    if (this.isHoldSameColor(hp))
    	        return callByDiff(diff, 20);
    		return callByDiff(diff, 10);
    	}
    	// 手牌其中有一个大于GAP_VALUE
    	else if (hp.get(0).getValue() >= Constants.GAP_VALUE
    			|| hp.get(0).getValue() >= Constants.GAP_VALUE) {
    		// 如果需要下注小于可接受下等下注金额且(手牌同花色(有可能组成同花)或者相差小于4(有可能组成顺子))
    	    if (this.isHoldSameColor(hp) || this.isHoldLessThanFour(hp))
    				return callByDiff(diff, 3);
    		return callByDiff(diff, 1);
    	}
    	// 手牌都小于10
    	else {
    		// 手牌同花色或者相差小于4
    		if (this.isHoldSameColor(hp)) {
    			return callByDiff(diff, 1);
    		}
    	}
    	return this.getResponse(diff, 0);
    }

    /**
     * 发出三张公共牌之后思考策略
     * 
     * @param betStates
     *            各玩家的当前押注状态
     * @return 押注策略 "check|call|raise num|all_in|fold"
     */
    @Override
    public String thinkAfterFlop(ArrayList<BetState> betStates) {
    	ArrayList<Poker> hp = this.getHoldPokers();
    	ArrayList<Poker> pp = this.getPublicPokers();
    	CardGroup maxGroup = (new MaxCardComputer(hp, pp)).getMaxCardGroup();
    
    	int maxBet = this.getMaxBet(betStates);
    	int selfBet = this.getSelfBet(betStates);
    	int diff = (maxBet - selfBet);
    
    	long power = maxGroup.getPower();
    	// 两对
    	if (power > (long) 3 * Math.pow(10, 10)
    			&& power < (long) 4 * Math.pow(10, 10)) {
    	    //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
    		ArrayList<Integer> holdPairValues = this
    				.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
    		// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
    		if (holdPairValues.size() == 1) {
    			// 大对
    			if (holdPairValues.get(0) >= Constants.GAP_VALUE) {
    				return raiseByDiff(diff, 2, 50);
    			}
    			// 小对
    			else {
    				return raiseByDiff(diff, 1, 30); // TODO 加注还是跟注好？
    			}
    		}
    		// 手牌中的两张分别与公共牌中的一张组成对子
    		else if (holdPairValues.size() == 2) {
    			// 两对都是大对，加高倍注
    			if (holdPairValues.get(0) >= Constants.GAP_VALUE
    					&& holdPairValues.get(1) >= Constants.GAP_VALUE)
    				return raiseByDiff(diff, 5, 100);
    			// 其中一个为大对，加中倍注
    			else if (holdPairValues.get(0) >= Constants.GAP_VALUE
    					|| holdPairValues.get(1) >= Constants.GAP_VALUE)
    				return raiseByDiff(diff, 3, 100);
    			// 两对都是小对，加低倍注
    			else
    				return raiseByDiff(diff, 1, 30);
    		}
    	}
    	// 三条
    	else if (power > (long) 4 * Math.pow(10, 10)
    			&& power < (long) 5 * Math.pow(10, 10)) {
    	    //诈唬
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                return this.raiseByDiff(diff, 1, 100);
            }
            
    		// 手牌相等
    		if (hp.get(0).getValue() == hp.get(1).getValue()) {
    			return raiseByDiff(diff, 6, 100); // 加高倍注
    		}
    		// 手牌不相等
    		else {
    			ArrayList<Integer> pairValues = this.getPubPairValue(pp);
    			// 公共牌中有一对，说明三条中有两个是在公共牌里的
    			if (pairValues.size() == 1) {
    				if (pairValues.get(0) >= Constants.GAP_VALUE)
    					return raiseByDiff(diff, 6, 100);
    				else 
    					return raiseByDiff(diff, 5, 100);
    			}
    			// 说明三条是出现在公共牌里
    			else if (pairValues.size() == 0) {
    				// 手牌都是大牌
    				if (this.isHoldBig(hp)) {
    					if (diff <= Constants.LESS_MIDDLE_BET_MULTIPLE
    							* this.getBlind())
    						return raiseByDiff(diff, 2, 100);
    				} else if (hp.get(0).getValue() >= Constants.GAP_VALUE
    						|| hp.get(0).getValue() >= Constants.GAP_VALUE) {
    					return raiseByDiff(diff, 1, 30);
    				} else
    					return callByDiff(diff, 1);
    			}
    		}
    	}
    	// 顺子及以上
    	else if (power > (long) 5 * Math.pow(10, 10)) {
    	    //诈唬
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                return this.raiseByDiff(diff, 2, 100);
            }
            
    		return raiseByDiff(diff, 8, 100);
    	}
    	// 一对
    	else if (power > (long) 2 * Math.pow(10, 10)
    			&& power < (long) 3 * Math.pow(10, 10)) {
    	    //诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
            
    		// 手牌是大对，加中倍注
    		if (isHoldBigPair(hp))
    			return raiseByDiff(diff, 1, 30);
    		// 手牌是小对，跟注
    		else if (isHoldSmallPair(hp)) {
    			return callByDiff(diff, 10);
    		}
    		else {
    			ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
    			// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
    			if (pubPair.size() == 1) {
    				if (this.isHoldBig(hp)) {
    					return callByDiff(diff, 8);
    				}
    				// 手牌中有一个大牌
    				else if (hp.get(0).getValue() >= Constants.GAP_VALUE
    						|| hp.get(1).getValue() >= Constants.GAP_VALUE) {
    					return callByDiff(diff, 5);
    				}
    			}
    			// 说明手牌中的一张牌与公共牌中的一张牌组成对子
    			else if (pubPair.size() == 0) {
    				ArrayList<Integer> pairValues = this.getHoldPubPairValue(
    						hp, pp); // 在这里，pairValues中有且只有一个值
    				// 大对，加低倍注
    				if (pairValues.get(0) >= Constants.GAP_VALUE)
    					return raiseByDiff(diff, 1, 10);
    				// 小对，跟注
    				return callByDiff(diff, 6);
    			}
    		}
    	}
    	// 同花或顺子差一张
    	else if (this.computeFlush(hp, pp) <= 1
    			|| this.computeStraight(hp, pp) <= 1) {
    	  //诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
            
    		if (this.isHoldBig(hp)) {
    			return callByDiff(diff, 5);
    		}
    		return callByDiff(diff, 3);
    	}
    
    	// 高牌
    	else if (this.isHoldBig(hp)) {
    	  //诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
    		return callByDiff(diff, 10);
    	}
    	//诈唬
        if (this.IsTheLastOne() && diff <= 0) {
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM) {
                return this.raiseByDiff(diff, random.nextInt(3) + 2, 2);
            }
        }
    	
    	return this.getResponse(diff, 0);
    }

    /**
	 * 发出一张转牌之后思考策略
	 * 
	 * @param betStates
	 *            各玩家的当前押注状态
	 * @return 押注策略 "check|call|raise num|all_in|fold"
	 */
	@Override
	public String thinkAfterTurn(ArrayList<BetState> betStates) {
		ArrayList<Poker> hp = this.getHoldPokers();
		ArrayList<Poker> pp = this.getPublicPokers();
		CardGroup maxGroup = (new MaxCardComputer(hp, pp)).getMaxCardGroup();

		int maxBet = this.getMaxBet(betStates);
		int selfBet = this.getSelfBet(betStates);
		int diff = (maxBet - selfBet);

		long power = maxGroup.getPower();
		
		// 两对
		if (power > (long) 3 * Math.pow(10, 10)
				&& power < (long) 4 * Math.pow(10, 10)) {
		  //诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
			ArrayList<Integer> holdPairValues = this
					.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
			// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
			if (holdPairValues.size() == 1) {
				// 大对
				if (holdPairValues.get(0) >= Constants.GAP_VALUE) {
					return raiseByDiff(diff, 3, 30);
				}
				// 小对
				return raiseByDiff(diff, 1, 15); // TODO 加注还是跟注好？
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加中倍注
				if (holdPairValues.get(0) >= Constants.GAP_VALUE
						&& holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff, 6, 50);
				// 其中一个为大对，加低倍注
				else if (holdPairValues.get(0) >= Constants.GAP_VALUE
						|| holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff, 3, 30);
				return raiseByDiff(diff, 1, 10);
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
		  //诈唬
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                return this.raiseByDiff(diff, 3, 100);
            }
            
			// 手牌相等
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				if (hp.get(0).getValue() >= Constants.GAP_VALUE)
					return raiseByDiff(diff, 10, 100); // 加高倍注
				else
					return raiseByDiff(diff, 8, 100); // 加中倍注
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1) {
					if (pairValues.get(0) >= Constants.GAP_VALUE)
						return raiseByDiff(diff, 8, 100);
					else 
						return raiseByDiff(diff, 6, 100);
				}
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					// 手牌都是大牌
					if (this.isHoldBig(hp)) {
					    return callByDiff(diff, 15);
					}
					return callByDiff(diff, 1);
				}
			}
		}
		// 顺子及以上
		else if (power > (long) 5 * Math.pow(10, 10)) {
//			return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE); // 加高倍注
		    //诈唬
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                return this.raiseByDiff(diff, 4, 100);
            }
            
		    return raiseByDiff(diff, 15, 1000);
		}
		// 一对
		else if (power > (long) 2 * Math.pow(10, 10)
				&& power < (long) 3 * Math.pow(10, 10)) {
		  //诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
			// 手牌是大对，加低倍注
			if (isHoldBigPair(hp))
				return raiseByDiff(diff, 2, 10);
			// 手牌是小对
			else if (isHoldSmallPair(hp)) {
				return callByDiff(diff, 6);
			}
			else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					if (this.isHoldBig(hp)) {
						return callByDiff(diff, 1);
					}
					// 手牌中有一个大牌
					else if (hp.get(0).getValue() >= Constants.GAP_VALUE
							|| hp.get(1).getValue() >= Constants.GAP_VALUE) {
						return callByDiff(diff, 1);
					}
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
							hp, pp); // 在这里，pairValues中有且只有一个值
					// 大对，加低倍注
					if (pairValues.get(0) >= Constants.GAP_VALUE)
						return raiseByDiff(diff, 1, 8);
					// 小对，跟注
					return callByDiff(diff, 2);
				}
			}
		}
		
		// 同花或顺子差一张
		else if (this.computeFlush(hp, pp) <= 1
				|| this.computeStraight(hp, pp) <= 1) {
		    //诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
			if (this.isHoldBig(hp)) {
				return callByDiff(diff, 3);
			}
			return callByDiff(diff, 1);
		}
	
		// 高牌
		else if (this.isHoldBig(hp)) {
		  //诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
			return callByDiff(diff, 1);
		}
		//诈唬
        if (this.IsTheLastOne() && diff <= 0) {
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM) {
                return this.raiseByDiff(diff, random.nextInt(3) + 2, 2);
            }
        }
		
		return this.getResponse(diff, 0);
	}

	/**
	 * 发出一张河牌之后思考策略
	 * 
	 * @param betStates
	 *            各玩家的当前押注状态
	 * @return 押注策略 "check|call|raise num|all_in|fold"
	 */
	@Override
	public String thinkAfterRiver(ArrayList<BetState> betStates) {
		ArrayList<Poker> hp = this.getHoldPokers();
		ArrayList<Poker> pp = this.getPublicPokers();
		CardGroup maxGroup = (new MaxCardComputer(hp, pp)).getMaxCardGroup();

		int maxBet = this.getMaxBet(betStates);
		int selfBet = this.getSelfBet(betStates);
		int diff = (maxBet - selfBet);

		long power = maxGroup.getPower();
		// 两对
		if (power > (long) 3 * Math.pow(10, 10)
				&& power < (long) 4 * Math.pow(10, 10)) {
			ArrayList<Integer> holdPairValues = this
					.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
			// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
			//诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
            
			if (holdPairValues.size() == 1) {
				// 大对
				if (holdPairValues.get(0) >= Constants.GAP_VALUE) {
					return raiseByDiff(diff, 5, 30);
				}
				// 小对
				return raiseByDiff(diff, 3, 5); // TODO 加注还是跟注好？
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加中倍注
				if (holdPairValues.get(0) >= Constants.GAP_VALUE
						&& holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff, 10, 50);
				// 其中一个为大对，加低倍注
				else if (holdPairValues.get(0) >= Constants.GAP_VALUE
						|| holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff, 5, 30);
				return raiseByDiff(diff, 3, 15);
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {    
			// 手牌相等
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				if (hp.get(0).getValue() >= Constants.GAP_VALUE)
					return raiseByDiff(diff, 15, 50); // 加高倍注
				else
					return raiseByDiff(diff, 12, 30); // 加中倍注
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1) {
					if (pairValues.get(0) >= Constants.GAP_VALUE)
						return raiseByDiff(diff, 15, 50);
					else 
						return raiseByDiff(diff, 10, 30);
				}
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					// 手牌都是大牌
					if (this.isHoldBig(hp)) {
						return callByDiff(diff, 6);
					}
					return callByDiff(diff, 1);
				}
			}
		}
		// 顺子及以上
		else if (power > (long) 5 * Math.pow(10, 10)) {
//			return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE); // 加高倍注
            
		    return raiseByDiff(diff, 15, 100);
		}
		// 一对
		else if (power > (long) 2 * Math.pow(10, 10)
				&& power < (long) 3 * Math.pow(10, 10)) {
		    //诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
			// 手牌是大对，加低倍注
			if (isHoldBigPair(hp))
				return raiseByDiff(diff, 2, 20);
			// 手牌是小对
			else if (isHoldSmallPair(hp)) {
				return callByDiff(diff, 5);
			} 
			else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					return callByDiff(diff, 4);
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
//					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
//							hp, pp); // 在这里，pairValues中有且只有一个值
//					// 大对，加低倍注
//					if (pairValues.get(0) >= Constants.GAP_VALUE)
//						return raiseByDiff(diff,
//								Constants.LESS_LOW_RAISE_MULTIPLE);
//					// 小对，跟注
//					else if (diff <= Constants.LESS_MIDDLE_BET_MULTIPLE * this.getBlind())
					return callByDiff(diff, 3);
				}
			}
		}
		//诈唬
        if (this.IsTheLastOne() && diff <= 0) {
            Random random = new Random();
            if (random.nextInt(100) + 1 <= 50) {
                return this.raiseByDiff(diff, random.nextInt(3) + 2, 2);
            }
        }
		return this.getResponse(diff, 0);
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
				&& hp.get(0).getValue() < 10)
			return true;
		return false;
	}

	/**
	 * 计算当前牌组成同花最少还差多少张
	 * 
	 * @param backPokers
	 * @param publicPokers
	 * @return
	 */
	private int computeFlush(ArrayList<Poker> holdPokers,
			ArrayList<Poker> publicPokers) {
		int count[] = new int[4];
		for (Poker p : holdPokers) {
			switch (p.getColor()) {
			case SPADES:
				count[0]++;
				break;
			case HEARTS:
				count[1]++;
				break;
			case CLUBS:
				count[2]++;
				break;
			case DIAMONDS:
				count[3]++;
				break;
			}
		}
		for (Poker p : publicPokers) {
			switch (p.getColor()) {
			case SPADES:
				count[0]++;
				break;
			case HEARTS:
				count[1]++;
				break;
			case CLUBS:
				count[2]++;
				break;
			case DIAMONDS:
				count[3]++;
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
	 * 
	 * @param holdPokers
	 * @param publicPokers
	 * @return
	 */
	private int computeStraight(ArrayList<Poker> holdPokers,
			ArrayList<Poker> publicPokers) {
		boolean visited[] = new boolean[15];
		for (int i = 0; i < visited.length; i++)
			visited[i] = false;

		// 将所有出现的牌值标记
		for (Poker poker : holdPokers) {
			if (poker.getValue() == 14) {
				visited[1] = visited[14] = true;
			} else {
				visited[poker.getValue()] = true;
			}
		}
		for (Poker poker : publicPokers) {
			if (poker.getValue() == 14) {
				visited[1] = visited[14] = true;
			} else {
				visited[poker.getValue()] = true;
			}
		}
		int maxCount = 0;
		for (int i = 1; i <= 10; i++) {
			int count = 0;
			for (int j = 0; j < 5; j++) {
				if (visited[i + j]) {
					count++;
				}
			}
			if (count > maxCount) {
				maxCount = count;
			}
		}

		return 5 - maxCount;
	}

}
