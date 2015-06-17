package main;
import java.util.ArrayList;

import client.GameClient;
import simpleAI.SimpleAI;
import utils.BetState;
import utils.Color;
import utils.MlCardType;
import utils.Poker;

public class game {
    public static void main(String args[]) {
        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String clientIP = args[2];
        int clientPort = Integer.parseInt(args[3]);
        String playerID = args[4];
        
        GameClient client = new GameClient(serverIP, serverPort, clientIP, 
                clientPort, playerID, true);
        try {
            client.startGame();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
