package edu.up.cs301.risk;

import java.util.Random;

import android.util.Log;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;

/**
 * A computer-version of a risk-player.
 * Dumb AI
 * 
 * @authors Shamus Murray, Garrett Becker, Logan Mealy, Lucas Burns, John Will
 *          Bryan
 * 
 * @version April 2015
 * 
 */
public class RiskComputerPlayer1 extends GameComputerPlayer implements
		RiskPlayer, Tickable {
	
	//instance variables
	
	//boolean determing if it is the player's turn
	private boolean isItPlayerTurn;
	
	// the android activity that we are running
	private GameMainActivity myActivity;

	// the most recent game state, as given to us by the RiskLocalGame
	private RiskState state;

	//the current Game action
	private GameAction currentAction;

	private double actionRandomizer;

	//index Ids of the 2 countries selected
	private int countrySelectedIndexId;
	private int countrySelectedIndexId2;

	//boolean determining if the troops have been placed
	private boolean troopsPlaced;

	//new Random object
	private Random rand = new Random();

	/**
	 * Constructor for objects of class CounterComputerPlayer1
	 * 
	 * @param name
	 *            the player's name
	 */
	public RiskComputerPlayer1(String name) {
		// invoke superclass constructor
		super(name);
		troopsPlaced = false;
	}

	/**
	 * updates display based on whose turn it is
	 */
	protected void updateDisplay() {

		Log.i("player turn update", Integer.toString(state.getPlayerTurn()));
		
		//determines if it is the computers turn
		if (state.getPlayerTurn() == RiskState.PLAYER_TWO) {
			// starts the timer at the beginning of turn, ticking 20 times per second 
			getTimer().setInterval(500);
			getTimer().start();
		}
	}

	/**
	 * callback method--game's state has changed
	 * 
	 * @param info
	 *            the information (presumably containing the game's state)
	 */
	@Override
	protected void receiveInfo(GameInfo info) {
		// ignore the message if it's not a RiskState message
		if (!(info instanceof RiskState))
			return;

		this.state = (RiskState) info;
		updateDisplay();
	}

	/**
	 * callback method: the timer ticked, makes a random move
	 */
	protected void timerTicked() {
		if (state.getPlayerTurn() == RiskState.PLAYER_TWO) {
			// place troops first, if it has not already,
			//before performing random actions
			if (this.state.getHaveTroopBeenPlaced(RiskState.PLAYER_TWO) == false) {
			int i;
			// cycles through countries, selecting the first one it owns
			for (i = 1; i < 17; i++) {
				if (this.state.playerInControl(i) == RiskState.PLAYER_TWO
						&& this.state.getPlayerTroopsInCountry(RiskState.PLAYER_TWO, i) >= 1) {
					countrySelectedIndexId = i;	
					break;
				}
				}
			//sets place troops game action and sends to  the state
			RiskPlaceTroopAction placeAction = new RiskPlaceTroopAction(
					this, i, RiskState.PLAYER_TWO);
			game.sendAction(placeAction);
			}
			//int used to randomize the execution of attack, move troops, end turn, and surrender actions
			actionRandomizer = Math.random();

			//attack action, 40% chance of executing
			if (actionRandomizer <= 0.4) {
				int i;
				// cycles through countries, selection the first country it owns with at least 2 troops
				for (i = 1; i < 17; i++) {
					if (this.state.playerInControl(i) == RiskState.PLAYER_TWO
							&& this.state.getPlayerTroopsInCountry(RiskState.PLAYER_TWO, i) >= 2) {
						//assigns this country at i to its selected country
						countrySelectedIndexId = i;
						int y;
						// cycles through countries, selecting the first one it
						// doesn't own
						for (y = 1; y < 17; y++) {
							//checks to see if the enemy country is adjacent to its own selected country
							if (this.state.playerInControl(y) == RiskState.PLAYER_ONE
									&& this.state.getPlayerTroopsInCountry(RiskState.PLAYER_ONE,
											y) >= 1
									&& this.state.isTerritoryAdj(i, y) == true) {
								//assigns this country to 2nd selected, the attack destination
								countrySelectedIndexId2 = y;
							    //now that it has the two countries needed for attack action assigned, 
								//breaks out of the both loops
								break;
							}
						}
						break;
					}
				}
				// send the attack action to the game with these countries
				currentAction = new RiskAttackAction(this,
						countrySelectedIndexId, countrySelectedIndexId2);
			}
			
			//move troops action, 20% chance of executing
			if (actionRandomizer > 0.4 && actionRandomizer <= 0.6) {
				int i;
				// cycles through countries, selecting the first one it owns with more than two troops
				for (i = 1; i < 17; i++) {
					if (this.state.playerInControl(i) == RiskState.PLAYER_TWO
							&& this.state.getPlayerTroopsInCountry(RiskState.PLAYER_TWO, i) >= 2) {
						countrySelectedIndexId = i;
						int y;
						// cycles through countries, selecting the first country it owns that is adjacent
						// to the previously selected one
						for (y = 1; y < 17; y++) {
							if (this.state.playerInControl(y) == RiskState.PLAYER_TWO
									&& this.state.getPlayerTroopsInCountry(RiskState.PLAYER_TWO,
											y) >= 1
									&& this.state.isTerritoryAdj(i, y) == true) {
								countrySelectedIndexId2 = y;
								//breaks out of the loop now that the country to be moved from
								//and to be moved into have been selected
								break;
							}
						}
						break;
					}
				}
				//assigns its current game action to move action with the selected countries
				currentAction = new RiskMoveTroopAction(this, RiskState.PLAYER_TWO, countrySelectedIndexId, countrySelectedIndexId2);
			}
			
			//end turn action, 39.5% chance of executing
			if (actionRandomizer > 0.6 && actionRandomizer <= 0.995) {
				// assigns its current game action to end turn action
				currentAction = new RiskEndTurnAction(this, this.playerNum);
			}
			
			//surrender action, 0.5% chance of executing
			if (actionRandomizer > 0.995 && actionRandomizer <= 1.0) {
				//assigns its current game action to end turn action
				currentAction = new RiskSurrenderAction(this, this.playerNum);
			}
			
			
			if (currentAction instanceof RiskEndTurnAction || currentAction instanceof RiskSurrenderAction) {
				//in case of an end turn or surrender action, sends the action to the game
				//and then stops and resets the timer for next instance of use
				game.sendAction(currentAction);
				getTimer().stop();
				getTimer().reset();

				//sets boolean determining whether it is the computers turn or not to false
				isItPlayerTurn = false;
			} else {
				//otherwise send the current action normally as its turn
				//is not yet over
				game.sendAction(currentAction);
			}
		}
	}
	

}