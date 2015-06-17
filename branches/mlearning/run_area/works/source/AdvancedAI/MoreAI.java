package AdvancedAI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utils.BetState;
import utils.CardGroup;
import utils.Color;
import utils.Constants;
import utils.MaxCardComputer;
import utils.Poker;
import utils.SuperAI;

/**
 * 人数多于4人的时候使用该AI
 * 
 * @author wenzhu
 * 
 */
public class MoreAI extends SuperAI {

	private int foldCounter = 0; // 用来计算fold的局数

	public MoreAI(String playerID) {
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
//		String colors[] = { "SPADES", // 黑桃
//				"HEARTS", // 红桃
//				"CLUBS", // 梅花
//				"DIAMONDS" // 方片
//		};
//		String logName = "/home/wenzhu/area/fold_log.txt";
//		try {
//			FileWriter writer = new FileWriter(logName, true);
//			foldCounter++;
//			writer.write("MoreAI" + "\n");
//			writer.write("Playerid:" + this.getPlayerID() + "\n");
//			writer.write("fold " + Integer.toString(foldCounter) + "\n");
//			writer.write("Current Hand Number: "
//					+ Integer.toString(this.getHandNum()) + "\n");
//			writer.write("Hold pokers:\n");
//			ArrayList<Poker> hp = this.getHoldPokers();
//			for (int i = 0; i < hp.size(); i++)
//				writer.write(colors[getIndex(hp.get(i).getColor())] + " "
//						+ hp.get(i).getValue() + "\n");
//
//			writer.write("Public pokers:\n");
//			ArrayList<Poker> pp = this.getPublicPokers();
//			for (int i = 0; i < pp.size(); i++)
//				writer.write(colors[getIndex(pp.get(i).getColor())] + " "
//						+ pp.get(i).getValue() + "\n");
//
//			int maxBet = this.getMaxBet(betStates);
//			int selfBet = this.getSelfBet(betStates);
//			int diff = maxBet - selfBet;
//			writer.write("MaxBet: " + Integer.toString(maxBet) + "\n");
//			writer.write("SelfBet " + Integer.toString(selfBet) + "\n");
//			writer.write("Diff: " + Integer.toString(diff) + "\n");
//			writer.write("Rest Jetton: "
//					+ Integer.toString(this.getTotalJetton()) + "\n");
//			writer.write("Rest Money: "
//					+ Integer.toString(this.getTotalMoney()) + "\n");
//			writer.write("Money and Jetton: "
//					+ Integer.toString(this.getTotalMoneyAndJetton()) + "\n");
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//		}
		return "fold";
	}

