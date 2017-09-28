package ai;

import java.util.ArrayList;
import java.util.Random;

public class Zauberbohne {

  boolean playerOne = false;

  int random = 1;
  int gespeicherterZug = -1;
  int gewuenschteTiefe = 7;
  Gameboard copy = new Gameboard();
  Gameboard original = new Gameboard();

  /**
   * @param enemyIndex The index that refers to the field chosen by the enemy in the last action.If
   *        this value is 0, than the AI is the starting player and has to specify the first move.
   * @return Return The index that refers to the field of the action chosen by this AI.
   */
  public int getMove(int enemyIndex) {
    int index = 0;
    // This Ai starts the game
    if (enemyIndex < 1) {
      playerOne = true;
      System.out.println("Lets play!");
    }


    // mache gegnerischen Zug
    original.doMove(enemyIndex, true);

    // do {
    // random = getRandomMove();
    // System.out.println("state[" + random + "]: " + original.getState()[random - 1]);
    // } while (original.getState()[random - 1] < 1);

    // mache meinen Zug
    // System.out.println("gewähltes Feld: " + random);
    // original.doMove(random);

    // return index;
    // return random;
    index = getMinMaxMove();

    original.doMove(index, true);
    return index;



  }

  private int getRandomMove() {
    int r;
    if (playerOne) {
      r = (int) (Math.random() * 6) + 1;
      System.out.println(random);
    } else {
      r = (int) (Math.random() * 6) + 7;
      System.out.println(random);
    }

    return r;
  }

  // Code aus Wiki
  private int getMinMaxMove() {
    gespeicherterZug = -1;
    int bewertung = max(1, gewuenschteTiefe, 0);

    if (gespeicherterZug == -1) {
      System.out.println("Es gab keine weiteren Züge mehr");
      return getRandomMove();

    } else {
      return gespeicherterZug;
    }
  }

  // WIKI:
  private int max(int spieler, int tiefe, int move) {
    if (tiefe == 0 || !original.moveLeft(spieler, playerOne)) {
      return bewerten(move);
    }

    int maxWert = -10000;
    ArrayList<Integer> moves = original.createPossibleMoves(spieler, playerOne);
    while (!moves.isEmpty()) {
      int currentMove = moves.get(0);
      moves.remove(0);
      Gameboard currentboard = original.copy();
      doMove(currentMove);
      int wert = min(-spieler, tiefe - 1, currentMove);

      if (wert > maxWert) {
        maxWert = wert;
        if (tiefe == gewuenschteTiefe) {
          gespeicherterZug = currentMove;
        }
      }
      undoMove(currentboard);
    }

    return maxWert;
  }


  private int min(int spieler, int tiefe, int move) {
    if (tiefe == 0 || !original.moveLeft(spieler, playerOne)) {
      return bewerten(move);
    }

    int minWert = 10000;
    ArrayList<Integer> moves = original.createPossibleMoves(spieler, playerOne);
    while (!moves.isEmpty()) {
      int currentMove = moves.get(0);
      moves.remove(0);
      Gameboard currentboard = original.copy();
      doMove(currentMove);
      int wert = max(-spieler, tiefe - 1, currentMove);

      if (wert < minWert) {
        minWert = wert;
      }
      undoMove(currentboard);
    }
    return minWert;
  }


  private void doMove(Integer index) {

    original.doMove(index, false); // führt zug aus, veränderd spielstand

  }

  private void undoMove(Gameboard board) {
    original = board.copy(); // setzt spielstand zurück
  }



  /**
   * @author jzalonis
   * @return
   */
  private int bewerten(int move) {
    int wert = 0;
    if (playerOne) {
      wert = original.getTreasuryOne();
       wert += original.getSumOwnRow(true);
    } else {
      wert = original.getTreasuryTwo();
      wert += original.getSumOwnRow(false);
    }

    switch (original.getState()[move-1]) {
      case 1:
        wert += 1*5;
        break;
      case 3:
        wert += 3*5;
        break;
      case 5:
        wert += 5*5;
        break;

    }

    return wert;
  }



}
