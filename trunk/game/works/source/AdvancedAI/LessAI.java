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
 * 人数6-8人的时候使用该AI
 * 
 * @author wenzhu
 * 
 */
public class LessAI extends SuperAI {
	private final static int BIG_HIGH_CARD_LIMIT	= 2; // 80
	private final static int SMALL_HIGH_CARD_LIMIT 	= 1; // 40
	private final static int BIG_ONE_PAIR_LIMIT 	= 6; // 240
	private final static int SMALL_ONE_PAIR_LIMIT 	= 3; // 120
	private final static int BIG_TWO_PAIR_LIMIT 	= 10; // 400
	private final static int SMALL_TWO_PAIR_LIMIT 	= 8; // 320
	private final static int SMALL_THREE_LIMIT 		= 12; // 480
	private final static int BIG_THREE_LIMIT 		= 15; // 600
	private final static int STRAIGHT_LIMIT 		= 20; // 800
	private final static int FLUSH_LIMIT 			= 20; // 800
	private final static int BIG_FULL_HOUSE_LIMIT 	= 40; // 1600
	private final static int SMALL_FULL_HOUSE_LIMIT = 35; // 1400
	private final static int FOUR_LIMIT 			= 100; // 4000
	private final static int STRAIGHT_FLUSH_LIMIT 	= 100; // 4000
	
	private final static int ACTIVE_PLAYER_NUM = 3;

	private static final int PROB_NUM = 80; 
	
	private int foldCounter = 0; // 用来计算fold的局数

	public LessAI(String playerID) {
		super(playerID);
	}

	private int getIndex(Color color) {
		switch (color) {
		case SPADES:
			return 0;
		case HEARTS:
			return 1;
		case CLUBS:
			return 2;
		case DIAMONDS:
			return 3;
		default:
			break;
		}
		return 0;
	}

