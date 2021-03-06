package edu.up.cs301.risk;

import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * A GUI of a risk-player. The GUI displays the current number of units on a
 * specific territory and send moves to the game.
 * 
 * 
 * 
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @author logan Mealy, Garrett Becker, Lucas Burns, Shamus Murray, John Will
 *         Bryant
 * @version April 2015
 */
public class RiskHumanPlayer extends GameHumanPlayer implements RiskPlayer,
		OnClickListener {

	/* instance variables */

	// gui variables
	private Button attack;
	private Button move;
	private Button place;
	private Button surrender;
	private Button endTurn;
	private Button deselect;
	private TextView currentText;
	private TextView defendText;
	private Button[] countries = new Button[17];
	private TextView[] countryCount = new TextView[17];

	// stores ids for country and amount of troops in them
	private int[] countryIds = new int[17];
	private int[] countryCountIds = new int[17];

	// holds button pressed for use outside onClick method
	private View currentButton;

	// holds R.id for country selected
	private int countrySelectedID;
	private int countrySelectedID2;

	// holds index from 1-17 of country selected
	private int countrySelectedIndexID = 0;
	private int countrySelectedIndexID2 = 0;

	// holds name of country selected
	private String countrySelectedName;
	private String countrySelectedName2;

	// holds id of drawable needed to switch color
	private int countryDrawableIDSelect = 0;
	private int countryDrawableIDUnSelect = 0;

	// booleans to check for disabling buttons and sending actions
	private boolean countryPressed;
	private boolean country2Pressed;
	private boolean country2CanBeSelected;
	private boolean attackBtnEnabled;
	private boolean moveBtnEnabled;
	private boolean placeBtnEnabled;
	private boolean endTurnBtnEnabled = true;
	private boolean deselectBtnEnabled;
	private boolean isPlaceActionReady = false;
	private boolean isAttackActionReady = false;
	private boolean isMoveActionReady = false;
	private boolean isEndTurnActionReady = false;
	private boolean isSurrenderActionReady = false;
	private boolean isItPlayerTurn = false;

	// holds current action to send
	private GameAction currentAction;


	// the most recent game state, as given to us by the RiskLocalGame
	private RiskState state;

	// the android activity that we are running
	private GameMainActivity myActivity;

	/**
	 * constructor
	 * 
	 * @param name
	 *            of player
	 */
	public RiskHumanPlayer(String name) {
		super(name);
	}

	/**
	 * Returns the GUI's top view object
	 * 
	 * @return the top object in the GUI's view heirarchy
	 */
	public View getTopView() {
		return myActivity.findViewById(R.id.top_gui_layout);
	}

	/**
	 * sets the troop value in the text view depending on the country selected
	 */
	protected void updateDisplay() {

		// update textview based on who's turn it is
		// change to actual names later
		if (state.getPlayerTurn() == RiskState.PLAYER_ONE) {
			currentText.setText("Attacker: " + this.allPlayerNames[0]);
			currentText.setTextColor(Color.YELLOW);
			currentText.setShadowLayer(1.5f, 2.0f, 2.0f, Color.DKGRAY);
			defendText.setText("Defender: " + this.allPlayerNames[1]);
			defendText.setTextColor(Color.GREEN);
			defendText.setShadowLayer(1.5f, 2.0f, 2.0f, Color.DKGRAY);
		} else {
			currentText.setText("Attacker: " + this.allPlayerNames[1]);
			currentText.setTextColor(Color.YELLOW);
			currentText.setShadowLayer(1.5f, 2.0f, 2.0f, Color.DKGRAY);
			defendText.setText("Defender: " + this.allPlayerNames[0]);
			defendText.setTextColor(Color.GREEN);
			defendText.setShadowLayer(1.5f, 2.0f, 2.0f, Color.DKGRAY);
		}

		// check if player1 or player2 has won
		if (state.winnerCheck() == RiskState.PLAYER_ONE) {
			createTextAlertBox("Game Over. " + this.allPlayerNames[0] + " wins!");
		}
		if (state.winnerCheck() == RiskState.PLAYER_TWO) {
			createTextAlertBox("Game Over. "  + this.allPlayerNames[1] + " wins!");
		}
		// check if player1 or player2 has surrendered
		if (state.getSurrenderPlayerTrue(RiskState.PLAYER_ONE) == true) {
			createTextAlertBox("Game Over. " + this.allPlayerNames[0] + " surrendered. " + this.allPlayerNames[1] + " wins!");
		}
		if (state.getSurrenderPlayerTrue(RiskState.PLAYER_TWO) == true) {
			createTextAlertBox("Game Over. " + this.allPlayerNames[1] + " surrendered. " + this.allPlayerNames[0] + " wins!");
		}

		// if player turn, allow actions
		if (state.getPlayerTurn() == this.playerNum) {
			isItPlayerTurn = true;
			endTurnBtnEnabled = true;
			// disable everything otherwise
		} else {
			countryPressed = false;
			country2Pressed = false;
			country2CanBeSelected = false;
			attackBtnEnabled = false;
			moveBtnEnabled = false;
			placeBtnEnabled = false;
			endTurnBtnEnabled = false;
			deselectBtnEnabled = false;
			isPlaceActionReady = false;
			isAttackActionReady = false;
			isMoveActionReady = false;
			isEndTurnActionReady = false;
			this.state.setHaveTroopBeenPlacedToFalse(this.playerNum);
		}

		// do checks for if troops have been placed and enable and disable
		// buttons accordingly
		if (this.state.getHaveTroopBeenPlaced(this.playerNum) == false
				&& countryPressed == true) {

			attackBtnEnabled = false;
			moveBtnEnabled = false;
			placeBtnEnabled = true;

			attack.setBackgroundColor(attack.getContext().getResources()
					.getColor(R.color.Red));
			move.setBackgroundColor(move.getContext().getResources()
					.getColor(R.color.Red));
			place.setBackground(place.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));

		}
		if (this.state.getHaveTroopBeenPlaced(this.playerNum) == true
				&& countryPressed == true) {

			attackBtnEnabled = true;
			moveBtnEnabled = true;

			attack.setBackground(attack.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));
			move.setBackground(move.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));
		}
		if (this.state.getHaveTroopBeenPlaced(this.playerNum) == true
				&& countryPressed == false) {

			attackBtnEnabled = false;
			moveBtnEnabled = false;

			attack.setBackgroundColor(attack.getContext().getResources()
					.getColor(R.color.Red));
			move.setBackgroundColor(move.getContext().getResources()
					.getColor(R.color.Red));
		}

		// update text on countries to match troops and player
		// ownership on gui
		int i;
		for (i = 1; i < 17; i++) {
			if (this.state.playerInControl(i) == RiskState.PLAYER_ONE) {
				countryCount[i].setTextColor(Color.YELLOW);
				countryCount[i].setShadowLayer(1.5f, 4.0f, 4.0f, Color.DKGRAY);
				String temp = Integer.toString(this.state
						.getPlayerTroopsInCountry(RiskState.PLAYER_ONE, i));
				countryCount[i].setText(temp);
			} else {
				countryCount[i].setTextColor(Color.GREEN);
				countryCount[i].setShadowLayer(1.5f, 4.0f, 4.0f, Color.DKGRAY);
				String temp = Integer.toString(this.state
						.getPlayerTroopsInCountry(RiskState.PLAYER_TWO, i));
				countryCount[i].setText(temp);
			}
		}

		// check if booleans are enabled which enables buttons
		if (this.state.getHaveTroopBeenPlaced(this.playerNum) == true) {
			place.setBackgroundColor(place.getContext().getResources()
					.getColor(R.color.Red));
			placeBtnEnabled = false;
		}

		if (placeBtnEnabled == false) {
			place.setBackgroundColor(place.getContext().getResources()
					.getColor(R.color.Red));
		} else {
			place.setBackground(place.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));
		}
		if (moveBtnEnabled == false) {
			move.setBackgroundColor(move.getContext().getResources()
					.getColor(R.color.Red));
		} else {
			move.setBackground(move.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));
		}
		if (attackBtnEnabled == false) {
			attack.setBackgroundColor(attack.getContext().getResources()
					.getColor(R.color.Red));
		} else {
			attack.setBackground(attack.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));
		}
		if (endTurnBtnEnabled == false) {
			endTurn.setBackgroundColor(endTurn.getContext().getResources()
					.getColor(R.color.Red));
		} else {
			endTurn.setBackground(endTurn.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));
		}
		if (deselectBtnEnabled == false) {
			deselect.setBackgroundColor(deselect.getContext().getResources()
					.getColor(R.color.Red));
			deselect.setText("Country 1 Not Selected");
		} else {
			deselect.setBackground(place.getContext().getResources()
					.getDrawable(R.drawable.custombuttonshapewhite));
		}

	}

	/**
	 * this method gets called when the user clicks on an attack, defend, place
	 * troops, or surrender button. It creates a new RiskMoveAction to return to
	 * the parent activity.
	 * 
	 * @param button
	 *            the button that was clicked
	 */
	public void onClick(View button) {

		currentButton = button;

		// if we are not yet connected to a game, ignore
		if (game == null)
			return;

		// check if you hit disabled button and flash if so
		if (button.getId() == R.id.Attack && attackBtnEnabled == false) {
			flash(Color.RED, 200);
		}
		if (button.getId() == R.id.Move && moveBtnEnabled == false) {
			flash(Color.RED, 200);
		}
		if (button.getId() == R.id.Place && placeBtnEnabled == false) {
			flash(Color.RED, 200);
		}
		if (button.getId() == R.id.Deselect && deselectBtnEnabled == false) {
			flash(Color.RED, 200);
		}
		if (button.getId() == R.id.EndTurn && endTurnBtnEnabled == false) {
			flash(Color.RED, 200);
		}

		// check if hit country and which one it was
		int y;
		for (y = 1; y < 17; y++) {
			if (button.getId() == countryIds[y]) {
				// country 1 already selected, so you are selecting country2.
				if (country2CanBeSelected == true) {
					country2Pressed = true;

					countrySelectedID2 = button.getId();
					countrySelectedIndexID2 = y;

					// set 2nd country name based off id of country
					switch (button.getId()) {
					case R.id.russiaButton:
						countrySelectedName2 = "Russia";
						break;
					case R.id.icelandButton:
						countrySelectedName2 = "Iceland";
						break;
					case R.id.italyButton:
						countrySelectedName2 = "Italy";
						break;
					case R.id.swedenButton:
						countrySelectedName2 = "Sweden";
						break;
					case R.id.atlantisButton:
						countrySelectedName2 = "Atlantis";
						break;
					case R.id.hogwartsButton:
						countrySelectedName2 = "Hogwarts";
						break;
					case R.id.narniaButton:
						countrySelectedName2 = "Narnia";
						break;
					case R.id.belgiumButton:
						countrySelectedName2 = "Belgium";
						break;
					case R.id.mordorButton:
						countrySelectedName2 = "Mordor";
						break;
					case R.id.gondorButton:
						countrySelectedName2 = "Gondor";
						break;
					case R.id.shireButton:
						countrySelectedName2 = "The Shire";
						break;
					case R.id.rohanButton:
						countrySelectedName2 = "Rohan";
						break;
					case R.id.bulgariaButton:
						countrySelectedName2 = "Bulgaria";
						break;
					case R.id.israelButton:
						countrySelectedName2 = "Israel";
						break;
					case R.id.switzerlandButton:
						countrySelectedName2 = "Switzerland";
						break;
					case R.id.ukraineButton:
						countrySelectedName2 = "Ukraine";
						break;
					}

					// if attack was previously clicked, send attack action
					if (isAttackActionReady) {

						GameAction attackAction = new RiskAttackAction(this,
								countrySelectedIndexID, countrySelectedIndexID2);
						if (!state.isTerritoryAdj(countrySelectedIndexID,
								countrySelectedIndexID2)) {
							createTextAlertBox("Error! Countries not adjacent!");
						} else if (state
								.playerInControl(countrySelectedIndexID) == state
								.playerInControl(countrySelectedIndexID2)) {
							createTextAlertBox("Error! Cannot attack your allies!");
						} else {
							createActionAlertBox("Attack "
									+ countrySelectedName2 + " from "
									+ countrySelectedName, attackAction);
						}
					}
					if (isMoveActionReady) {
						GameAction moveAction = new RiskMoveTroopAction(this,
								this.playerNum, countrySelectedIndexID,
								countrySelectedIndexID2);
						if (!state.isTerritoryAdj(countrySelectedIndexID,
								countrySelectedIndexID2)) {
							createTextAlertBox("Error! Countries not adjacent!");
						} else if (state
								.playerInControl(countrySelectedIndexID) != state
								.playerInControl(countrySelectedIndexID2)) {
							createTextAlertBox("Error! Cannot move units to country you don't own!");

						} else {
							createActionAlertBox("Move troops to " + countrySelectedName2
									+ " from " + countrySelectedName,
									moveAction);
						}
					}

				} else {
					// first time selecting a country, enable deselect ability
					countryPressed = true;
					deselectBtnEnabled = true;
					
					if (countryDrawableIDUnSelect != 0 && countrySelectedIndexID != 0) {
						countries[countrySelectedIndexID].setBackground(countries[countrySelectedIndexID].getContext().getResources()
								.getDrawable(countryDrawableIDUnSelect));
					}

					countrySelectedID = button.getId();
					countrySelectedIndexID = y;

					// set 1st country name based off id of country
					switch (button.getId()) {
					case R.id.russiaButton:
						countrySelectedName = "Russia";
						countryDrawableIDSelect = R.drawable.russiaselec;
						countryDrawableIDUnSelect = R.drawable.russiaun;
						break;
					case R.id.icelandButton:
						countrySelectedName = "Iceland";
						countryDrawableIDSelect = R.drawable.icelandselec;
						countryDrawableIDUnSelect = R.drawable.icelandun;
						break;
					case R.id.italyButton:
						countrySelectedName = "Italy";
						countryDrawableIDSelect = R.drawable.italyselec;
						countryDrawableIDUnSelect = R.drawable.italyun;
						break;
					case R.id.swedenButton:
						countrySelectedName = "Sweden";
						countryDrawableIDSelect = R.drawable.swedenselec;
						countryDrawableIDUnSelect = R.drawable.swedenun;
						break;
					case R.id.atlantisButton:
						countrySelectedName = "Atlantis";
						countryDrawableIDSelect = R.drawable.atlantisselec;
						countryDrawableIDUnSelect = R.drawable.atlantisun;
						break;
					case R.id.hogwartsButton:
						countrySelectedName = "Hogwarts";
						countryDrawableIDSelect = R.drawable.hogwartsselec;
						countryDrawableIDUnSelect = R.drawable.hogwartsun;
						break;
					case R.id.narniaButton:
						countrySelectedName = "Narnia";
						countryDrawableIDSelect = R.drawable.narniaselec;
						countryDrawableIDUnSelect = R.drawable.narniaun;
						break;
					case R.id.belgiumButton:
						countrySelectedName = "Belgium";
						countryDrawableIDSelect = R.drawable.belgiumselec;
						countryDrawableIDUnSelect = R.drawable.belgiumun;
						break;
					case R.id.mordorButton:
						countrySelectedName = "Mordor";
						countryDrawableIDSelect = R.drawable.mordorselec;
						countryDrawableIDUnSelect = R.drawable.mordorun;
						break;
					case R.id.gondorButton:
						countrySelectedName = "Gondor";
						countryDrawableIDSelect = R.drawable.gondor1selec;
						countryDrawableIDUnSelect = R.drawable.gondor1un;
						break;
					case R.id.shireButton:
						countrySelectedName = "The Shire";
						countryDrawableIDSelect = R.drawable.shireselec;
						countryDrawableIDUnSelect = R.drawable.shireun;
						break;
					case R.id.rohanButton:
						countrySelectedName = "Rohan";
						countryDrawableIDSelect = R.drawable.rohanselec;
						countryDrawableIDUnSelect = R.drawable.rohanun;
						break;
					case R.id.bulgariaButton:
						countrySelectedName = "Bulgaria";
						countryDrawableIDSelect = R.drawable.bulgariaselec;
						countryDrawableIDUnSelect = R.drawable.bulgariaun;
						break;
					case R.id.israelButton:
						countrySelectedName = "Israel";
						countryDrawableIDSelect = R.drawable.israelselec;
						countryDrawableIDUnSelect = R.drawable.israelun;
						break;
					case R.id.switzerlandButton:
						countrySelectedName = "Switzerland";
						countryDrawableIDSelect = R.drawable.switzerlandselec;
						countryDrawableIDUnSelect = R.drawable.switzerlandun;
						break;
					case R.id.ukraineButton:
						countrySelectedName = "Ukraine";
						countryDrawableIDSelect = R.drawable.ukraineselec;
						countryDrawableIDUnSelect = R.drawable.ukraineun;
						break;
					}

					deselect.setBackground(deselect.getContext().getResources()
							.getDrawable(R.drawable.custombuttonshapewhite));
					deselect.setText("Deselect: " + countrySelectedName);

					countries[y].setBackground(button.getContext().getResources()
							.getDrawable(countryDrawableIDSelect));
				}
			}

			updateDisplay();
		}

		// check if deselect country was hit
		if (button.getId() == R.id.Deselect && deselectBtnEnabled == true) {
			// country is no longer pressed
			countryPressed = false;
			deselectBtnEnabled = false;
			country2CanBeSelected = false;
			
			//reset these when other button clicked
			isAttackActionReady = false;
			isMoveActionReady = false;

			deselect.setBackgroundColor(deselect.getContext().getResources()
					.getColor(R.color.Red));
			deselect.setText("Country 1 Not Selected");

			countries[countrySelectedIndexID].setBackground(countries[countrySelectedIndexID].getContext().getResources()
					.getDrawable(countryDrawableIDUnSelect));

			updateDisplay();
		}

		// if attack is pressed
		if (button.getId() == R.id.Attack && attackBtnEnabled == true) {

			//move disabled if enabled
			isMoveActionReady = false;
			
			int getcurrentPlayer = state.getPlayerTurn();

			// Make sure no invalid moves in first if statement then give the go
			// to attack using AttackActionReady boolean
			if (state.playerInControl(countrySelectedIndexID) == state
					.getPlayerTurn()
					&& state.getPlayerTroopsInCountry(getcurrentPlayer,
							countrySelectedIndexID) != 1) {
				//createTextAlertBox("Select 2nd adjacent enemy country to attack");
				
				Toast.makeText(myActivity, "Select 2nd adjacent enemy country to attack", 
						   Toast.LENGTH_LONG).show();
				
				isAttackActionReady = true;
				country2CanBeSelected = true;
			}
			// give error messages if invalid moves
			if (state.getPlayerTroopsInCountry(getcurrentPlayer,
					countrySelectedIndexID) == 1) {
				createTextAlertBox("Not enough troops for an attack");
			} else if (state.playerInControl(countrySelectedIndexID) != state
					.getPlayerTurn()) {
				createTextAlertBox("Not your country");
			}

			updateDisplay();
		}

		// if move is pressed make sure what message to send to the player
		// depending
		// if the move is legal or not (country owned & has enough players in
		// it)
		if (button.getId() == R.id.Move && moveBtnEnabled == true) {
			
			//attack disabled if enabled
			isAttackActionReady = false;
			
			if (state.playerInControl(countrySelectedIndexID) == this.playerNum
					&& state.getPlayerTroopsInCountry(this.playerNum,
							countrySelectedIndexID) > 1) {
				country2CanBeSelected = true;
				isMoveActionReady = true;
				//createTextAlertBox("Select 2nd adjacent friendly country to move to");
				
				Toast.makeText(myActivity, "Select 2nd adjacent friendly country to move to", 
						   Toast.LENGTH_LONG).show();
				
			} else if (state.playerInControl(countrySelectedIndexID) != this.playerNum) {
				createTextAlertBox("Not your country");
			} else if (state.getPlayerTroopsInCountry(this.playerNum,
					countrySelectedIndexID) == 1) {
				createTextAlertBox("Not enough troops to move!");
			}

			updateDisplay();
		}

		// if endTurn is pressed
		if (button.getId() == R.id.EndTurn && endTurnBtnEnabled == true) {
			// Set the action to be ready to send
			isEndTurnActionReady = true;

			updateDisplay();
			// Send information to the End turn constructor
			RiskEndTurnAction endAction = new RiskEndTurnAction(this,
					this.playerNum);
			createActionAlertBox("Are you sure you want to end your turn?",
					endAction);
		}

		// if surrender is pressed
		if (button.getId() == R.id.Surrender) {
			
			isSurrenderActionReady = true;

			updateDisplay();
			// Sends information to the surrender constructor
			RiskSurrenderAction surrenderAction = new RiskSurrenderAction(this,
					RiskState.PLAYER_ONE);
			createActionAlertBox("Are you sure you want to surrender?",
					surrenderAction);
		}

		// if place is pressed
		if (button.getId() == R.id.Place && placeBtnEnabled == true) {

			isPlaceActionReady = true;
			
			updateDisplay();
			// check to make sure it's your country and your turn to place
			if (state.playerInControl(countrySelectedIndexID) == state
					.getPlayerTurn()) {
				RiskPlaceTroopAction action = new RiskPlaceTroopAction(this,
						countrySelectedIndexID, this.playerNum);
				createActionAlertBox("Place troops in " + countrySelectedName
						+ "?", action);
			} else {
				createTextAlertBox("Country not in your control. Cannot Place Troops.");
			}
		}

	}// onClick

	/**
	 * callback method when we get a message (e.g., from the game)
	 * 
	 * @param info
	 *            the message
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		// ignore the message if it's not a RiskState message
		if (!(info instanceof RiskState))
			return;

		this.state = (RiskState) info;
		updateDisplay();
	}

	/**
	 * creates dialog box confirming action
	 * 
	 * @param String
	 *            question to be asked
	 * @param Game
	 *            Action action to be passed if action confirmed
	 * @return returns if yes was hit or not
	 */
	private void createActionAlertBox(String question, GameAction action) {
		AlertDialog.Builder alert = new AlertDialog.Builder(myActivity);
		alert.setCancelable(false);
		alert.setTitle(question);
		currentAction = action;

		// positive button of alert box
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				// send the action to local game
				game.sendAction(currentAction);

				if(countrySelectedIndexID != 0){
				countries[countrySelectedIndexID].setBackground(countries[countrySelectedIndexID].getContext().getResources()
						.getDrawable(countryDrawableIDUnSelect));
				}

				// disable other buttons depending on what's pressed
				if (isPlaceActionReady == true) {
					deselectBtnEnabled = false;
					placeBtnEnabled = false;
					countryPressed = false;
					country2CanBeSelected = false;

					// done with action
					isPlaceActionReady = false;
				}
				if (isAttackActionReady == true) {
					deselectBtnEnabled = false;
					countryPressed = false;
					country2CanBeSelected = false;
					attackBtnEnabled = false;
					moveBtnEnabled = false;

					// done with action
					isAttackActionReady = false;
				}
				if (isMoveActionReady == true) {
					deselectBtnEnabled = false;
					countryPressed = false;
					country2CanBeSelected = false;
					moveBtnEnabled = false;
					attackBtnEnabled = false;

					// done with action
					isMoveActionReady = false;
				}

				if (isEndTurnActionReady == true) {
					deselectBtnEnabled = false;
					countryPressed = false;
					country2CanBeSelected = false;
					moveBtnEnabled = false;
					attackBtnEnabled = false;
					endTurnBtnEnabled = false;
					placeBtnEnabled = false;

					// done with action
					isEndTurnActionReady = false;
				}
				updateDisplay();
			}
		});

		// negative button of alert box
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();

	}

	private void createTextAlertBox(String text) {
		AlertDialog.Builder alert = new AlertDialog.Builder(myActivity);
		alert.setTitle(text);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//if surrender was hit or game was won or lost, exit game upon hitting okay
				if (state.getSurrenderPlayerTrue(RiskState.PLAYER_ONE) == true) {
					System.exit(0);
				}
				if (state.getSurrenderPlayerTrue(RiskState.PLAYER_TWO) == true) {
					System.exit(0);
				}
				if(state.winnerCheck() == RiskState.PLAYER_ONE){
					System.exit(0);
				}
				if(state.winnerCheck() == RiskState.PLAYER_TWO){
					System.exit(0);
				}
			}
		});

		alert.show();

	}

	/**
	 * callback method--our game has been chosen/rechosen to be the GUI, called
	 * from the GUI thread
	 * 
	 * @param activity
	 *            the activity under which we are running
	 */
	public void setAsGui(GameMainActivity activity) {

		// remember the activity
		myActivity = activity;

		// Load the layout resource for our GUI
		activity.setContentView(R.layout.activity_main);

		// fill button id array with proper button xml ids
		countryIds[0] = 0;
		countryIds[1] = R.id.russiaButton;
		countryIds[2] = R.id.icelandButton;
		countryIds[3] = R.id.italyButton;
		countryIds[4] = R.id.swedenButton;
		countryIds[5] = R.id.atlantisButton;
		countryIds[6] = R.id.hogwartsButton;
		countryIds[7] = R.id.narniaButton;
		countryIds[8] = R.id.belgiumButton;
		countryIds[9] = R.id.mordorButton;
		countryIds[10] = R.id.gondorButton;
		countryIds[11] = R.id.shireButton;
		countryIds[12] = R.id.rohanButton;
		countryIds[13] = R.id.bulgariaButton;
		countryIds[14] = R.id.israelButton;
		countryIds[15] = R.id.switzerlandButton;
		countryIds[16] = R.id.ukraineButton;

		// fill textview id array with proper textview xml ids
		countryIds[0] = 0;
		countryCountIds[1] = R.id.countryCount1;
		countryCountIds[2] = R.id.countryCount2;
		countryCountIds[3] = R.id.countryCount3;
		countryCountIds[4] = R.id.countryCount4;
		countryCountIds[5] = R.id.countryCount5;
		countryCountIds[6] = R.id.countryCount6;
		countryCountIds[7] = R.id.countryCount7;
		countryCountIds[8] = R.id.countryCount8;
		countryCountIds[9] = R.id.countryCount9;
		countryCountIds[10] = R.id.countryCount10;
		countryCountIds[11] = R.id.countryCount11;
		countryCountIds[12] = R.id.countryCount12;
		countryCountIds[13] = R.id.countryCount13;
		countryCountIds[14] = R.id.countryCount14;
		countryCountIds[15] = R.id.countryCount15;
		countryCountIds[16] = R.id.countryCount16;

		countries[0] = null;

		// set id's for buttons and textviews
		int i;
		for (i = 1; i < 17; i++) {

			countries[i] = (Button) myActivity.findViewById(countryIds[i]);
			countries[i].setOnClickListener(this);

			countryCount[i] = (TextView) myActivity
					.findViewById(countryCountIds[i]);
		}

		// set id's for other buttons
		attack = (Button) myActivity.findViewById(R.id.Attack);
		attack.setOnClickListener(this);
		move = (Button) myActivity.findViewById(R.id.Move);
		move.setOnClickListener(this);
		place = (Button) myActivity.findViewById(R.id.Place);
		place.setOnClickListener(this);
		surrender = (Button) myActivity.findViewById(R.id.Surrender);
		surrender.setOnClickListener(this);
		endTurn = (Button) myActivity.findViewById(R.id.EndTurn);
		endTurn.setOnClickListener(this);
		deselect = (Button) myActivity.findViewById(R.id.Deselect);
		deselect.setOnClickListener(this);

		// set id's for other textviews
		currentText = (TextView) myActivity.findViewById(R.id.playerTurn);
		defendText = (TextView) myActivity.findViewById(R.id.playerNotTurn);

		// set default button and text values
		attack.setBackgroundColor(attack.getContext().getResources()
				.getColor(R.color.Red));
		move.setBackgroundColor(move.getContext().getResources()
				.getColor(R.color.Red));
		place.setBackgroundColor(place.getContext().getResources()
				.getColor(R.color.Red));
		deselect.setBackgroundColor(deselect.getContext().getResources()
				.getColor(R.color.Red));
		deselect.setText("Country 1 Not Selected");

		// if we have a game state, "simulate" that we have just received
		// the state from the game so that the GUI values are updated
		if (state != null) {
			receiveInfo(state);
		}
	}

}// class RiskHumanPlayer