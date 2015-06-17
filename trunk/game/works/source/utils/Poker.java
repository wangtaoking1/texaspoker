package utils;

/**
 * 表示一张扑克牌
 * @author wangtao
 *
 */
public class Poker {
    private Color color;
    private int value;      //2~14
    
    public Poker(Color color, int value) {
        this.setColor(color);
        this.setValue(value);
    }
    
    public Color getColor() {
        return this.color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    
    public int getValue() {
        return this.value;
    }
    public void setValue(int value) {
        assert value >= 2 && value <= 14;
        this.value = value;
    }
}
