package utils;

public enum MlHoldCardType {
    BIG_PAIR,       //两张底牌为9～A的对子
    SMALL_PAIR,     //为2～8的对子
    BIG_FLUSH,      //同花色，且都大于等于9
    SMALL_FLUSH,    //同花色，不都大于等于9
    HIGH_SINGLE,    //单牌，且都大于9
    LOW_SINGLE,     //小的单牌
}
