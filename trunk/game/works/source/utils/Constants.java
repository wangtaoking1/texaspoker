package utils;

public class Constants 
{
//	public final static int HIGH_BET_MULTIPLE = 20; // 下注时最大跟注的倍数，超过这个倍数时就弃牌
//	public final static int MIDDLE_BET_MULTIPLE = 10;
//	public final static int LOW_BET_MULTIPLE = 5;
//	
//	public final static int MAX_TOTAL_MULTIPLE = 100; // MAX_TOTAL_MULTIPLE * blind 超过剩余的金币与筹码总和
//	
//	public final static int GAP_VALUE = 10; 	// 分界值，大于或等于这个值为Big，小于这个值为Small
//
//	public final static int HIGH_RAISE_MULTIPLE = 10;
//	public final static int MIDDLE_RAISE_MULTIPLE = 5;
//	public final static int LOW_RAISE_MULTIPLE = 3;
	
	public final static int PLAYER_NUM = 4;
	public final static int GAP_VALUE = 10; 	// 分界值，大于或等于这个值为Big，小于这个值为Small
	public final static int MAX_TOTAL_MULTIPLE = 3; 
	public final static int MAX_BET_MULTIPLE_EACH_HAND = 2;
	public final static int HIGH_BET_MULTIPLE = 10; // 下注时最大跟注的倍数，超过这个倍数时就弃牌
	public final static int MIDDLE_BET_MULTIPLE = 5;
	public final static int LOW_BET_MULTIPLE = 3;

	public final static int HIGH_RAISE_MULTIPLE = 5;
	public final static int MIDDLE_RAISE_MULTIPLE = 3;
	public final static int LOW_RAISE_MULTIPLE = 2;
	
	public final static int MAX_FOLD_MULTIPLE = 3; // 当剩余筹码占初始金币不到 1 / MAX_FOLD_MULTIPLE 时，会变得比较保守
	// MoreAI
	public final static int MORE_MAX_BET_MULTIPLE = 10;
	public final static int MORE_HIGH_BET_MULTIPLE = 5; // 下注时最大跟注的倍数，超过这个倍数时就弃牌 // 6 4 2
	public final static int MORE_MIDDLE_BET_MULTIPLE = 3;
	public final static int MORE_LOW_BET_MULTIPLE = 2;

	public final static int MORE_MAX_RAISE_MULTIPLE = 15;
	public final static int MORE_HIGH_RAISE_MULTIPLE = 6;
	public final static int MORE_MIDDLE_RAISE_MULTIPLE = 4;
	public final static int MORE_LOW_RAISE_MULTIPLE = 2;
	
	public final static int MORE_CALL_PERCENTAGE = 25;
	public final static int MORE_GAP_VALUE = 11;
	// LessAI
	public final static int LESS_MAX_BET_MULTIPLE = 13;
	public final static int LESS_HIGH_BET_MULTIPLE = 6; // 下注时最大跟注的倍数，超过这个倍数时就弃牌
	public final static int LESS_MIDDLE_BET_MULTIPLE = 4;
	public final static int LESS_LOW_BET_MULTIPLE = 3;
	
	public final static int LESS_MAX_RAISE_MULTIPLE = 20;
	public final static int LESS_HIGH_RAISE_MULTIPLE = 6;
	public final static int LESS_MIDDLE_RAISE_MULTIPLE = 4;
	public final static int LESS_LOW_RAISE_MULTIPLE = 2;
	
	public final static int LESS_CALL_PERCENTAGE = 35; // 牌型不是很好的时候，跟注的概率百分比
	public final static int LESS_GAP_VALUE = 11;
}