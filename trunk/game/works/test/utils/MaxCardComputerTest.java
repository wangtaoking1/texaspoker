package utils;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class MaxCardComputerTest {

    @Test
    public void test() {
        ArrayList<Poker> bp = new ArrayList<Poker>();
        bp.add(new Poker(Color.CLUBS, 2));
        bp.add(new Poker(Color.SPADES, 4));
        
        ArrayList<Poker> pp = new ArrayList<Poker>();
        pp.add(new Poker(Color.CLUBS, 14));
        pp.add(new Poker(Color.DIAMONDS, 14));
        pp.add(new Poker(Color.CLUBS, 5));
        pp.add(new Poker(Color.DIAMONDS, 3));
        pp.add(new Poker(Color.HEARTS, 14));
        
        MaxCardComputer computer = new MaxCardComputer(bp, pp);
        

        System.out.println(computer.getMaxCardGroup().getType());
        System.out.println(computer.getMaxCardGroup().getPower());
    }

}
