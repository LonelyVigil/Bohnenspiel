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
	    value += original.getTreasuryOne() * 4;

	    value = attackablefields(value, 2);	           //quite good
	    value = checkEmptyField(value, 2);		   //ok
	    //value = owningHighFields(value, 2);	   //no real impact
	    value += original.getSumOwnRow(playerOne)/3;   //good
	} else {
	    value += original.getTreasuryTwo() * 4;

	    value = attackablefields(value, 1);             //quite good
	    value = checkEmptyField(value, 1);              //ok
	    //value = owningHighFields(value, 1);           //no real impact
	    value += original.getSumOwnRow(!playerOne)/3;   //good

	}

    return value;
  }



//  /**
//   * @param value
//   * @param k
//   * @return
//   */
//    private int owningHighFields(int value, int k) {
//	// owning fields with a lot beans is good, it's bad, if the opponent has some
//	for (int i = 0; i < 6; i++) {
//	    if (original.getState()[i] >= 7) {
//		value += (int) (Math.pow(-1, k) * 1);
//	    }
//	}
//	for (int i = 6; i <= 11; i++) {
//	    if (original.getState()[i] >= 7) {
//		value -= (int) (Math.pow(-1, k) * 1);
//	    }
//	}
//	return value;
//    }
    
    

  /**
   * @param value
   * @param k
   * @return
   */
    private int checkEmptyField(int value, int k) {
	// only empty fields on my side is critical
	int count = 0;
	int y = 12 - 6 * ((k+1) % 2);
	for (int i = 6 * (k % 2); i < y; i++) {
	    if (original.getState()[i] == 0) {
		count++;
	    }
	}
	
	if(count==6) {
	    value-=15;
	}
		
	// only empty fields on opponent's side is great
	count = 0;
	y = 12 - 6 * (k % 2);
	for (int i = 6 * (k+1 % 2); i < y; i++) {
	    if (original.getState()[i] == 0) {
		count++;
	    }
	}
	if(count==6) {
	    value+=15;
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
			value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
			if (i != 0) {
			    if (original.getState()[i - 1] == 1 || original.getState()[i - 1] == 3
				    || original.getState()[i - 1] == 5) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
			    }
			} else {
			    if (original.getState()[11] == 1 || original.getState()[11] == 3
				    || original.getState()[11] == 5) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
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
		    if (original.getState()[j] == i - j) {
			value += (int) (Math.pow(-1, k) * (original.getState()[i]));
			if (original.getState()[i - 1] == 1 || original.getState()[i - 1] == 3
				|| original.getState()[i - 1] == 5) {
			    value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
			}
		    }
		}
	    }
	}
	
	// attackable own 1, 3 and 5 are problematic, attackable 1, 3 and 5 on opponent's side are good 
		for (int i = 0; i < 6; i++) {
		    if (original.getState()[i] == 0 || original.getState()[i] == 2 || original.getState()[i] == 4) {
			for (int j = 6; j <= 11; j++) {
			    if (original.getState()[j] == j - i +12) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
				if (i != 0) {
				    if (original.getState()[i - 1] == 1 || original.getState()[i - 1] == 3
					    || original.getState()[i - 1] == 5) {
					value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
				    }
				} else {
				    if (original.getState()[11] == 0 || original.getState()[11] == 2
					    || original.getState()[11] == 4) {
					value -= (int) (Math.pow(-1, k) * (original.getState()[i]) );
				    }
				}

			    }
			}
		    }
		}

		// attackable own 1, 3 and 5 are problematic, attackable 1, 3 and 5 on opponent's side are good 
		for (int i = 6; i <= 11; i++) {
		    if (original.getState()[i] == 0 || original.getState()[i] == 2 || original.getState()[i] == 4) {
			for (int j = 0; j < 6; j++) {
			    if (original.getState()[j] == i - j + 12) {
				value += (int) (Math.pow(-1, k) * (original.getState()[i]+1));
				if (original.getState()[i - 1] == 0 || original.getState()[i - 1] == 2
					|| original.getState()[i - 1] == 4) {
				    value -= (int) (Math.pow(-1, k) * (original.getState()[i]+1));
				}
			    }
			}
		    }
		}

    return value;
  }
}
