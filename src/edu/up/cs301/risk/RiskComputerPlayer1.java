package edu.up.cs301.risk;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;
    
/**
 * A computer-version of a risk-player.
 * 
 * @authors Shamus Murray, Garrett Becker, Logan Mealy, Lucas Burns, John Will Bryan 
 * 
 * @version March 2015 
 * 
 */
public class RiskComputerPlayer1 extends GameComputerPlayer implements RiskPlayer, Tickable {
	
    /**
     * Constructor for objects of class CounterComputerPlayer1
     * 
     * @param name
     * 		the player's name
     */
    public RiskComputerPlayer1(String name) {
        // invoke superclass constructor
        super(name);
        
      
    }
    
    /**
     * callback method--game's state has changed
     * 
     * @param info
     * 		the information (presumably containing the game's state)
     */
	@Override
	protected void receiveInfo(GameInfo info) {
		// Do nothing, as we ignore all state in deciding our next move. It
		// depends totally on the timer and random numbers.
	}
	
	/**
	 * Controls the actions that the dumb AI makes
	 */
	protected void compAction1() {
		
		
		// send the move-action to the game
		game.sendAction(new RiskMoveTroopAction(this,false));
	}
}
