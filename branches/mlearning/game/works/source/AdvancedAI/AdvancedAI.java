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

public class AdvancedAI extends SuperAI {
	
	private int foldCounter = 0; // 用来计算fold的局数
	public AdvancedAI(String playerID) {
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
//			foldCounter ++;
//			writer.write("fold " + Integer.toString(foldCounter) + "\n");
//			writer.write("Current Hand Number: " + Integer.toString(this.getHandNum()) + "\n");
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

		int maxBlindBet = Constants.HIGH_BET_MULTIPLE * this.getBlind(); // 可接受（跟注）最大下注筹码
		int midBlindBet = Constants.MIDDLE_BET_MULTIPLE * this.getBlind(); // 可接受（跟注）中等下注筹码

		// 如果手牌是大对：AA, KK, QQ, JJ, 1010等
		if (this.isHoldBigPair(hp)) {
			int raise_bet = Constants.LOW_RAISE_MULTIPLE * this.getBlind();
			// 如果需要加注的筹码小于"BLIND_RAISE_MULTIPLE * 盲注金额"， 则自己加注至
			// "BLIND_RAISE_MULTIPLE * 盲注金额"
			if (diff < raise_bet) {
				if (raise_bet - diff > this.getTotalJetton())
					return "all_in";
				else
					return "raise " + Integer.toString(raise_bet - diff);
			}
			// 如果需要加注的筹码大于"BLIND_RAISE_MULTIPLE * 盲注金额"时，不加注，但跟注
			else
				return callByDiff(diff);

		}
		// 其它牌的情况，如果需要下注超过可打接受最大下注金额，弃牌
		else if (diff >= maxBlindBet)
			return fold(betStates);
		// 需要跟注的筹码大于剩余总金币与筹码的一个比例时则弃牌(相对于剩余的金币与筹码来说，下注太多了，比较保守的做法的就是弃牌)
		// else if (diff * Constants.MAX_TOTAL_MULTIPLE > this
		// .getTotalMoneyAndJetton())
		// return fold(betStates);
		// 手牌是小对：2~9中的一对
		else if (this.isHoldSmallPair(hp)) {
			// 如果需要下注小于可接受最大下注金额，则跟注
			if (diff <= maxBlindBet)
				return callByDiff(diff);
		}
		// 手牌不相等且都大于GAP_VALUE
		else if (this.isHoldBig(hp)) {
			// 如果需要下注小于可接受中等下注金额，则跟注
			if (diff <= midBlindBet)
				return callByDiff(diff);
			else
				return fold(betStates);
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
			} else
				return fold(betStates);
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
			if (betStates.get(i).getPlayerID() == this.getPlayerID())
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
			// 手牌是大对
			if (isHoldBigPair(hp))
				return callByDiff(diff);
			// 手牌是小对
			else if (isHoldSmallPair(hp)) {
				if (diff <= Constants.HIGH_BET_MULTIPLE * this.getBlind())
					return callByDiff(diff);
				else
					return fold(betStates);
			}
			// 手牌不是对，说明公共牌有一对
			else {
				if (hp.get(0).getValue() >= Constants.GAP_VALUE
						|| hp.get(1).getValue() >= Constants.GAP_VALUE) {
					if (diff < Constants.MIDDLE_BET_MULTIPLE * this.getBlind())
						return callByDiff(diff);
					else
						return fold(betStates);
				}
				// 两张牌都是小牌
				else if (diff <= Constants.LOW_BET_MULTIPLE)
					return callByDiff(diff);
				else
					return fold(betStates);
			}
		}
		// 两对
		else if (power > (long) 3 * Math.pow(10, 10)
				&& power < (long) 4 * Math.pow(10, 10)) {
			// 手牌是大对，说明另一对是公共牌中出现的，这种情况相当于只有一对
			if (this.isHoldBigPair(hp)) {
				if (diff >= Constants.HIGH_BET_MULTIPLE * this.getBlind()) {
					// 剩余金币与筹码还比较多，使用比较积极的打法
					if (diff * Constants.MAX_TOTAL_MULTIPLE < this
							.getTotalMoneyAndJetton())
						return callByDiff(diff);
					else
						return fold(betStates);
				}
				return callByDiff(diff);
			}
			// 手牌是小对
			else if (this.isHoldSmallPair(hp)) {
				if (diff <= Constants.MIDDLE_BET_MULTIPLE * this.getBlind())
					return callByDiff(diff);
				else
					return fold(betStates);
			}
			// 手牌不相等，说明此时是与公共牌中的两张牌组成两对
			else {
				// 两对都是大对
				if (this.isHoldBig(hp)) {
					return this
							.raiseByDiff(diff, Constants.HIGH_RAISE_MULTIPLE);
				}
				// 其中一个是大对
				else if (hp.get(0).getValue() >= Constants.GAP_VALUE
						|| hp.get(1).getValue() >= Constants.GAP_VALUE) {
					return this.raiseByDiff(diff,
							Constants.MIDDLE_RAISE_MULTIPLE);
				}
				// 两对都是小对
				else {
					if (diff <= Constants.HIGH_BET_MULTIPLE * this.getBlind())
						return callByDiff(diff);
					else
						return fold(betStates);
				}
			}
		}
		// 三条
		else if (power > (long) 4 * Math.pow(10, 10)
				&& power < (long) 5 * Math.pow(10, 10)) {
			// 手牌相等
			if (hp.get(0).getValue() == hp.get(1).getValue()) {
				return raiseByDiff(diff, Constants.HIGH_RAISE_MULTIPLE); // 加高倍注
			}
			// 手牌不相等，说明三条中的两个是在公共牌里的
			else {
				return raiseByDiff(diff, Constants.MIDDLE_RAISE_MULTIPLE); // 加中倍注
			}
		}
		// 顺子及以上
		else if (power > (long) 5 * Math.pow(10, 10)) {
			return raiseByDiff(diff, Constants.HIGH_RAISE_MULTIPLE); // 加高倍注
		}
		// 在当前剩余金币与筹码总和下，下注太多，弃牌
		if (diff * Constants.MAX_TOTAL_MULTIPLE > this.getTotalMoneyAndJetton())
			return fold(betStates);
		// 同花或顺子差一张
		else if (this.computeFlush(hp, pp) <= 1
				|| this.computeStraight(hp, pp) <= 1) {
			if (diff <= Constants.MIDDLE_BET_MULTIPLE * this.getBlind())
				return callByDiff(diff);
			else
				return fold(betStates);
		}
		// 高牌
		else if (this.isHoldBig(hp)) {
			if (diff <= Constants.LOW_BET_MULTIPLE * this.getBlind())
				return callByDiff(diff);
			else
				return fold(betStates);
		} else if (diff == 0)
			return "check";
		return fold(betStates);
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
		if (diff < multiple * this.getBlind())
			return "raise "
					+ Integer.toString(multiple * this.getBlind() - diff);
		else
			return "call";
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
		return thinkAfterFlop(betStates);
		// ArrayList<Poker> hp = this.getHoldPokers();
		// ArrayList<Poker> pp = this.getPublicPokers();
		// CardGroup maxGroup = (new MaxCardComputer(hp, pp)).getMaxCardGroup();
		//
		// int diff = this.computeDifference(betStates);
		// int bet = 0;
		// if (maxGroup.getPower() > (long) 3 * Math.pow(10, 10)) {
		// // 两对及两对以上
		// if (diff >= this.getTotalJetton())
		// return "all_in";
		// else if (diff == 0)
		// return "raise " + 3 * this.getBlind();
		// else
		// return "call";
		// } else if (this.computeFlush(hp, pp) <= 1
		// || this.computeStraight(hp, pp) <= 1) {
		// // 同花或顺子差一张
		// if (diff >= 5 * this.getBlind())
		// return fold(betStates);
		// else if (diff == 0)
		// return "check";
		// else if (diff > this.getTotalJetton())
		// return "all_in";
		// else
		// return "call";
		// } else if (this.isOneMaxPair(maxGroup, hp)) {
		// // 最大对子
		// if (diff >= 3 * this.getBlind())
		// return fold(betStates);
		// else if (diff == 0)
		// return "check";
		// else if (diff > this.getTotalJetton())
		// return "all_in";
		// else
		// return "call";
		// } else {
		// if (diff == 0)
		// return "check";
		// else
		// return fold(betStates);
		// }
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
		return thinkAfterFlop(betStates);
		// ArrayList<Poker> hp = this.getHoldPokers();
		// ArrayList<Poker> pp = this.getPublicPokers();
		// CardGroup maxGroup = (new MaxCardComputer(hp, pp)).getMaxCardGroup();
		//
		// int diff = this.computeDifference(betStates);
		// int bet = 0;
		// if (maxGroup.getPower() > (long) 3 * Math.pow(10, 10)) {
		// // 两对及两对以上
		// if (diff >= this.getTotalJetton())
		// return "all_in";
		// else if (diff == 0)
		// return "raise " + 3 * this.getBlind();
		// else
		// return "call";
		// } else if (this.computeFlush(hp, pp) <= 1
		// || this.computeStraight(hp, pp) <= 1) {
		// // 同花或顺子差一张
		// if (diff >= 5 * this.getBlind())
		// return fold(betStates);
		// else if (diff == 0)
		// return "check";
		// else if (diff > this.getTotalJetton())
		// return "all_in";
		// else
		// return "call";
		// } else if (this.isOneMaxPair(maxGroup, hp)) {
		// // 最大对子
		// if (diff >= 3 * this.getBlind())
		// return fold(betStates);
		// else if (diff == 0)
		// return "check";
		// else if (diff > this.getTotalJetton())
		// return "all_in";
		// else
		// return "call";
		// } else {
		// if (diff == 0)
		// return "check";
		// else
		// return fold(betStates);
		// }
	}

	/**
	 * 计算自己与其他押最大注玩家的差距，用于决定自己押注的多少
	 * 
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
	 * 根据两张底牌判断是否应该弃牌
	 * 
	 * @param holdPokers
	 * @return
	 */
	private boolean shouldFold(ArrayList<Poker> hp) {
		int v1 = hp.get(0).getValue();
		int v2 = hp.get(1).getValue();
		// 两张牌都小于10且不可能组成顺子，弃牌
		if ((v1 < 10 && v2 < 10) && Math.abs(v1 - v2) > 4)
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

	/**
	 * 如果为一对，判断是否为最大对
	 * 
	 * @param group
	 * @return
	 */
	private boolean isOneMaxPair(CardGroup group, ArrayList<Poker> hp) {
		if ((group.getPower() / (long) Math.pow(10, 10)) != 2) {
			return false;
		}
		boolean flag = false;
		for (Poker poker : hp) {
			if (poker.getValue() == group.getPokers().get(0).getValue()) {
				flag = true;
				break;
			}
		}

		return flag;
	}

}
