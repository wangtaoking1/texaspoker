package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import probability.ProbAI;
import simpleAI.SimpleAI;
import utils.BetState;
import utils.Color;
import utils.InitState;
import utils.Poker;
import AdvancedAI.LessAI;
import AdvancedAI.SelectAI;

public class GameClient {
	private Socket socket = null;
	private BufferedReader input = null;
	private PrintWriter output = null;
	private String playerID;
	private int handNum = 0;
//	private SelectAI ai;
	private ProbAI ai;
	private String needNotify;

	public GameClient(String serverIp, int serverPort, String clientIp,
			int clientPort, String playerID, boolean needNotify) {

		boolean flag = false;
		while (!flag) {
			try {
				socket = new Socket(); //此时Socket对象未绑定到本地端口，并且未连接远程服务器
				socket.setReuseAddress(true);

				SocketAddress localAddr = new InetSocketAddress(clientIp, clientPort);
				SocketAddress remoteAddr = new InetSocketAddress(serverIp, serverPort);

				socket.bind(localAddr); //与本地端口绑定
				
				socket.connect(remoteAddr, 200);
				flag = true;
//				System.out.println("success!");
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				
			} catch (Exception e) {
//				System.out.println("connect failed,try again!");
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}

		this.playerID = playerID;
		if (needNotify) {
			this.needNotify = "need_notify ";
		} else {
			this.needNotify = "";
		}
	}

	/**
	 * 外部调用该函数，开始游戏
	 * 
	 * @throws Exception
	 */
	public void startGame() throws Exception {
		this.register();
//		this.ai = new SelectAI(playerID);
		this.ai = new ProbAI(playerID);

		while (true) {
			// 从服务端读取数据
			ArrayList<String> inputData = this.readData();

//			for (int i = 0; i < inputData.size(); i++) {
//				System.out.println(inputData.get(i));
//			}

			if (inputData.get(0).equals("game-over")) {
				this.gameOver();
				break;
			} else if (inputData.get(0).equals("seat")) {
				this.handNum++;
				this.setSeatInfo(inputData.get(1));
			} else if (inputData.get(0).equals("blind")) {
				this.setBlindInfo(inputData.get(1));
			} else if (inputData.get(0).equals("hold")) {
				this.setHoldInfo(inputData.get(1));
			} else if (inputData.get(0).equals("inquire")) {
				this.inquireAndAction(inputData.get(1));
			} else if (inputData.get(0).equals("flop")) {
				this.setFlopInfo(inputData.get(1));
			} else if (inputData.get(0).equals("turn")) {
				this.setTurnInfo(inputData.get(1));
			} else if (inputData.get(0).equals("river")) {
				this.setRiverInfo(inputData.get(1));
			} else if (inputData.get(0).equals("showdown")) {
				this.showdown(inputData.get(1));
			} else if (inputData.get(0).equals("pot-win")) {
				this.potwin(inputData.get(1));
			} else if (inputData.get(0).equals("notify")) {
				this.setNotifyInfo(inputData.get(1));
			}
		}
	}

	/**
	 * 向服务端注册
	 */
	private void register() {
		try {
			this.sendData("reg: " + this.playerID + " wukong "
					+ this.needNotify + "\n");
		} catch (Exception e) {
//			System.out.println(e);
		}
	}

	/**
	 * 游戏结束，关闭客户端
	 */
	private void gameOver() {
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (Exception e) {
//				System.out.println(e);
			}
			this.socket = null;
		}
	}

