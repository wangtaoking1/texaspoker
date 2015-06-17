package utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

public class PokerTest {

    @Test
    public void test() {
        ArrayList<Poker> pokers = new ArrayList<Poker>();
        for (int i = 0; i < 5; i++) {
            Poker poker = new Poker(Color.CLUBS, 
                    new Random().nextInt(13) + 2);
            pokers.add(poker);
        }
        Collections.sort(pokers, new PokerComparator());
        
        for (int i = 0; i < 5; i++) {
            System.out.println(pokers.get(i).getValue());
        }
    }

}
