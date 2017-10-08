/**
 * 
 */
package ai;

import java.util.ArrayList;

/**
 * @author jzalonis
 */
public class Gameboard {
  // Array with bean-holes
  private int[] state = new int[12];

  // Schatzkammer red
  private int treasuryOne = 0;

  // Schatzkammer blue
  private int treasuryTwo = 0;


  public Gameboard() {
    initialize();
  }

  /**
   * Copys the Gameboard with an deep-copy
   * 
   * @return
   */
  public Gameboard copy() {
    Gameboard other = new Gameboard();
    for (int i = 0; i < 12; i++) {
      other.state[i] = this.state[i];

    }
    other.treasuryOne = this.treasuryOne;
    other.treasuryTwo = this.treasuryTwo;
    return other;
  }


  /**
   * initializes the board
   */
  private void initialize() {
    for (int i = 0; i < state.length; i++) {
      state[i] = 6;
    }
    treasuryTwo = 0;
    treasuryOne = 0;

  }

  /**
   * 
   * @param index Goes from 1.... 12! Watch out!!
   * @param finaldecisicon just an parameter for printing
   */
  public void doMove(int index) {
    // if move is not permissible do nothing
    if (index < 1 || index > 12) {
      return;
    }

    // distribute beans to other holes
    int sharedBeans = state[index - 1];
    state[index - 1] = 0;

    for (int i = 0; i < sharedBeans; i++) {
      state[(index + i) % 12]++;
    }

    // is this the red or blue player?
    boolean playerOne = false;
    if (index < 6) {
      playerOne = true;
    }
    // tests the field of number of beans in it
    testField(index, sharedBeans, playerOne);

  }

  /**
   * 
   * @param index index of field that was chosen for the move
   * @param sharedBeans number of shared beans (so you can calculate the last field)
   * @param playerOne to check wich player gets the beans
   */
  private void testField(int index, int sharedBeans, boolean playerOne) {

    // check the field
    switch (state[(sharedBeans + index - 1) % 12]) {

      case 2:
      case 4:
      case 6:

        // put beans in right treasury
        if (playerOne) {
          treasuryOne += state[(sharedBeans + index - 1) % 12];
        } else {
          treasuryTwo += state[(sharedBeans + index - 1) % 12];
        }
        // delete the hole
        state[(sharedBeans + index - 1) % 12] = 0;

        // recursive testing of all other fields
        if (index == 1) {
          testField(12, sharedBeans, playerOne);
        } else {
          testField(index - 1, sharedBeans, playerOne);
        }
    }
  }


  /**
   * 
   * @param player 1 --> me, -1 ---> enemy
   * @param playerOne
   * @return if any moves are left
   */
  public boolean moveLeft(int player, boolean playerOne) {
    boolean movePossible = false;
    int i, j;

    if (player == 1) { // its my turn
      if (playerOne) { // and i am the red player
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    } else { // turn of enemy
      if (!playerOne) { // i am the blue player
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    }
    // check if possible move left
    for (int k = i; k < j; k++) {
      if (state[k] > 0) {
        movePossible = true;
        return movePossible;
      }
    }
    return movePossible;
  }

  /**
   * prints the array
   */
  public void print() {
    System.out.print("\t");
    for (int i = 11; i >= 6; i--) {
      System.out.print(state[i] + "\t | \t");

    }
    System.out.println();
    System.out.print(treasuryOne);
    for (int i = 11; i >= 6; i--) {
      System.out.print(" \t \t");

    }
    System.out.print("\t");
    System.out.print(treasuryTwo);
    System.out.println();
    System.out.print("\t");

    for (int i = 0; i < 6; i++) {
      System.out.print(state[i] + "\t | \t");

    }
    System.out.println();
  }

  /**
   * 
   * @return state
   */
  public int[] getState() {
    return state;
  }


  /**
   * creates all possible moves
   * 
   * @param player: me or enemy
   * @return list of all possible moves
   */
  public ArrayList<Integer> createPossibleMoves(int player, boolean playerOne) {
    int i, j;
    ArrayList<Integer> move = new ArrayList<Integer>();
    if (player == 1) { // its my turn
      if (playerOne) { // and i am the red player
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    } else { // turn of enemy
      if (!playerOne) { // i am the blue player
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    }

    // add moves
    for (int k = i; k < j; k++) {
      if (state[k] > 0) {
        move.add(k + 1);
      }
    }

    return move;
  }

  public int getTreasuryOne() {
    return treasuryOne;
  }

  public int getTreasuryTwo() {
    return treasuryTwo;
  }

  /**
   * 
   * @param playerOne
   * @return the Sum of all beans in own or enemies row
   */
  public int getSumOwnRow(boolean playerOne) {
    int i, j, sum = 0;
    if (playerOne) {
      i = 0;
      j = 6;
    } else {
      i = 6;
      j = 12;
    }
    for (int k = i; k < j; k++) {
      sum += state[k];
    }
    return sum;
  }


}
