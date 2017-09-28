package ai;

import java.util.ArrayList;
import java.util.Random;

public class Zauberbohne {

  boolean playerOne = false;

  int random = 1;
  int gespeicherterZug = -1;
  int gewuenschteTiefe = 8;
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
  private int bewerten2(int move) {

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


  private int bewerten(int move) {
    int value = 0;
    if (playerOne) {

      value = attackablefields(value, 2);
      value = checkEmptyField(value, 2);
      value = owningHighFields(value, 2);

      // putting more beans on own side is good, otherwise bad
      int differenceOwnSide = original.getSumOwnRow(playerOne) - original.getSumOwnRow(playerOne);
      int differenceOtherSide =
          original.getSumOwnRow(!playerOne) - original.getSumOwnRow(!playerOne);
      if (differenceOwnSide > differenceOtherSide) {
        value += 2;
      } else {
        value -= 2;
      }

      // using high numbers if one's low on opportunities is good
      if (original.getState()[move - 1] >= 10 && move - 1 < 6) {
        value += 3;
      }
      if (original.getState()[move - 1] >= 10 && move - 1 >= 6) {
        value -= 3;
      }
    }

    else {
      value = attackablefields(value, 1);
      value = checkEmptyField(value, 1);
      value = owningHighFields(value, 1);

      // putting more beans on own side is good, otherwise bad
      int differenceOwnSide = original.getSumOwnRow(!playerOne) - original.getSumOwnRow(!playerOne);
      int differenceOtherSide = original.getSumOwnRow(playerOne) - original.getSumOwnRow(playerOne);
      if (differenceOwnSide > differenceOtherSide) {
        value += 2;
      } else {
        value -= 2;
      }

      // using high numbers if one's low on opportunities is good
      if (original.getState()[move - 1] >= 10 && move - 1 >= 6) {
        value += 3;
      }
      if (original.getState()[move - 1] >= 10 && move - 1 < 6) {
        value -= 3;
      }
    }

    return value;
  }



  /**
   * @param value
   * @param k
   * @return
   */
  private int owningHighFields(int value, int k) {
    // owning fields with a lot beans is good, it's bad, if the opponent has some
    for (int i = 0; i < 6; i++) {
      if (original.getState()[i] >= 6) {
        value += (int) (Math.pow(-1, k) * 1);
      }
    }
    for (int i = 6; i <= 11; i++) {
      if (original.getState()[i] >= 6) {
        value -= (int) (Math.pow(-1, k) * 1);
      }
    }
    return value;
  }



  /**
   * @param value
   * @param k
   * @return
   */
  private int checkEmptyField(int value, int k) {
    // too many own empty fields are bad
    int count = 0;
    for (int i = 0; i < 6; i++) {
      if (original.getState()[i] == 0) {
        count++;
      }
    }
    switch (count) {
      case 0:
        value += (int) (Math.pow(-1, k) * 2);
        break;
      case 1:
        value += (int) (Math.pow(-1, k) * 1);
        break;
      case 2:
        break;
      case 3:
        value += (int) (Math.pow(-1, k) * -1);
        break;
      case 4:
        value -= (int) (Math.pow(-1, k) * -2);
        break;
      case 5:
        value -= (int) (Math.pow(-1, k) * -3);
        break;
      case 6:
        value -= (int) (Math.pow(-1, k) * -4);
        break;
    }

    // many empty fields on opponent's side are good
    count = 0;
    for (int i = 6; i <= 11; i++) {
      if (original.getState()[i] == 0) {
        count++;
      }
    }
    switch (count) {
      case 0:
        value -= (int) (Math.pow(-1, k) * 2);
        break;
      case 1:
        value -= (int) (Math.pow(-1, k) * 1);
        break;
      case 2:
        break;
      case 3:
        value += (int) (Math.pow(-1, k) * 1);
        break;
      case 4:
        value += (int) (Math.pow(-1, k) * 2);
        break;
      case 5:
        value += (int) (Math.pow(-1, k) * 3);
        break;
      case 6:
        value += (int) (Math.pow(-1, k) * 4);
        break;
    }
    return value;
  }



  /**
   * @param value
   * @param k
   * @return
   */
    private int attackablefields(int value, int k) {
	// attackable own 1, 3 and 5 are problematic
	for (int i = 0; i < 6; i++) {
	    if (original.getState()[i] == 1 || original.getState()[i] == 3 || original.getState()[i] == 5) {
		for (int j = 6; j <= 11; j++) {
		    if (original.getState()[j] == j - i) {
			value -= (int) (Math.pow(-1, k) * original.getState()[i] * 2);
		    }
		}
	    }
	    if (original.getState()[i] == 0 || original.getState()[i] == 2 || original.getState()[i] == 4) {
		for (int j = 6; j <= 11; j++) {
		    if (original.getState()[j] == 12 + j - i) {
			value -= (int) (Math.pow(-1, k) * original.getState()[i] * 2);
		    }
		}
	    }
	}

    // attackable 1, 3 and 5 on opponent's side are good
	for (int i = 6; i <= 11; i++) {
	    if (original.getState()[i] == 1 || original.getState()[i] == 3 || original.getState()[i] == 5) {
		for (int j = 0; j < 6; j++) {
		    if (original.getState()[j] == j - i) {
			value += (int) (Math.pow(-1, k) * original.getState()[i] * 2);
		    }
		}
	    }
	    if (original.getState()[i] == 0 || original.getState()[i] == 2 || original.getState()[i] == 4) {
		for (int j = 0; j < 6; j++) {
		    if (original.getState()[j] == 12 + j - i) {
			value += (int) (Math.pow(-1, k) * original.getState()[i] * 2);
		    }
		}
	    }
	}

    return value;
  }
}
