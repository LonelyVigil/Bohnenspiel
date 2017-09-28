/**
 * 
 */
package ai;

/**
 * @author jzalonis
 *
 *
 *SELTSAME FEHLER!!!
 *
 */
public class Gameboard {
  // Array mit den Bohnenfeldern
  //Achtung: index von 0 - 11
 private int[] state = new int[12];

  // Schatzkammer links
 private int treasuryStarter = 0;

  // Schatzkammer rechts
private  int treasurySecond = 0;
  
public static int count = 0;
  
  public static Gameboard instance;
  
  public Gameboard(){
    initialize();
    print();
  }
  
  
  public static Gameboard getInstance(){
    if(instance == null){
      instance = new Gameboard();
    }
    return instance;
  }
  
  private void initialize(){
    for(int i = 0; i < state.length; i++){
      state[i] = 6;
    }
    treasurySecond = 0;
    treasuryStarter = 0;
    
  }

  //index geht von 1 ... 12!!!
  public void doMove(int index){
    if(index < 1 || index > 11){
      return;
    }

    //Verteile Bohnen auf andere Felder
    int sharedBeans = state[index-1];
    state[index-1] = 0;
    
    for(int i = 0; i < sharedBeans; i++){
      state[(index+i)%12]++;
    }
  
    //Teste ob letztes Feld mit 2,4 oder 6 Bohnen
    testField(index, sharedBeans); 
    
    //print Spielfeld
   System.out.println(count++ + "------------------------------------");
    print();
  }
  
  
  private void testField(int index, int sharedBeans){
    
    boolean filled = false;
    
    //Teste ob letztes Feld mit 2,4 oder 6 Bohnen
    switch(state[(sharedBeans  + index-1)%12]){
      
      //Feld passend gefüllt
      case 2: case 4: case 6: filled = true;
      if(7 <= index && index <= 12){
        treasurySecond += state[(sharedBeans  + index-1)%12];
      }else{
        treasuryStarter += state[(sharedBeans  + index-1)%12];
      }
      state[(sharedBeans  + index-1)%12] = 0;
      
      //Testet die hinteren Felder
      if(index == 1){
        testField(12, sharedBeans);
      }else{
        testField(index-1, sharedBeans);
      }
      
    }
  }

  private void print(){
    System.out.print("\t");
    for(int i = 11; i >= 6; i--){
      System.out.print(state[i] + "\t | \t" );
      
    }
    System.out.println();
    System.out.print(treasuryStarter);
    for(int i = 11; i >= 6; i--){
      System.out.print(" \t \t" );
      
    }
    System.out.print("\t");
    System.out.print(treasurySecond);
    System.out.println();
    System.out.print("\t");
    
    for(int i = 0; i < 6; i++){
      System.out.print(state[i] + "\t | \t" );
      
    }
    System.out.println();
  }
  
  
  public int[] getState(){
    return state;
  }
}
