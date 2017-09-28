/**
 * 
 */
package ai;

import java.util.ArrayList;

/**
 * @author jzalonis
 */
public class Gameboard {
  // Array mit den Bohnenfeldern
  // Achtung: index von 0 - 11
  private int[] state = new int[12];

  // Schatzkammer links
  private int treasuryOne = 0;

  // Schatzkammer rechts
  private int treasuryTwo = 0;


  public Gameboard() {
    initialize();
    //print();
  }

  public Gameboard copy() {
     Gameboard other = new Gameboard();
    for (int i = 0; i < 12; i++) {
      other.state[i] = this.state[i];
      
    }
    other.treasuryOne = this.treasuryOne;
    other.treasuryTwo = this.treasuryTwo;
    return other;
  }


  // public static Gameboard getInstance() {
  // if (instance == null) {
  // instance = new Gameboard();
  // }
  // return instance;
  // }

  private void initialize() {
    for (int i = 0; i < state.length; i++) {
      state[i] = 6;
    }
    treasuryTwo = 0;
    treasuryOne = 0;

  }

  // index geht von 1 ... 12!!!
  public void doMove(int index, boolean finaldecisicon) {
    if (index < 1 || index > 12) {
      return;
    }

    // Verteile Bohnen auf andere Felder
    int sharedBeans = state[index - 1];
    state[index - 1] = 0;

    for (int i = 0; i < sharedBeans; i++) {
      state[(index + i) % 12]++;
    }

    boolean playerOne = false;
    if (index < 6) {
      playerOne = true;
    }
    testField(index, sharedBeans, playerOne);


    // print Spielfeld
//    if(finaldecisicon){
//      System.out.println("------------------------------------");
//      print();
//    }
   
  }

  // player one ist immer der untere (rote) Spieler, mit index 1--6
  private void testField(int index, int sharedBeans, boolean playerOne) {

    boolean filled = false;

    // Teste ob letztes Feld mit 2,4 oder 6 Bohnen
    switch (state[(sharedBeans + index - 1) % 12]) {

      // Feld passend gefüllt
      case 2:
      case 4:
      case 6:
        filled = true;
        if (playerOne) {
          treasuryOne += state[(sharedBeans + index - 1) % 12];
        } else {
          treasuryTwo += state[(sharedBeans + index - 1) % 12];
        }

        state[(sharedBeans + index - 1) % 12] = 0;

        // Testet die hinteren Felder
        if (index == 1) {
          testField(12, sharedBeans, playerOne);
        } else {
          testField(index - 1, sharedBeans, playerOne);
        }

    }
  }


  // player = 1 --> me
  // player = -1 --> enemy
  public boolean moveLeft(int player, boolean playerOne) {
    boolean movePossible = false;
    int i, j;
    // if(player == 1 && playerOne){
    // i = 0;
    // j = 6;
    // }else{
    // if(player == 1 && !playerOne){
    //
    // }
    // }
    //
    if (player == 1) { // Mein Zug
      if (playerOne) { // ich bin starter
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    } else { // gegnerischer Zug
      if (!playerOne) { // gegner hat gestartet
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    }

    for (int k = i; k < j; k++) {
      if (state[k] > 0) {
        movePossible = true;
        return movePossible;
      }
    }
    return movePossible;
  }

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


  public int[] getState() {
    return state;
  }


  /**
   * @author jzalonis
   * @param spieler
   * @return
   */
  public ArrayList<Integer> createPossibleMoves(int spieler, boolean playerOne) {
    int i, j;
    ArrayList<Integer> move = new ArrayList<Integer>();
    if (spieler == 1) { // Mein Zug
      if (playerOne) { // ich bin starter
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    } else { // gegnerischer Zug
      if (!playerOne) { // gegner hat gestartet
        i = 0;
        j = 6;
      } else {
        i = 6;
        j = 12;
      }
    }

    for (int k = i; k < j; k++) {
      if (state[k] > 0) {
        move.add(k+1);
      }
    }

    return move;
  }
  
  public int getTreasuryOne(){
    return treasuryOne;
  }
  
  public int getTreasuryTwo(){
    return treasuryTwo;
  }
 
  public int getSumOwnRow(boolean playerOne){
    int i,j, sum = 0;
    if(playerOne){
      i = 0;
      j = 6;
    }else{
      i = 6;
      j = 12;
    }
    for(int k = i; k < j; k++){
      sum += state[k];
    }
    return sum;
  }
}
