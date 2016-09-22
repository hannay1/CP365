
package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;


public class MyAgent extends BasicMarioAIAgent implements Agent
{

	public MyAgent()
	{
		super("MyAgent");
		reset();
	}

	// Does (row, col) contain an enemy?   
	public boolean hasEnemy(int row, int col) {
		return enemies[row][col] != 0;
	}

	// Is (row, col) empty?   
	public boolean isEmpty(int row, int col) {
		return (levelScene[row][col] == 0);
	}


	// Display Mario's view of the world
	public void printObservation() {
		System.out.println("**********OBSERVATIONS**************");
		for (int i = 0; i < mergedObservation.length; i++) {
			for (int j = 0; j < mergedObservation[0].length; j++) {
				if (i == mergedObservation.length / 2 && j == mergedObservation.length / 2) {
					System.out.print("M ");
				}
				else if (hasEnemy(i, j)) {
					System.out.print("E ");
				}
				else if (!isEmpty(i, j)) {
					System.out.print("B");
				}
				else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
		System.out.println("************************");
	}

	public void halt()
    {
        action[Mario.KEY_SPEED] = false;
        action[Mario.KEY_JUMP] = false;
        action[Mario.KEY_RIGHT] = false;
        action[Mario.KEY_LEFT] = false;
    }


	public boolean fall()
	{
		return (isEmpty(10, 9) || isEmpty(10,10));
	}

	public boolean enemy()
	{
		
       
		return hasEnemy(9,10) || hasEnemy(9,11) || hasEnemy(9,12) || hasEnemy(9,15);
	}

	//here is where work is done
	public boolean[] getAction()
	{
		//change actions here


        if(!enemy() && isEmpty(9, 9))
        {
            //go right with speed if clear
            action[Mario.KEY_SPEED] = true;
            action[Mario.KEY_RIGHT] = true;
        }

        
        if(hasEnemy(8,9) || hasEnemy(7,9))
        {
            //dont jump if enemy is above, stop for a tic and then run
            halt();
            action[Mario.KEY_RIGHT] = true;
        }

        if(hasEnemy(9,10) || !isEmpty(9,10))
        {
            //if enemy/barrier right of mario, jump first then speed/fireball
            action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
            action[Mario.KEY_RIGHT] = true;
        }

   
        printObservation();
		return action;
	}



	// Do the processing necessary to make decisions in getAction
	public void integrateObservation(Environment environment)
	{
		super.integrateObservation(environment);
    	levelScene = environment.getLevelSceneObservationZ(2);
	}

	// Clear out old actions by creating a new action array
	public void reset()
	{
		action = new boolean[Environment.numberOfKeys];
	}
}

