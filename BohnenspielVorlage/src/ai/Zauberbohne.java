package ai;

import java.util.ArrayList;
import java.util.Random;

public class Zauberbohne {

  boolean playerOne = false;

  int random = 1;
  int gespeicherterZug = -1;
  int gewuenschteTiefe = 9;
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

    index = getMinMaxMove();

    original.doMove(index, true);
    return index;



  }



  /**
   * Code from Wikipedia for MinMax-Strategy
   * 
   * @return
   */
  private int getMinMaxMove() {
    gespeicherterZug = -1;
    int bewertung = max(1, gewuenschteTiefe, 0);

    if (gespeicherterZug == -1) {
      System.out.println("Es gab keine weiteren Züge mehr");
      return -1;
    } else {
      return gespeicherterZug;
    }
  }

  /**
   * Code from Wikipedia for MinMax-Strategy
   * 
   * @param spieler
   * @param tiefe
   * @param move
   * @return
   */
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

  /**
   * Code from Wikipedia for MinMax-Strategy
   * 
   * @param spieler
   * @param tiefe
   * @param move
   * @return
   */
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

  /**
   * makes move
   * 
   * @param index
   */
  private void doMove(Integer index) {

    original.doMove(index, false);

  }

  /**
   * undo the move
   * 
   * @param board
   */
  private void undoMove(Gameboard board) {
    original = board.copy();
  }


  
   /**
   * Heuristic for strategy
   *
   * @return
   */
   private int bewerten(int move) {
  
   ///DIE AKTUELLE HEURISTIK GEWINNT IMMER MIT 30:40
   //wäre also zum bestehen des projektes geeignet
   //Allerdings nur mit Suchtiefe bis 8
   int wert = 0;
   if (playerOne) {
   wert = original.getTreasuryOne() * 4;
   wert -= original.getTreasuryTwo() * 2;
  
   wert += original.getHolesWithLotOfBeans(true);
   if (original.moveLeft(1, false)) {
   wert += original.getSumOwnRow(false);
   }
   } else {
   wert = original.getTreasuryTwo() * 4;
   wert += original.getHolesWithLotOfBeans(false);
   wert -= original.getTreasuryOne() * 2;
   if (original.moveLeft(1, true)) {
   wert += original.getSumOwnRow(true);
   }
   }
  
   switch (original.getState()[move - 1]) {
   case 1:
   wert += 1;
   break;
   case 3:
   wert += 3;
   break;
   case 5:
   wert += 5;
   break;
  
   }
  
   return wert;
   }

//Die hier ist schlechter!
  private int bewerten2(int move) {
    int wert = 0;
    if (playerOne) {
      wert = original.getTreasuryOne();
      wert -= original.getTreasuryTwo();
    } else {
      wert = original.getTreasuryTwo();
      wert -= original.getTreasuryOne();
    }
    return wert;
  }
}
