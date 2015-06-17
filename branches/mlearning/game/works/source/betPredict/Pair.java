package betPredict;

/**
 * Created by makun on 2015/5/28.
 */
public class Pair<A, B> {
    public  A first;
    public  B second;

    public Pair() {

    }

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public String toString() {
        String result = "";
        result += "(";
        result += this.first;
        result += ","+this.second;
        result  += ")";
        return result;
    }
}