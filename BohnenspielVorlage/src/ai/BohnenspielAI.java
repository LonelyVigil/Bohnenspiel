package ai;

import java.util.Random;

public class BohnenspielAI {

	Random rand = new Random();
	
	/**
	* @param enemyIndex The index that refers to the field chosen by the enemy in the last action.If this value is 0, than the AI is the starting player and has to specify the first move.
	* @return Return The index that refers to the field of the action chosen by this AI.
	*/
	public int getMove(int enemyIndex) {
		int index = 0;
		// have to choose the first move
		if (enemyIndex == -1) {
			
			index = rand.nextInt(6) + 1;
		}
		// enemy acted and i have to react
		else if (enemyIndex > 0 && enemyIndex <= 6) {
			index = rand.nextInt(6) + 7;
		}
		else if (enemyIndex > 6 && enemyIndex <= 12) {
			index = rand.nextInt(6) + 1;
		}
		return index;
	}
	


}
