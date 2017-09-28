package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import ai.Zauberbohne;
import util.Settings;

/** Außer dem Attribut ai_name darf an dieser Klasse nichts verändert werden. */
public class GameClient {

	private static String ai_name = "ZauberbohneNEU";
	private String gameID;
	private Zauberbohne ai;

	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Name: " + ai_name);
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			System.out.print("Spielnummer eingeben (0 für neues Spiel): ");
			String eingabe = br.readLine();
			int playMode = Integer.parseInt(eingabe);
			
			GameClient client = new GameClient();

			if (playMode == 0) {
				client.createGame();
			} else {
				client.joinGame("" + playMode);
			}
		} catch (NumberFormatException e) {
			System.err.println("Invalid number. Call with 0 to create server or ID to join ID and client name.");
		}
	}

	public GameClient() {
		this.ai = new Zauberbohne();
	}

	public void createGame() throws Exception {
		String url = Settings.SERVER_ADDRESS + "/api/creategame/" + ai_name;
		this.gameID = load(url);
		System.out.println("Spiel erstellt. ID: " + this.gameID);

		url = Settings.SERVER_ADDRESS + "/api/check/" + this.gameID + "/" + ai_name;
		while (true) {
			Thread.sleep(500);
			String state = load(url);
			if (state.equals("0") || state.equals("-1")) {
				break;
			} else if (state.equals("-2")) {
				System.out.println("time out");
				return;
			}
		}
		play(0);
	}

	public void joinGame(String gameID) throws Exception {
		String url = Settings.SERVER_ADDRESS + "/api/joingame/" + gameID + "/" + ai_name;
		this.gameID = gameID;
		String state = load(url);
		System.out.println("Join-Game-State: " + state);
		if (state.equals("1")) {
			play(6);
		} else if (state.equals("0")) {
			System.out.println("error (join game)");
		}
	}

	private void play(int offset) throws Exception {
		String checkURL = Settings.SERVER_ADDRESS + "/api/check/" + this.gameID + "/" + ai_name;
		String stateIdURL = Settings.SERVER_ADDRESS + "/api/state/" + this.gameID;
		int start, end;
		if (offset == 0) {
			start = 7;
			end = 12;
		} else {
			start = 1;
			end = 6;
		}

		while (true) {
			int moveState = Integer.parseInt(load(checkURL));
			int stateID = Integer.parseInt(load(stateIdURL));

			if (ownTurn(stateID, moveState, start, end)) {
				makeOwnMove(moveState);
			} else if (gameIsFinished(stateID, moveState)) {
				checkURL = Settings.SERVER_ADDRESS + "/api/statemsg/" + this.gameID;
				System.out.println("Das Spiel wurde beendet.");
				return;
			} 

			Thread.sleep(20);
		}
	}

	private boolean ownTurn(int stateID, int moveState, int start, int end) {
		return (stateID != 2) && (((start <= moveState) && (moveState <= end)) || (moveState == -1));
	}

	private boolean gameIsFinished(int stateID, int moveState) {
		return (moveState == -2) || (stateID == 2);
	}

	private void makeOwnMove(int enemyMove) throws Exception {
		int selectedField = this.ai.getMove(enemyMove);
		move(selectedField);
	}

	private void move(int fieldID) throws Exception {
		String url = Settings.SERVER_ADDRESS + "/api/move/" + this.gameID + "/" + ai_name + "/" + fieldID;
		System.out.print(load(url));
	}

	private static String load(String url) throws Exception {
		URI uri = new URI(url.replace(" ", ""));
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(uri.toURL().openConnection().getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
		}
		bufferedReader.close();
		return (sb.toString());
	}
}