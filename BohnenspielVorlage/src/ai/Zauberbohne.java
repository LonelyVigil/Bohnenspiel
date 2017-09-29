package ai;

import java.util.ArrayList;

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

    index = getAlphaBeta();

    original.doMove(index, true);
    return index;

  }



  /**
   * Code from Wikipedia for MinMax-Strategy
   * 
   * @return
   */
  private int getAlphaBeta() {
    gespeicherterZug = -1;
    int bewertung = max(1, gewuenschteTiefe,0, -1000, 1000);

    if (gespeicherterZug == -1) {
      System.out.println("Es gab keine weiteren Z�ge mehr");
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
  private int max(int spieler, int tiefe, int move, int alpha, int beta) {
    if (tiefe == 0 || !original.moveLeft(spieler, playerOne)) {
      return bewerten(move);
    }

    int maxWert = alpha;
    ArrayList<Integer> moves = original.createPossibleMoves(spieler, playerOne);
    while (!moves.isEmpty()) {
      int currentMove = moves.get(0);
      moves.remove(0);
      Gameboard currentboard = original.copy();
      doMove(currentMove);
      int wert = min(-spieler, tiefe - 1, currentMove, maxWert, beta);

      if (wert > maxWert) {
        maxWert = wert;
        if (maxWert >= beta)
           break;
        if (tiefe == gewuenschteTiefe)
           gespeicherterZug = currentMove;
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
  private int min(int spieler, int tiefe, int move, int alpha, int beta) {
    if (tiefe == 0 || !original.moveLeft(spieler, playerOne)) {
      return bewerten(move);
    }

    int minWert = beta;
    ArrayList<Integer> moves = original.createPossibleMoves(spieler, playerOne);
    while (!moves.isEmpty()) {
      int currentMove = moves.get(0);
      moves.remove(0);
      Gameboard currentboard = original.copy();
      doMove(currentMove);
      int wert = max(-spieler, tiefe - 1, currentMove, alpha, minWert);

      if (wert < minWert) {
        minWert = wert;
        if(minWert <= alpha){
          break;
        }
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
	    value += original.getTreasuryOne() * 5;

	    value = attackablefields(value, 2);
	    value = checkEmptyField(value, 2);
	    value = owningHighFields(value, 2);
	    
	} else {
	    value += original.getTreasuryTwo() * 5;

	    value = attackablefields(value, 1);
	    value = checkEmptyField(value, 1);
	    value = owningHighFields(value, 1);
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
//	for (int i = 0; i < 6; i++) {
//	    if(original.getState()[i]>=12) {
//		value += (int) (Math.pow(-1, k) * 5);
//	    }
//	    else 
//	    if (original.getState()[i] >= 9) {
//		value += (int) (Math.pow(-1, k) * 1);
//	    }
//	}
//	for (int i = 6; i <= 11; i++) {
//
//	    if (original.getState()[i] >= 12) {
//		value -= (int) (Math.pow(-1, k) * 2);
//	    }
//	    else 
//	    if (original.getState()[i] >= 9) {
//		value -= (int) (Math.pow(-1, k) * 1);
//	    }
//	}
	if(k==2) {
	    for(int i=0; i<6; i++) {

		    if (original.getState()[i] >= 9) {
			value += 2;
		    }
	    }
	}
	else {
	    for(int i=6; i<12; i++) {

		    if (original.getState()[i] >= 9) {
			value += 2;
		    }
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
	int y = 12 - 6 * ((k+1) % 2);
	for (int i = 6 * (k % 2); i < y; i++) {
	    if (original.getState()[i] == 0) {
		count++;
	    }
	}
	if(count<=5) {
	    value+=-count+2;
	}
	else {
	    value-=500;
	}
		
	// many empty fields on opponent's side are good
	count = 0;
	y = 12 - 6 * (k % 2);
	for (int i = 6 * (k+1 % 2); i < y; i++) {
	    if (original.getState()[i] == 0) {
		count++;
	    }
	}
	if(count<=5) {
	   value+=count-2; 
	}
	else {
	    value+=500;
	}
	return value;
    }

  /**
   * @param value
   * @param k
   * @return
   */
    private int attackablefields(int value, int k) {
	// attackable own 1, 3 and 5 are problematic, attackable 1, 3 and 5 on opponent's side are good 
	for (int i = 0; i < 6; i++) {
	    if (original.getState()[i] == 1 || original.getState()[i] == 3 || original.getState()[i] == 5) {
		for (int j = 6; j <= 11; j++) {
		    if (original.getState()[j] == j - i) {
			value -= (int) (Math.pow(-1, k) * (original.getState()[i]) * 2);
			if (i != 0) {
			    if (original.getState()[i - 1] == 1 || original.getState()[i - 1] == 3
				    || original.getState()[i - 1] == 5) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]) * 2);
			    }
			} else {
			    if (original.getState()[11] == 1 || original.getState()[11] == 3
				    || original.getState()[11] == 5) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]) * 2);
			    }
			}

		    }
		}
	    }
	}

	// attackable own 1, 3 and 5 are problematic, attackable 1, 3 and 5 on opponent's side are good 
	for (int i = 6; i <= 11; i++) {
	    if (original.getState()[i] == 1 || original.getState()[i] == 3 || original.getState()[i] == 5) {
		for (int j = 0; j < 6; j++) {
		    if (original.getState()[j] == j - i) {
			value += (int) (Math.pow(-1, k) * (original.getState()[i]) * 2);
			if (original.getState()[i - 1] == 1 || original.getState()[i - 1] == 3
				|| original.getState()[i - 1] == 5) {
			    value -= (int) (Math.pow(-1, k) * (original.getState()[i]) * 2);
			}
		    }
		}
	    }
	}

    return value;
  }
}
