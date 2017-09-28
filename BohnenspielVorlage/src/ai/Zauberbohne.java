package ai;

import java.util.Random;

public class Zauberbohne {
  
  boolean amIStarter = false;

	int random = 1;
	/**
	* @param enemyIndex The index that refers to the field chosen by the enemy in the last action.If this value is 0, than the AI is the starting player and has to specify the first move.
	* @return Return The index that refers to the field of the action chosen by this AI.
	*/
	public int getMove(int enemyIndex) {
		int index = 0;
		// This Ai starts the game
		if (enemyIndex < 1) {
			amIStarter = true;
		}
		
		
		//mache gegnerischen Zug
		Gameboard.getInstance().doMove(enemyIndex);
		
		do{
		  random = getRandomMove();
		  System.out.println("state[random]: " + Gameboard.getInstance().getState()[random-1]);
		}
		while(Gameboard.getInstance().getState()[random-1] < 1);
		
		
		
		
		//mache meinen Zug
		System.out.println("gewähltes Feld: "+ random);
		Gameboard.getInstance().doMove(random);
		
		//return index;
		return random;
		
		
		
		
		
	}
	
	private int getRandomMove(){
	  int r;
	  if(amIStarter = true){
        r = (int)(Math.random()*6 +1);
       System.out.println(random);
       }else{
        r = (int)(Math.random()*6 +7);
        System.out.println(random);
       }
	  
	  return r;
	}

}