	/**
	 * 设置座位信息
	 * 
	 * @param msg
	 */
	private void setSeatInfo(String msg) {
		String lines[] = msg.split("\n");
		ArrayList<InitState> states = new ArrayList<InitState>();

		int jet = 0, mon = 0, pos = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() > 3) {
				String items[];
				if (line.indexOf(":") != -1) {
					items = (line.split(": ")[1]).split(" ");
				} else {
					items = line.split(" ");
				}
				String playerID = items[0];
				int jetton = Integer.parseInt(items[1]);
				int money = Integer.parseInt(items[2]);
				states.add(new InitState(playerID, jetton));
				if (playerID.equals(this.playerID)) {
					jet = jetton;
					mon = money;
					pos = i + 1;
				}
			}
		}
		this.ai.setInitInfo(jet, mon, lines.length, pos, this.handNum, states);
	}

	/**
	 * 设置盲注信息
	 * 
	 * @param msg
	 */
	private void setBlindInfo(String msg) {
		String lines[] = msg.split("\n");
		int blind = 0;
		for (String line : lines) {
			if (line.length() > 3) {
				String items[] = line.split(" ");
				String pid = items[0].substring(0, items[0].length() - 1);
				int jet = Integer.parseInt(items[1]);
				ai.postBlind(pid, jet);
				if (jet > blind) {
					blind = jet;
				}
			}
		}
		ai.setBlind(blind);
	}

	/**
	 * 每个玩家分发2张底牌
	 * 
	 * @param msg
	 */
	private void setHoldInfo(String msg) {
		String lines[] = msg.split("\n");
		Poker poker[] = new Poker[2];
		for (int i = 0; i < 2; i++) {
			String items[] = lines[i].split(" ");
			poker[i] = this.convertToPoker(items[0], items[1]);
		}

		this.ai.addHoldPokers(poker[0], poker[1]);
	}

	/**
	 * 翻开三张共牌
	 * 
	 * @param msg
	 */
	private void setFlopInfo(String msg) {
		String lines[] = msg.split("\n");
		Poker poker[] = new Poker[3];
		for (int i = 0; i < 3; i++) {
			String items[] = lines[i].split(" ");
			poker[i] = this.convertToPoker(items[0], items[1]);
		}

		this.ai.addFlopPokers(poker[0], poker[1], poker[2]);
	}

	/**
	 * 翻开一张转牌
	 * 
	 * @param msg
	 */
	private void setTurnInfo(String msg) {
		String lines[] = msg.split("\n");
		Poker poker = null;
		String items[] = lines[0].split(" ");
		poker = this.convertToPoker(items[0], items[1]);
		this.ai.addTurnPoker(poker);
	}

	/**
	 * 将字符串表示的牌转换成Poker
	 * 
	 * @param color
	 * @param val
	 * @return
	 */
	private Poker convertToPoker(String color, String val) {
		Poker poker = null;
		if (color.equals("SPADES"))
			poker = new Poker(Color.SPADES, convertToValue(val));
		else if (color.equals("HEARTS"))
			poker = new Poker(Color.HEARTS, convertToValue(val));
		else if (color.equals("CLUBS"))
			poker = new Poker(Color.CLUBS, convertToValue(val));
		else if (color.equals("DIAMONDS"))
			poker = new Poker(Color.DIAMONDS, convertToValue(val));
		return poker;
	}

	/**
	 * 翻开一张河牌
	 * 
	 * @param msg
	 */
	private void setRiverInfo(String msg) {
		String lines[] = msg.split("\n");
		Poker poker = null;
		String items[] = lines[0].split(" ");
		poker = this.convertToPoker(items[0], items[1]);

		this.ai.addRiverPoker(poker);
	}

	/**
	 * 将扑克牌大小转换成数字
	 * 
	 * @param value
	 * @return
	 */
	private int convertToValue(String value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("J", "11");
		map.put("Q", "12");
		map.put("K", "13");
		map.put("A", "14");
		if (map.containsKey(value)) {
			value = map.get(value);
		}
		return Integer.parseInt(value);
	}

	/**
	 * 收到询问并作出回应
	 * 
	 * @param msg
	 */
	private void inquireAndAction(String msg) {
		String lines[] = msg.split("\n");
		ArrayList<BetState> states = new ArrayList<BetState>();
		int totalPot = 0;
		for (String line : lines) {
			if (line.startsWith("total pot")) {
				String items[] = line.split(":");
				totalPot = Integer.parseInt(items[1].substring(1,
						items[1].length() - 1));
				continue;
			}
			if (line.length() > 3) {
				String items[] = line.split(" ");
				BetState state = new BetState(items[0],
						Integer.parseInt(items[1]), Integer.parseInt(items[2]),
						Integer.parseInt(items[3]), items[4]);
				states.add(state);
			}
		}
		this.ai.setBetStates(states);
		
		// 根据当前加注状态思考对策
		String action = "";
		switch (this.ai.getPublicPokers().size()) {
		case 0:
			action = this.ai.thinkAfterHold(states);
			break;
		case 3:
			action = this.ai.thinkAfterFlop(states);
			break;
		case 4:
			action = this.ai.thinkAfterTurn(states);
			break;
		case 5:
			action = this.ai.thinkAfterRiver(states);
			break;
		}

		this.sendData(action + " \n");
	}

	private void showdown(String msg) {
		// String lines[] = msg.split("\n");
		// HashMap<String, ArrayList<Poker>> holds = new HashMap<String,
		// ArrayList<Poker>>();
		//
		// for (String line: lines) {
		// if (line.indexOf(":") != -1) {
		// String items[] = (line.split(": "))[1].split(" ");
		// String id = items[0];
		// ArrayList<Poker> pokers = new ArrayList<Poker>();
		// pokers.add(this.convertToPoker(items[1], items[2]));
		// pokers.add(this.convertToPoker(items[3], items[4]));
		// holds.put(id, pokers);
		// }
		// }
		// this.ai.parseOtherPlayerInfo(holds);
	}

	private void potwin(String msg) {
	}

	/**
	 * 处理通知信息，保存所有玩家的押注状态
	 * 
	 * @param msg
	 */
	private void setNotifyInfo(String msg) {
		// String lines[] = msg.split("\n");
		// ArrayList<BetState> states = new ArrayList<BetState>();
		// int totalPot = 0;
		// for (String line: lines) {
		// if (line.startsWith("total pot")) {
		// String items[] = line.split(":");
		// totalPot = Integer.parseInt(items[1].substring(1,
		// items[1].length() - 1));
		// continue;
		// }
		// if (line.length() > 3) {
		// String items[] = line.split(" ");
		// BetState state = new BetState(items[0],
		// Integer.parseInt(items[1]), Integer.parseInt(items[2]),
		// Integer.parseInt(items[3]), items[4]);
		// if (!state.getAction().equals("fold"))
		// states.add(state);
		// }
		// }
		// this.ai.setBetStates(states);
	}

	/**
	 * 向服务端发送数据
	 * 
	 * @param data
	 */
	private void sendData(String data) {
		this.output.println(data);
		this.output.flush();

//		System.out.println("To server: " + data);
	}

	/**
	 * 从服务端读取数据
	 * 
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> readData() throws IOException {
		ArrayList<String> inLines = new ArrayList<String>();
		String firstLine = this.input.readLine();
		int ind = firstLine.indexOf("/");
		if (ind == -1) {
			int indexOfSpace = firstLine.indexOf(" ");
			inLines.add(firstLine.substring(0, indexOfSpace));
			return inLines;
		} else {
			String token = firstLine.substring(0, ind);
			String endFlag = "/" + token;
			StringBuffer buffer = new StringBuffer();
			while (true) {
				String line = this.input.readLine();
				if (line.startsWith(endFlag))
					break;
				buffer.append(line + "\n");
			}
			inLines.add(token);
			inLines.add(buffer.toString());
			return inLines;
		}
	}
}
