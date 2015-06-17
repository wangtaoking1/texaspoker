package betPredict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by makun on 2015/5/31.
 */
public class UserBetTest {
    private BetPredict betPredict = BetPredict.getInstance();

    private UserUnit getTempUserUnit(String playerId,String line) {
        UserUnit userUnit = new UserUnit();
        userUnit.setPlayerId(playerId);

        String[] tmpStrs = line.split(",");
        int handNum = Integer.parseInt(tmpStrs[0].trim());
        int position = Integer.parseInt(tmpStrs[1].trim());
        int jetton = Integer.parseInt(tmpStrs[2].trim());

        /**
         * 注意添加内容顺序，需要先添加基础数据，playerId，jetton，handNum，position等基本信息，
         * 然后添加hold -> flop -> turn -> river
         */

        userUnit.setJetton(jetton);
        userUnit.setHandNum(handNum);
        userUnit.setPosition(position);

        // 设定hold
        String hold = tmpStrs[3].trim();
        float strategy = Float.parseFloat(tmpStrs[4].trim());
        userUnit.setHoldCard(hold,strategy);

        //设定flop
        String flop = tmpStrs[5].trim();
        strategy = Float.parseFloat(tmpStrs[6].trim());
        userUnit.setFlopCard(flop,strategy);
//
//        //设定turn
        String turn = tmpStrs[7].trim();
        strategy = Float.parseFloat(tmpStrs[8].trim());
        userUnit.setTurnCard(turn,strategy);
//
//        //设定river
        String river = tmpStrs[9].trim();
        strategy = Float.parseFloat(tmpStrs[10].trim());
        userUnit.setRiverCard(river,strategy);

        return userUnit;
    }
    public void test() {
        String path = "D:\\project\\华为比赛\\huaweiGame\\data\\data.csv";
        File file = new File(path);
        if(!file.exists()|| file.isDirectory()) {
            System.out.println("file is not exist");
            return;
        }

        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            String result = "";
            int index = 1;
            while ((line = reader.readLine())!=null) {
                if (line.length()>0) {
//                    String[] tmpStr = line.split(",");
//                    int handNum = Integer.parseInt(tmpStr[0].trim());
//                    int position = Integer.parseInt(tmpStr[1].trim());
//                    int jetton = Integer.parseInt(tmpStr[2].trim());
//                    String type = tmpStr[3].trim();
//                    float other = Float.parseFloat(tmpStr[4].trim());
//                    float stragety = Float.parseFloat(tmpStr[5].trim());
                    result += line+"\n";
                    String playerId = Integer.toString(index);
                    playerId = "1";
                    betPredict.addUserUnit(getTempUserUnit(playerId,line));
                    index ++;
                }
            }
            System.out.println(result);

            System.out.println("userMap:"+betPredict.getUserMap());

            System.out.println("cardType:"+betPredict.getCardTypeRatio());

//            UserUnit userUnit = new UserUnit();
//            userUnit.setPlayerId("1");
//            userUnit.setJetton(8);

            System.out.println(betPredict.getPredict("1","river",25,1));


        }catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        UserBetTest userBetTest = new UserBetTest();
        userBetTest.test();
    }
}
