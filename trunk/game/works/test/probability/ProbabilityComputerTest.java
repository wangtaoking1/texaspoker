package probability;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

import utils.Color;
import utils.Poker;

public class ProbabilityComputerTest {

    @Test
    public void testComputeProbability() {
        long startTime = System.currentTimeMillis();
        ArrayList<Poker> holds = new ArrayList<Poker>();
        ArrayList<Poker> publics = new ArrayList<Poker>();
        holds.add(new Poker(Color.DIAMONDS, 9));
        holds.add(new Poker(Color.HEARTS, 12));
        publics.add(new Poker(Color.DIAMONDS, 8));
        publics.add(new Poker(Color.HEARTS, 7));
        publics.add(new Poker(Color.HEARTS, 10));
        publics.add(new Poker(Color.CLUBS, 11));
        publics.add(new Poker(Color.HEARTS, 13));

        float prob = ProbabilityComputer.computeProbability(2, holds, publics);
        long endTime = System.currentTimeMillis();
        System.out.println("Prob: " + prob);
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }
    
    @Test
    public void testComputeProbability1() {
        Random random = new Random();
        System.out.println(random.nextInt(2) + 1);
    }
}
