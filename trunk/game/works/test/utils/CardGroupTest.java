package utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class CardGroupTest {

    @Test
    public void test1() {
        ArrayList<Poker> pokers = new ArrayList<Poker>();
        pokers.add(new Poker(Color.CLUBS, 2));
        pokers.add(new Poker(Color.CLUBS, 4));
        pokers.add(new Poker(Color.DIAMONDS, 4));
        pokers.add(new Poker(Color.HEARTS, 4));
        pokers.add(new Poker(Color.CLUBS, 5));
        CardGroup group = new CardGroup(pokers);
        //System.out.println(group.getPower());
    }

    @Test
    public void test2() {
        ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
        
        ArrayList<Poker> pokers = new ArrayList<Poker>();
        pokers.add(new Poker(Color.CLUBS, 7));
        pokers.add(new Poker(Color.CLUBS, 9));
        pokers.add(new Poker(Color.HEARTS, 9));
        pokers.add(new Poker(Color.CLUBS, 6));
        pokers.add(new Poker(Color.CLUBS, 5));
        
        groups.add(new CardGroup(pokers));
        
        pokers = new ArrayList<Poker>();
        pokers.add(new Poker(Color.CLUBS, 11));
        pokers.add(new Poker(Color.DIAMONDS, 11));
        pokers.add(new Poker(Color.CLUBS, 12));
        pokers.add(new Poker(Color.CLUBS, 13));
        pokers.add(new Poker(Color.CLUBS, 2));
        groups.add(new CardGroup(pokers));
        
        Collections.sort(groups, new CardGroupComparator());
        //System.out.println(groups.get(0).getPower());
        //System.out.println(groups.get(1).getPower());
    }
}