	@Override
	public String thinkAfterHold(ArrayList<BetState> betStates) {
		ArrayList<Poker> hp = this.getHoldPokers();
		// 计算自己与最大押注的差距，得出需要押注的大小
		int maxBet = this.getMaxBet(betStates);
		int selfBet = this.getSelfBet(betStates);
		int diff = (maxBet - selfBet);

		int maxBlindBet = Constants.MORE_HIGH_BET_MULTIPLE * this.getBlind(); // 可接受（跟注）最大下注筹码
		int midBlindBet = Constants.MORE_MIDDLE_BET_MULTIPLE * this.getBlind(); // 可接受（跟注）中等下注筹码
		int lowBlindBet = Constants.MORE_LOW_BET_MULTIPLE * this.getBlind();

		// 如果手牌是大对：AA, KK, QQ, JJ, 1010等
		if (this.isHoldBigPair(hp)) {
			// 加注至 MIDDLE_RAISE_MULTIPLE * blind
			return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE);
		}
		// 手牌是小对：2~9中的一对
		else if (this.isHoldSmallPair(hp)) {
			// 如果需要下注小于可接受最大下注金额，则跟注
			if (diff <= maxBlindBet)
				return callByDiff(diff);
		}
		// 手牌不相等且都大于GAP_VALUE
		else if (this.isHoldBig(hp)) {
			// 如果需要下注小于可接受最大下注金额，则跟注
			if (diff <= maxBlindBet)
				return callByDiff(diff);
		}
		// 手牌其中有一个大于GAP_VALUE
		else if (hp.get(0).getValue() >= Constants.GAP_VALUE
				|| hp.get(0).getValue() >= Constants.GAP_VALUE) {
			// 如果需要下注小于可接受中等下注金额且(手牌同花色(有可能组成同花)或者相差小于4(有可能组成顺子))
			if (diff <= midBlindBet) {
				if (this.isHoldSameColor(hp)
						|| this.isHoldLessThanFour(hp)
						|| (hp.get(0).getValue() >= 11 || hp.get(1).getValue() >= 11))
					return callByDiff(diff);
			}
		}
		// 手牌都小于10
		else {
			// 手牌同花色或者相差小于4
			if (this.isHoldSameColor(hp) || this.isHoldLessThanFour(hp)) {
				if (diff <= midBlindBet)
					return callByDiff(diff);
			}
			// 其它牌型，50%概率跟注
			else if ((int) Math.random() * 100 <= Constants.MORE_CALL_PERCENTAGE) {
				if (diff <= lowBlindBet)
					return callByDiff(diff);
			}
		}
		return fold(betStates);
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
	 * 跟注
	 * 
	 * @param diff
	 * @return
	 */
	private String callByDiff(int diff) {
		// 不需要跟注的时候，则让牌(check)
		if (diff == 0)
			return "check";
		// 剩余筹码足够，则跟注
		else if (diff < this.getTotalJetton())
			return "call";
		// 剩余筹码不够，则全押
		else
			return "all_in";
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
		// 一对
		if (power > (long) 2 * Math.pow(10, 10)
				&& power < (long) 3 * Math.pow(10, 10)) {
			// 手牌是大对，加中倍注
			if (isHoldBigPair(hp))
				return raiseByDiff(diff, Constants.MORE_MIDDLE_RAISE_MULTIPLE);
			// 手牌是小对，加低倍注
			else if (isHoldSmallPair(hp)) {
				return raiseByDiff(diff, Constants.MORE_LOW_RAISE_MULTIPLE);
			} else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					// 手牌中有一个大牌
					if (hp.get(0).getValue() >= Constants.GAP_VALUE
							|| hp.get(1).getValue() >= Constants.GAP_VALUE) {
						if (diff <= Constants.MORE_LOW_BET_MULTIPLE
								* this.getBlind())
							return callByDiff(diff);
					}
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
							hp, pp); // 在这里，pairValues中有且只有一个值
					// 大对，加中倍注
					if (pairValues.get(0) >= Constants.GAP_VALUE)
						return raiseByDiff(diff,
								Constants.MORE_MIDDLE_RAISE_MULTIPLE);
					// 小对，加低倍注
					else
						return raiseByDiff(diff,
								Constants.MORE_LOW_RAISE_MULTIPLE);
				}
			}
		}
		// 两对
		else if (power > (long) 3 * Math.pow(10, 10)
				&& power < (long) 4 * Math.pow(10, 10)) {
			ArrayList<Integer> holdPairValues = this
					.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
			// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
			if (holdPairValues.size() == 1) {
				return callByDiff(diff);
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加高倍注
				if (holdPairValues.get(0) >= Constants.GAP_VALUE
						&& holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE);
				// 其中一个为大对，加中倍注
				else if (holdPairValues.get(0) >= Constants.GAP_VALUE
						|| holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				// 两对都是小对，加低倍注
				else
					return raiseByDiff(diff, Constants.MORE_LOW_RAISE_MULTIPLE);
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
			// 手牌相等
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE); // 加高倍注
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1)
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					// 手牌都是大牌
					if (hp.get(0).getValue() >= Constants.GAP_VALUE
							&& hp.get(0).getValue() >= Constants.GAP_VALUE) {
						if (diff <= Constants.MORE_MIDDLE_BET_MULTIPLE
								* this.getBlind())
							return callByDiff(diff);
					} else if (diff <= Constants.MORE_LOW_BET_MULTIPLE
							* this.getBlind())
						return callByDiff(diff);
				}
			}
		}
		// 顺子及以上
		else if (power > (long) 5 * Math.pow(10, 10)) {
			return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE); // 加高倍注
		}
		// 在当前剩余金币与筹码总和下，下注太多，弃牌
		if (diff * Constants.MAX_TOTAL_MULTIPLE > this.getTotalMoneyAndJetton())
			return fold(betStates);
		// 同花或顺子差一张
		else if (this.computeFlush(hp, pp) <= 1
				|| this.computeStraight(hp, pp) <= 1) {
			if (diff <= Constants.MORE_MIDDLE_BET_MULTIPLE * this.getBlind())
				return callByDiff(diff);
		}
		// 高牌
		else if (this.isHoldBig(hp)) {
			if (diff <= Constants.MORE_LOW_BET_MULTIPLE * this.getBlind())
				return callByDiff(diff);
		}
		// 手牌中有一张是大牌
		else if (hp.get(0).getValue() >= Constants.GAP_VALUE
				|| hp.get(1).getValue() >= Constants.GAP_VALUE) {
			if (diff <= Constants.MORE_LOW_BET_MULTIPLE * this.getBlind()
					&& (int) (Math.random() * 100) <= Constants.MORE_CALL_PERCENTAGE) // 按照一定的概率跟注
				return callByDiff(diff);
		} else if (diff == 0)
			return "check";
		return fold(betStates);
	}

	private boolean isHoldPair(ArrayList<Poker> hp) {
		if (hp.get(0).getValue() == hp.get(1).getValue())
			return true;
		return false;
	}

	/**
	 * 加注：加注金额为mutiple * blind
	 * 
	 * @param diff
	 *            根据前面玩家下注，需要跟注的最小数量
	 * @param multiple
	 * @return
	 */
	private String raiseByDiff(int diff, int multiple) {
		// 本环节已经加过注，则选择跟注
	    if (this.getHasRaised()) 
	        return "call";
	    
		if (diff < multiple * this.getBlind()) {
		    this.setHasRaised(true);
			return "raise "
					+ Integer.toString(multiple * this.getBlind() - diff);
		}
		else
			return "call";
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
		// 一对
		if (power > (long) 2 * Math.pow(10, 10)
				&& power < (long) 3 * Math.pow(10, 10)) {
			// 手牌是一对，跟注
			if (isHoldPair(hp)
					&& diff <= Constants.MORE_HIGH_BET_MULTIPLE * this.getBlind()) {
				return callByDiff(diff);
			} else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					// 手牌都是大牌
					if (this.isHoldBig(hp) && diff <= Constants.MORE_HIGH_BET_MULTIPLE * this.getBlind())
						return callByDiff(diff);
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
							hp, pp); // 在这里，pairValues中有且只有一个值
					// 大对，加中倍注
					if (pairValues.get(0) >= Constants.GAP_VALUE)
						return raiseByDiff(diff,
								Constants.MORE_MIDDLE_RAISE_MULTIPLE);
					// 小对，跟注
					else if (diff <= Constants.MORE_HIGH_BET_MULTIPLE * this.getBlind())
						return callByDiff(diff);
				}
			}
		}
		// 两对
		else if (power > (long) 3 * Math.pow(10, 10)
				&& power < (long) 4 * Math.pow(10, 10)) {
			ArrayList<Integer> holdPairValues = this
					.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
			// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
			if (holdPairValues.size() == 1) {
				// 大对
				if (holdPairValues.get(0) >= Constants.GAP_VALUE) {
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				}
				// 小对
				else {
					return callByDiff(diff); // TODO 加注还是跟注好？
				}
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加高倍注
				if (holdPairValues.get(0) >= Constants.GAP_VALUE
						&& holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE);
				// 其中一个为大对，加中倍注
				else if (holdPairValues.get(0) >= Constants.GAP_VALUE
						|| holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				// 两对都是小对，加低倍注
				else
					return raiseByDiff(diff, Constants.MORE_LOW_RAISE_MULTIPLE);
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
			// 手牌相等
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE); // 加高倍注
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1)
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					// 手牌都是大牌
					if (hp.get(0).getValue() >= Constants.GAP_VALUE
							&& hp.get(0).getValue() >= Constants.GAP_VALUE) {
						if (diff <= Constants.MORE_MIDDLE_BET_MULTIPLE
								* this.getBlind())
							return callByDiff(diff);
					} else if (diff <= Constants.MORE_LOW_BET_MULTIPLE
							* this.getBlind())
						return callByDiff(diff);
				}
			}
		}
		// 顺子及以上
		else if (power > (long) 5 * Math.pow(10, 10)) {
			return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE); // 加高倍注
		}
		// 在当前剩余金币与筹码总和下，下注太多，弃牌
		if (diff * Constants.MAX_TOTAL_MULTIPLE > this.getTotalMoneyAndJetton())
			return fold(betStates);
		// 同花或顺子差一张
		else if (this.computeFlush(hp, pp) <= 1
				|| this.computeStraight(hp, pp) <= 1) {
			if (diff <= Constants.MORE_LOW_BET_MULTIPLE * this.getBlind())
				return callByDiff(diff);
		}
		// 高牌，按照一定的概率跟注定
		else {
			if (diff <= Constants.MORE_LOW_BET_MULTIPLE * this.getBlind()
					&& (int) (Math.random() * 100) <= Constants.MORE_CALL_PERCENTAGE) // 按照一定的概率跟注
				return callByDiff(diff);
		}
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
		// 一对
		if (power > (long) 2 * Math.pow(10, 10)
				&& power < (long) 3 * Math.pow(10, 10)) {
			// 手牌是大对，加低倍注
			if (isHoldBigPair(hp))
				return raiseByDiff(diff, Constants.MORE_LOW_RAISE_MULTIPLE);
			// 手牌是小对，跟注
			else if (isHoldSmallPair(hp)
					&& diff <= Constants.MORE_HIGH_BET_MULTIPLE
							* this.getBlind()) {
				return callByDiff(diff);
			} else {
				ArrayList<Integer> pubPair = this.getPubPairValue(pp); // 获取公共牌中的对子的值
				// 公共牌中有一对，说明手牌没有和公共牌中的某一张组成对子 ，这种情况跟高牌差不多
				if (pubPair.size() == 1) {
					// 手牌都是大牌
					if (this.isHoldBig(hp) && diff <= Constants.MORE_HIGH_BET_MULTIPLE * this.getBlind()
							&& (int) (Math.random() * 100) <= Constants.MORE_CALL_PERCENTAGE)
						return callByDiff(diff);
				}
				// 说明手牌中的一张牌与公共牌中的一张牌组成对子
				else if (pubPair.size() == 0) {
					ArrayList<Integer> pairValues = this.getHoldPubPairValue(
							hp, pp); // 在这里，pairValues中有且只有一个值
					// 大对，加低倍注
					if (pairValues.get(0) >= Constants.GAP_VALUE)
						return raiseByDiff(diff,
								Constants.MORE_LOW_RAISE_MULTIPLE);
					// 小对，跟注
					else if (diff <= Constants.MORE_MIDDLE_RAISE_MULTIPLE
							* this.getBlind())
						return callByDiff(diff);
				}
			}
		}
		// 两对
		else if (power > (long) 3 * Math.pow(10, 10)
				&& power < (long) 4 * Math.pow(10, 10)) {
			ArrayList<Integer> holdPairValues = this
					.getHoldPubPairValue(hp, pp); // 获取手牌与公共牌组成对子的value
			// 手牌中只有一张与公共牌组成对子，说明另一对是在公共牌里的
			if (holdPairValues.size() == 1) {
				// 大对
				if (holdPairValues.get(0) >= Constants.GAP_VALUE) {
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				}
				// 小对
				else {
					return callByDiff(diff); // TODO 加注还是跟注好？
				}
			}
			// 手牌中的两张分别与公共牌中的一张组成对子
			else if (holdPairValues.size() == 2) {
				// 两对都是大对，加高倍注
				if (holdPairValues.get(0) >= Constants.GAP_VALUE
						&& holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE);
				// 其中一个为大对，加中倍注
				else if (holdPairValues.get(0) >= Constants.GAP_VALUE
						|| holdPairValues.get(1) >= Constants.GAP_VALUE)
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				// 两对都是小对，跟注
				else
					return callByDiff(diff);
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
			// 手牌相等
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE); // 加高倍注
			}
			// 手牌不相等
			else {
				ArrayList<Integer> pairValues = this.getPubPairValue(pp);
				// 公共牌中有一对，说明三条中有两个是在公共牌里的
				if (pairValues.size() == 1)
					return raiseByDiff(diff,
							Constants.MORE_MIDDLE_RAISE_MULTIPLE);
				// 说明三条是出现在公共牌里
				else if (pairValues.size() == 0) {
					// 手牌都是大牌
					if (this.isHoldBig(hp)) {
						if (diff <= Constants.MORE_MIDDLE_BET_MULTIPLE
								* this.getBlind())
							return callByDiff(diff);
					} else if (diff <= Constants.MORE_LOW_BET_MULTIPLE
							* this.getBlind())
						return callByDiff(diff);
				}
			}
		}
		// 顺子及以上
		else if (power > (long) 5 * Math.pow(10, 10)) {
			return raiseByDiff(diff, Constants.MORE_HIGH_RAISE_MULTIPLE); // 加高倍注
		}
		// 在当前剩余金币与筹码总和下，下注太多，弃牌
		if (diff * Constants.MAX_TOTAL_MULTIPLE > this.getTotalMoneyAndJetton())
			return fold(betStates);
		// 高牌，按照一定的概率跟注
		else {
			if (diff <= Constants.MORE_LOW_BET_MULTIPLE * this.getBlind()
					&& (int) (Math.random() * 100) <= Constants.MORE_CALL_PERCENTAGE) // 按照一定的概率跟注
				return callByDiff(diff);
		}
		return fold(betStates);
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