	private String fold(ArrayList<BetState> betStates) {
		// String colors[] = { "SPADES", // 黑桃
		// "HEARTS", // 红桃
		// "CLUBS", // 梅花
		// "DIAMONDS" // 方片
		// };
		// String logName = "/home/wenzhu/area/fold_log.txt";
		// try {
		// FileWriter writer = new FileWriter(logName, true);
		// foldCounter++;
		// writer.write("MoreAI" + "\n");
		// writer.write("Playerid:" + this.getPlayerID() + "\n");
		// writer.write("fold " + Integer.toString(foldCounter) + "\n");
		// writer.write("Current Hand Number: "
		// + Integer.toString(this.getHandNum()) + "\n");
		// writer.write("Hold pokers:\n");
		// ArrayList<Poker> hp = this.getHoldPokers();
		// for (int i = 0; i < hp.size(); i++)
		// writer.write(colors[getIndex(hp.get(i).getColor())] + " "
		// + hp.get(i).getValue() + "\n");
		//
		// writer.write("Public pokers:\n");
		// ArrayList<Poker> pp = this.getPublicPokers();
		// for (int i = 0; i < pp.size(); i++)
		// writer.write(colors[getIndex(pp.get(i).getColor())] + " "
		// + pp.get(i).getValue() + "\n");
		//
		// int maxBet = this.getMaxBet(betStates);
		// int selfBet = this.getSelfBet(betStates);
		// int diff = maxBet - selfBet;
		// writer.write("MaxBet: " + Integer.toString(maxBet) + "\n");
		// writer.write("SelfBet " + Integer.toString(selfBet) + "\n");
		// writer.write("Diff: " + Integer.toString(diff) + "\n");
		// writer.write("Rest Jetton: "
		// + Integer.toString(this.getTotalJetton()) + "\n");
		// writer.write("Rest Money: "
		// + Integer.toString(this.getTotalMoney()) + "\n");
		// writer.write("Money and Jetton: "
		// + Integer.toString(this.getTotalMoneyAndJetton()) + "\n");
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// }
		return "fold";
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
			// 前面玩家没有下注，这时下注少一点可以吸引其它玩家跟注
			if (diff == 0) {
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE,
						BIG_ONE_PAIR_LIMIT);
			} else {
				// 针对于唬人的程序，拿到大牌时下更大的注唬它
				if (diff >= 2 * this.getBlind()) {
					if (this.getHandNum() <= 300 && !this.hasAllIn()) // 在前300局唬人的可能性更大，所以这里以300局为分界点
						return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE, 10);
				}
				// 人数少于4人时，可以下稍微大点注
				if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
					return raiseByDiff(diff,
							Constants.LESS_MIDDLE_RAISE_MULTIPLE,
							BIG_ONE_PAIR_LIMIT);
				else
					return raiseByDiff(diff,
							Constants.LESS_LOW_RAISE_MULTIPLE,
							BIG_ONE_PAIR_LIMIT);
			}
		}
		// 手牌是小对：2~9中的一对
		else if (this.isHoldSmallPair(hp)) {
			if (diff == 0)
				return raiseByDiff(diff, 1,
						SMALL_ONE_PAIR_LIMIT);
			else {
				if (diff >= 2 * this.getBlind() && !this.hasAllIn()) {
					// 在前300局唬人的可能性更大，所以这里以300局为分界点
					if (this.getHandNum() <= 300) {
						if ((int) Math.random() * 100 <= 10)
							return raiseByDiff(diff, (int)(Math.random() * 2) + 4 , 10);
					}
				}
				if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
					return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE,
							SMALL_ONE_PAIR_LIMIT);
				else
					return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
			}
		}

		else if (this.isHoldBig(hp)) {
			if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM) {
				// 20%概率下注160，唬人的
				if ((int) Math.random() * 100 <= 10 && !this.hasAllIn()) {
					return raiseByDiff(diff,
							Constants.LESS_MIDDLE_RAISE_MULTIPLE, 4);
				}
			}
			return callByDiff(diff, BIG_HIGH_CARD_LIMIT);
		}

		// 如果剩余金币比较少，弃牌
		if (diff > 0
				&& this.getTotalMoneyAndJetton() * Constants.MAX_FOLD_MULTIPLE < this
						.getInitJetton())
			return fold(betStates);

		// 手牌不相等且都大于LESS_GAP_VALUE
		else if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
				|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE) {
			return callByDiff(diff, SMALL_HIGH_CARD_LIMIT);
		}
		// 手牌都小于LESS_GAP_VALUE
		else {
			// 手牌同花色或者相差小于4
			if (this.isHoldSameColor(hp) || this.isHoldLessThanFour(hp)) {
				if (hp.get(0).getValue() >= 6 && hp.get(1).getValue() >= 6)
					return callByDiff(diff, 1);
			}
		}
		if (diff == 0)
			return "check";
		return fold(betStates);
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
			// 诈唬
			if (this.IsTheLastOne() && diff <= this.getBlind()) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(5) + 5, 2);
                }
            }
			
			ArrayList<Integer> holdPairValues = this
					.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
			// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
			if (holdPairValues.size() == 1) {
				// 大对
				if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE) {
					if (diff == 0) {
						return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
					} else {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
						else
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
					}
				}
				// 小对
				else {
					if (diff == 0) {
						if (hp.get(0).getValue() >= 6)
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, SMALL_ONE_PAIR_LIMIT);
						else
							return raiseByDiff(diff, 1, 2);
					}
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM) {
						if (hp.get(0).getValue() >= 6)
							return raiseByDiff(diff, 1, 2);
						else
							return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
					}
					else
						return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
				}
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加高倍注
				if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE
						&& holdPairValues.get(1) >= Constants.LESS_GAP_VALUE)
					return raiseByDiff(diff,
							Constants.LESS_LOW_RAISE_MULTIPLE,
							BIG_TWO_PAIR_LIMIT);
				// 其中一个为大对，加中倍注
				else if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE
						|| holdPairValues.get(1) >= Constants.LESS_GAP_VALUE) {
					if (diff == 0)
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								BIG_TWO_PAIR_LIMIT);
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								BIG_TWO_PAIR_LIMIT);
					else
						return callByDiff(diff, BIG_TWO_PAIR_LIMIT);
				}
				// 两对都是小对，跟注
				else {
					if (diff == 0 && this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
							Constants.LESS_LOW_RAISE_MULTIPLE,
							SMALL_TWO_PAIR_LIMIT);
					else
						return callByDiff(diff, SMALL_TWO_PAIR_LIMIT);
				}
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
            
			// 手牌
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE) {
					if (diff == 0)
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								BIG_THREE_LIMIT);
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
								Constants.LESS_MIDDLE_RAISE_MULTIPLE,
								BIG_THREE_LIMIT);
				} else {
					if (diff == 0)
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								SMALL_THREE_LIMIT);
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, SMALL_THREE_LIMIT);
					else
						return callByDiff(diff, SMALL_THREE_LIMIT);	
				}
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1) {
					if (pairValues.get(0) >= Constants.LESS_GAP_VALUE) {
						if (diff == 0) {
							return raiseByDiff(diff,
									Constants.LESS_LOW_RAISE_MULTIPLE,
									BIG_THREE_LIMIT);
						} else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff,
									Constants.LESS_MIDDLE_RAISE_MULTIPLE,
									BIG_THREE_LIMIT);
					} else
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								SMALL_THREE_LIMIT);
				}
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					// 手牌都是大牌
					if (this.isHoldBig(hp)) {
						if (diff == 0) {
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_THREE_LIMIT);
						}
					} else if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
							|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE) {
						return callByDiff(diff, SMALL_THREE_LIMIT);
					} else
						return callByDiff(diff, 1);
				}
			}
		}
		// 顺子
		else if (power > (long) 5 * Math.pow(10, 10) && power < (long) 6 * Math.pow(10, 10)) {
            
			if (this.isHoldBig(hp)) {
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, STRAIGHT_LIMIT);
			}
			else {
				// 一定的概率下大注，可以唬人 
				if ((int)Math.random() * 100 <= 20) {
					return raiseByDiff(diff, Constants.LESS_MAX_RAISE_MULTIPLE, STRAIGHT_LIMIT);
				}
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, STRAIGHT_LIMIT);
			}
		}
		// 同花
		else if (power > (long) 6 * Math.pow(10, 10) && power < (long) 7 * Math.pow(10, 10)) {
		  //诈唬
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                return this.raiseByDiff(diff, 3, 100);
            }
            
		    if (this.isHoldBig(hp)) {
				return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, FLUSH_LIMIT);
			}
			else {
//				// 一定的概率下大注，可以唬人 
//				if ((int)Math.random() * 100 <= 20) {
//					return raiseByDiff(diff, Constants.LESS_MAX_RAISE_MULTIPLE, FLUSH_LIMIT);
//				}
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, FLUSH_LIMIT);
			}
		}
		// 葫芦及以上 
		else if (power > (long) 7 * Math.pow(10, 10)) {
			//诈唬
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                return this.raiseByDiff(diff, 3, 100);
            }
            
			if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM) {
				return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, BIG_FULL_HOUSE_LIMIT);
			}
			else
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE,
					STRAIGHT_LIMIT); // 加中倍注
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
			if (isHoldBigPair(hp)) {
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
			}
			// 手牌是小对，跟注
			else if (isHoldSmallPair(hp)) {
				if (hp.get(0).getValue() >= 6)
					return raiseByDiff(diff, 1, SMALL_ONE_PAIR_LIMIT);
				else
					return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
			} else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					// 手牌中至少有一个大牌
					if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
							|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE) {
						return callByDiff(diff, BIG_HIGH_CARD_LIMIT);
					}
					else
						return callByDiff(diff, 1);
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
							hp, pp); // 在这里，pairValues中有且只有一个值
					// 大对，加低倍注
					if (pairValues.get(0) >= Constants.LESS_GAP_VALUE) {
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE,
									BIG_ONE_PAIR_LIMIT);
					}
					// 小对，跟注
					else if (diff == 0)
						return raiseByDiff(diff, 1, SMALL_ONE_PAIR_LIMIT);
					else
						return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
				}
			}
		}
		// 在当前剩余金币与筹码总和下，下注太多，弃牌
		if (diff > 0
				&& diff * Constants.MAX_TOTAL_MULTIPLE > this
						.getTotalMoneyAndJetton())
			return fold(betStates);
		// 同花或顺子差一张
		else if (this.computeFlush(hp, pp) <= 1
				|| this.computeStraight(hp, pp) <= 1) {
			//诈唬
            if (this.IsTheLastOne() && diff == 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
                }
            }
            
			if (this.isHoldBig(hp)) 
				return callByDiff(diff, 2);
			else if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
					|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE)
				return callByDiff(diff, 1);
			else
				return callByDiff(diff, 1);
		}
		// 剩余筹码比较少，弃牌
		if (diff > 0
				&& this.getTotalMoneyAndJetton() * Constants.MAX_FOLD_MULTIPLE < this
						.getInitJetton())
			return fold(betStates);

		//诈唬
        if (this.IsTheLastOne() && diff == 0) {
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM) {
                return this.raiseByDiff(diff, random.nextInt(3) + 5, 2);
            }
        }
        
		// 有A
		else if (hp.get(0).getValue() == 14 || hp.get(1).getValue() == 14) 
			return callByDiff(diff, 2);
		
		else if (diff == 0)
			return "check";
		return fold(betStates);
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
            if (this.IsTheLastOne() && diff == 0) {
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
				if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE) {
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
						else
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
					}
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
								Constants.LESS_MIDDLE_RAISE_MULTIPLE,
								BIG_ONE_PAIR_LIMIT);
					else
						return callByDiff(diff, BIG_ONE_PAIR_LIMIT);
				}
				// 小对
				else {
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM) {
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, SMALL_ONE_PAIR_LIMIT);
						}
						else
							return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
					}
					else
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								SMALL_ONE_PAIR_LIMIT); 
				}
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加高倍注
				if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE
						&& holdPairValues.get(1) >= Constants.LESS_GAP_VALUE)
					return raiseByDiff(diff,
							Constants.LESS_HIGH_RAISE_MULTIPLE,
							BIG_TWO_PAIR_LIMIT);
				// 其中一个为大对，加中倍注
				else if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE
						|| holdPairValues.get(1) >= Constants.LESS_GAP_VALUE) {
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, BIG_TWO_PAIR_LIMIT);
						else
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_TWO_PAIR_LIMIT);
					}
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, BIG_TWO_PAIR_LIMIT);
					else
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								BIG_TWO_PAIR_LIMIT);
				}
				// 两对都是小对，跟注
				else if (diff == 0)
					return raiseByDiff(diff,
							Constants.LESS_LOW_RAISE_MULTIPLE,
							SMALL_TWO_PAIR_LIMIT);
				else
					return callByDiff(diff, SMALL_TWO_PAIR_LIMIT);
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
            
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE) {
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE, BIG_THREE_LIMIT);
						else
							return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, BIG_THREE_LIMIT);
					} 
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
								Constants.LESS_MIDDLE_RAISE_MULTIPLE,
								BIG_THREE_LIMIT);
					else
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								BIG_THREE_LIMIT);
				} 
				else if (hp.get(0).getValue() >= 6){
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, SMALL_THREE_LIMIT);
						else
							return callByDiff(diff, SMALL_THREE_LIMIT);
					}
					else {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, SMALL_THREE_LIMIT);
						else
							return callByDiff(diff, SMALL_THREE_LIMIT);
					}
				}
				else {
					if (diff == 0)
						return raiseByDiff(diff, 1, SMALL_THREE_LIMIT);
					else
						return callByDiff(diff, SMALL_THREE_LIMIT);
				}
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1) {
					if (pairValues.get(0) >= Constants.LESS_GAP_VALUE) {
						if (diff == 0) {
							if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
								return raiseByDiff(diff,
										Constants.LESS_MIDDLE_RAISE_MULTIPLE,
										BIG_THREE_LIMIT);
							else
								return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_THREE_LIMIT);
						} else {
							if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
								return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_THREE_LIMIT);
							else
								return raiseByDiff(diff, 1, BIG_THREE_LIMIT);
						}
						
					} 
					// 小三条
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								SMALL_THREE_LIMIT);
					else
						return callByDiff(diff, SMALL_THREE_LIMIT);
				}
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
							|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE) {
						return callByDiff(diff, BIG_HIGH_CARD_LIMIT);
					}
				}
			}
		}
		// 顺子
		else if (power > (long) 5 * Math.pow(10, 10) && power < (long) 6 * Math.pow(10, 10)) {
            
			if (this.isHoldBig(hp)) {
				return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, STRAIGHT_LIMIT);
			}
			else {
				// 一定的概率下大注，可以唬人 
				if ((int)Math.random() * 100 <= 20) {
					return raiseByDiff(diff, Constants.LESS_MAX_RAISE_MULTIPLE, STRAIGHT_LIMIT);
				}
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, STRAIGHT_LIMIT);
			}
		}
		
		// 同花
		else if (power > (long) 6 * Math.pow(10, 10) && power < (long) 7 * Math.pow(10, 10)) {
			if (this.isHoldBig(hp)) {
				return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, FLUSH_LIMIT);
			}
			else {
				// 一定的概率下大注，可以唬人 
				if ((int)Math.random() * 100 <= 25) {
					return raiseByDiff(diff, Constants.LESS_MAX_RAISE_MULTIPLE, FLUSH_LIMIT);
				}
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, FLUSH_LIMIT);
			}
		}
		// 葫芦及以上 
		else if (power > (long) 7 * Math.pow(10, 10)) {
			//诈唬
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                return this.raiseByDiff(diff, 5, 100);
            }
			return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE,
				BIG_FULL_HOUSE_LIMIT); // 加中倍注
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
			if (isHoldBigPair(hp)) {
				if (diff == 0) {
					return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
				}
				else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
					return raiseByDiff(diff, 1, BIG_ONE_PAIR_LIMIT);
				else
					return callByDiff(diff, BIG_ONE_PAIR_LIMIT);
			}
			// 手牌是小对，跟注
			else if (isHoldSmallPair(hp)) {
				return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
			} else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					// 手牌中至少有一个大牌
					if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
							|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE) {
						return callByDiff(diff, 1);
					}
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
							hp, pp); // 在这里，pairValues中有且只有一个值
					// 大对，加低倍注
					if (pairValues.get(0) >= Constants.LESS_GAP_VALUE) {
						if (diff == 0) {
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
						}
						else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, 1, BIG_ONE_PAIR_LIMIT);
						else
							return callByDiff(diff, BIG_ONE_PAIR_LIMIT);
					}
					// 小对，跟注
					else 
						return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
				}
			}
		}
		// 在当前剩余金币与筹码总和下，下注太多，弃牌
		if (diff > 0
				&& diff * Constants.MAX_TOTAL_MULTIPLE > this
						.getTotalMoneyAndJetton())
			return fold(betStates);
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
			return callByDiff(diff, 1);
		}
		//诈唬
        if (this.IsTheLastOne() && diff <= 0) {
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM) {
                return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
            }
        }
		// 剩余筹码比较少，弃牌
		if (diff > 0
				&& this.getTotalMoneyAndJetton() * Constants.MAX_FOLD_MULTIPLE < this
						.getInitJetton())
			return fold(betStates);

		else if (diff == 0)
			return "check";
		return fold(betStates);
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
			//诈唬
            if (this.IsTheLastOne() && diff <= 0) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
			ArrayList<Integer> holdPairValues = this
					.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
			// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
			if (holdPairValues.size() == 1) {
				// 大对
				if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE) {
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_ONE_PAIR_LIMIT);
						else
							return callByDiff(diff, BIG_ONE_PAIR_LIMIT);
					}
					else
						return callByDiff(diff, BIG_ONE_PAIR_LIMIT);
				}
				// 小对
				else {
						return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
				}
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加高倍注
				if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE
						&& holdPairValues.get(1) >= Constants.LESS_GAP_VALUE)
					return raiseByDiff(diff,
							Constants.LESS_HIGH_RAISE_MULTIPLE,
							BIG_TWO_PAIR_LIMIT);
				// 其中一个为大对，加中倍注
				else if (holdPairValues.get(0) >= Constants.LESS_GAP_VALUE
						|| holdPairValues.get(1) >= Constants.LESS_GAP_VALUE) {
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, BIG_TWO_PAIR_LIMIT);
						else
							return callByDiff(diff, BIG_TWO_PAIR_LIMIT);
					}
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_TWO_PAIR_LIMIT);
					else
						return callByDiff(diff, BIG_TWO_PAIR_LIMIT);
				}
				// 两对都是小对，跟注
				else
					return callByDiff(diff, SMALL_TWO_PAIR_LIMIT);
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
			//诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE) {
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_MIDDLE_RAISE_MULTIPLE, BIG_THREE_LIMIT);
						else
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_THREE_LIMIT);
					} 
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
								Constants.LESS_MIDDLE_RAISE_MULTIPLE,
								BIG_THREE_LIMIT);
					else
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								BIG_THREE_LIMIT);
				} 
				else if (hp.get(0).getValue() >= 6){
					if (diff == 0) {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, SMALL_THREE_LIMIT);
						else
							return callByDiff(diff, SMALL_THREE_LIMIT);
					}
					else {
						if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
							return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, SMALL_THREE_LIMIT);
						else
							return callByDiff(diff, SMALL_THREE_LIMIT);
					}
				}
				else {
					if (diff == 0)
						return raiseByDiff(diff, 1, SMALL_THREE_LIMIT);
					else
						return callByDiff(diff, SMALL_THREE_LIMIT);
				}
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1) {
					if (pairValues.get(0) >= Constants.LESS_GAP_VALUE) {
						if (diff == 0) {
							if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
								return raiseByDiff(diff,
										Constants.LESS_MIDDLE_RAISE_MULTIPLE,
										BIG_THREE_LIMIT);
							else
								return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_THREE_LIMIT);
						} else {
							if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
								return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, BIG_THREE_LIMIT);
							else
								return raiseByDiff(diff, 1, BIG_THREE_LIMIT);
						}
						
					} 
					// 小三条
					else if (this.getActiverPlayerNum() <= ACTIVE_PLAYER_NUM)
						return raiseByDiff(diff,
								Constants.LESS_LOW_RAISE_MULTIPLE,
								SMALL_THREE_LIMIT);
					else
						return callByDiff(diff, SMALL_THREE_LIMIT);
				}
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
							|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE) {
						return callByDiff(diff, 1);
					}
				}
			}
		}
		// 顺子
		else if (power > (long) 5 * Math.pow(10, 10) && power < (long) 6 * Math.pow(10, 10)) {
			//诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
            
			if (this.isHoldBig(hp)) {
				return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE, STRAIGHT_LIMIT);
			}
			else {
				// 一定的概率下大注，可以唬人 
				if ((int)Math.random() * 100 <= 20) {
					return raiseByDiff(diff, Constants.LESS_MAX_RAISE_MULTIPLE, STRAIGHT_LIMIT);
				}
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, STRAIGHT_LIMIT);
			}
		}
		
		// 同花
		else if (power > (long) 6 * Math.pow(10, 10) && power < (long) 7 * Math.pow(10, 10)) {
			if (this.isHoldBig(hp)) {
				return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE, FLUSH_LIMIT);
			}
			else {
				// 一定的概率下大注，可以唬人 
				if ((int)Math.random() * 100 <= 20) {
					return raiseByDiff(diff, Constants.LESS_MAX_RAISE_MULTIPLE, FLUSH_LIMIT);
				}
				return raiseByDiff(diff, Constants.LESS_LOW_RAISE_MULTIPLE, FLUSH_LIMIT);
			}
		}
		// 葫芦及以上 
		else if (power > (long) 7 * Math.pow(10, 10)) {
			//诈唬
            if (this.IsTheLastOne() && diff <= this.getBlind()) {
                Random random = new Random();
                if (random.nextInt(100) + 1 <= PROB_NUM + 10) {
                    return this.raiseByDiff(diff, random.nextInt(3) + 3, 2);
                }
            }
            
			return raiseByDiff(diff, Constants.LESS_HIGH_RAISE_MULTIPLE,
				BIG_FULL_HOUSE_LIMIT); // 加中倍注
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
            
			// 手牌是大对，加低倍注
			if (isHoldBigPair(hp)) {
				if (diff == 0)
					return raiseByDiff(diff, 1, BIG_ONE_PAIR_LIMIT);
				
				else
					return callByDiff(diff, BIG_ONE_PAIR_LIMIT);
			}
			// 手牌是小对，跟注
			else if (isHoldSmallPair(hp)) {
				return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
			} else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					// 手牌中至少有一个大牌
					if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
							|| hp.get(1).getValue() >= Constants.LESS_GAP_VALUE) {
						return callByDiff(diff, 1);
					}
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
							hp, pp); // 在这里，pairValues中有且只有一个值
					// 大对，加低倍注
					if (pairValues.get(0) >= Constants.LESS_GAP_VALUE) {
						if (diff == 0) {
							return raiseByDiff(diff, 1, BIG_ONE_PAIR_LIMIT);
						}
						else
							return callByDiff(diff, BIG_ONE_PAIR_LIMIT);
					}
					// 小对，跟注
					else 
						return callByDiff(diff, SMALL_ONE_PAIR_LIMIT);
				}
			}
		}
		//诈唬
        if (this.IsTheLastOne() && diff == 0) {
            Random random = new Random();
            if (random.nextInt(100) + 1 <= PROB_NUM) {
                return this.raiseByDiff(diff, random.nextInt(3) + 2, 2);
            }
        }
		// 在当前剩余金币与筹码总和下，下注太多，弃牌
		if (diff > 0
				&& diff * Constants.MAX_TOTAL_MULTIPLE > this
						.getTotalMoneyAndJetton())
			return fold(betStates);
		// 剩余筹码比较少，弃牌
		if (diff > 0
				&& this.getTotalMoneyAndJetton() * Constants.MAX_FOLD_MULTIPLE < this
						.getInitJetton())
			return fold(betStates);

		else if (diff == 0)
			return "check";
		return fold(betStates);
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
		if (hp.get(0).getValue() >= Constants.LESS_GAP_VALUE
				&& hp.get(1).getValue() >= Constants.LESS_GAP_VALUE)
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
	 * 判断公共牌是否是葫芦
	 * 
	 * @param pp
	 * @return
	 */
	private boolean isPubFullHouse(ArrayList<Poker> pp) {
		int count[] = new int[15];
		for (Poker p : pp) {
			count[p.getValue()]++;
		}
		boolean flag1 = false;
		boolean flag2 = false;
		for (int i = 2; i < 15; i++) {
			if (count[i] == 3)
				flag1 = true;
			if (count[i] == 2)
				flag2 = true;
		}

		if (flag1 && flag2)
			return true;

		return false;
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
				&& hp.get(0).getValue() >= Constants.LESS_GAP_VALUE)
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
				&& hp.get(0).getValue() < Constants.LESS_GAP_VALUE)
			return true;
		return false;
	}

	/**
	 * 判断公共牌是否是三条（不包括葫芦）
	 * 
	 * @param pp
	 * @return
	 */
	private boolean isPubThree(ArrayList<Poker> pp) {
		int count[] = new int[15];
		for (Poker p : pp) {
			count[p.getValue()]++;
		}
		boolean flag1 = false;
		boolean flag2 = false;
		for (int i = 2; i < 15; i++) {
			if (count[i] == 3)
				flag1 = true;
			if (count[i] == 2)
				flag2 = true;
		}

		if (flag1 && !flag2)
			return true;

		return false;

	}

	/**
	 * 判断公共牌是否是四条
	 * 
	 * @param pp
	 * @return
	 */
	private boolean isPubFour(ArrayList<Poker> pp) {
		int count[] = new int[15];
		for (Poker p : pp) {
			count[p.getValue()]++;
		}
		boolean flag1 = false;
		for (int i = 2; i < 15; i++) {
			if (count[i] == 4)
				flag1 = true;
		}

		return flag1;
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
		if (holdPokers != null) {
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
		}
		if (publicPokers != null) {
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
