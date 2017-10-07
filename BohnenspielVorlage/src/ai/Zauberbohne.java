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
     * @param enemyIndex
     *            The index that refers to the field chosen by the enemy in the last
     *            action.If this value is 0, than the AI is the starting player and
     *            has to specify the first move.
     * @return Return The index that refers to the field of the action chosen by
     *         this AI.
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
	int bewertung = max(1, gewuenschteTiefe, 0, -1000, 1000);

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
		if (minWert <= alpha) {
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
     * estimate the value of the current state
     * 
     * @param move
     * @return value of the current state
     */
    private int bewerten(int move) {
	// initial value 0
	int value = 0;
	// the regarded player is player 1
	if (playerOne) {
	    // the first important part of the value is the current
	    // amount of already collected beans, reckoned with
	    // weight 4
	    value += original.getTreasuryOne() * 4;

	    // the second part is calculating the value of currently
	    // attackable fields on the opponent's side
	    // regards also if the field before would also bei gained
	    value = attackablefields(value, 2);

	    // the third part is on checking complete empty rows
	    // if the opponent only has empty fields, it's very good
	    // if the regarded player only has empty fields, it's very bad
	    value = checkEmptyField(value, 2);

	    // the last part of the value is the sum of beans on the own side
	    // weight 1/3
	    value += original.getSumOwnRow(playerOne) / 3;

	}
	// the regarded player is player 2
	else {
	    // the first important part of the value is the current
	    // amount of already collected beans, reckoned with
	    // weight 4
	    value += original.getTreasuryTwo() * 4;

	    // the second part is calculating the value of currently
	    // attackable fields on the opponent's side
	    // regards also if the field before would also bei gained
	    value = attackablefields(value, 1);

	    // the third part is on checking complete empty rows
	    // if the opponent only has empty fields, it's very good
	    // if the regarded player only has empty fields, it's very bad
	    value = checkEmptyField(value, 1);

	    // the last part of the value is the sum of beans on the own side
	    // weight 1/3
	    value += original.getSumOwnRow(!playerOne) / 3;

	}

	return value;
    }

    /**
     * check, if the regarded player or the opponent has only empty fields;
     * value-=15 if player has them, value+=15 if opponent has them, return original
     * value if none is the case (or both is)
     * 
     * @param value
     * @param k
     * @return
     */
    private int checkEmptyField(int value, int k) {
	// only empty fields on my side is critical
	int count = 0;
	int y = 12 - 6 * ((k + 1) % 2);
	for (int i = 6 * (k % 2); i < y; i++) {
	    if (original.getState()[i] == 0) {
		count++;
	    }
	}
	if (count == 6) {
	    value -= 15;
	}

	// only empty fields on opponent's side is great
	count = 0;
	y = 12 - 6 * (k % 2);
	for (int i = 6 * (k + 1 % 2); i < y; i++) {
	    if (original.getState()[i] == 0) {
		count++;
	    }
	}
	if (count == 6) {
	    value += 15;
	}

	return value;
    }

    /**
     * calculate the value of attackable fields on the own and the opponents side
     * add the value of fields the regarded player can attack subtract the value of
     * fields the opponent can attack
     * 
     * 
     * @param value
     * @param k
     * @return
     */
    private int attackablefields(int value, int k) {
	// the values of the fields are just added or subtracted from the state value
	// weighting issue: the current value is used, not the expected gain

	// two sets of double loops, because for calculating it is important if the
	// regarded player is
	// player one or two

	// --------set 1---------

	// regard attackable fields with 1, 3 or 5
	for (int i = 0; i < 6; i++) {
	    if (original.getState()[i] == 1 || original.getState()[i] == 3 || original.getState()[i] == 5) {
		for (int j = 6; j <= 11; j++) {
		    if (original.getState()[j] == j - i) {
			// add or subtract current value
			value -= (int) (Math.pow(-1, k) * (original.getState()[i]));

			// would the field before also be gained?

			// normal case
			if (i != 0) {
			    if (original.getState()[i - 1] == 1 || original.getState()[i - 1] == 3
				    || original.getState()[i - 1] == 5) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
			    }
			}
			// this is just the problem, that the field 0's predecessor is 11
			else {
			    if (original.getState()[11] == 1 || original.getState()[11] == 3
				    || original.getState()[11] == 5) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
			    }
			}

		    }
		}
	    }
	}

	// regard attackable fields with 1, 3 or 5
	for (int i = 6; i <= 11; i++) {
	    if (original.getState()[i] == 1 || original.getState()[i] == 3 || original.getState()[i] == 5) {
		for (int j = 0; j < 6; j++) {
		    if (original.getState()[j] == i - j) {
			// add or subtract current value
			value += (int) (Math.pow(-1, k) * (original.getState()[i]));

			// would the field before also be gained?

			// no special case needed here
			if (original.getState()[i - 1] == 1 || original.getState()[i - 1] == 3
				|| original.getState()[i - 1] == 5) {
			    value += (int) (Math.pow(-1, k) * (original.getState()[i]));
			}
		    }
		}
	    }
	}

	// --------set 2---------

	// regard attackable fields with 0, 2 or 4
	for (int i = 0; i < 6; i++) {
	    if (original.getState()[i] == 0 || original.getState()[i] == 2 || original.getState()[i] == 4) {
		for (int j = 6; j <= 11; j++) {
		    if (original.getState()[j] == j - i + 12) {
			// add or subtract current value
			value -= (int) (Math.pow(-1, k) * (original.getState()[i]));

			// would the field before also be gained?

			// normal case
			if (i != 0) {
			    if (original.getState()[i - 1] == 0 || original.getState()[i - 1] == 2
				    || original.getState()[i - 1] == 4) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
			    }
			}
			// this is just the problem, that the field 0's predecessor is 11
			else {
			    if (original.getState()[11] == 0 || original.getState()[11] == 2
				    || original.getState()[11] == 4) {
				value -= (int) (Math.pow(-1, k) * (original.getState()[i]));
			    }
			}

		    }
		}
	    }
	}

	// regard attackable fields with 0, 2 or 4
	for (int i = 6; i <= 11; i++) {
	    if (original.getState()[i] == 0 || original.getState()[i] == 2 || original.getState()[i] == 4) {
		for (int j = 0; j < 6; j++) {
		    if (original.getState()[j] == i - j + 12) {
			// add or subtract current value
			value += (int) (Math.pow(-1, k) * (original.getState()[i] + 1));

			// would the field before also be gained?

			// no special case needed here
			if (original.getState()[i - 1] == 0 || original.getState()[i - 1] == 2
				|| original.getState()[i - 1] == 4) {
			    value += (int) (Math.pow(-1, k) * (original.getState()[i] + 1));
			}
		    }
		}
	    }
	}

	return value;
    }
}
