package utils;

/**
 * 用于机器学习的牌型
 *
 */
public enum MlCardType {
    HIGH_CARD,          //高牌
    LOW_ONE_PAIR,       //小对子
    HIGH_ONE_PAIR,      //大对子
    LOW_TWO_PAIR,       //小两对
    HIGH_TWO_PAIR,      //大两对
    THREE_OF_A_KIND,    //三条
    STRAIGHT,           //顺子
    FLUSH,              //同花
    FULL_HOUSE_UP,      //葫芦及葫芦以上
}
