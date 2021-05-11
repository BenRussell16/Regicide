package UserInterface;

import java.util.List;
import CoreGame.Card;

public interface UserInterface {
	public int GetNumPlayers();
	
	public void ShowState();
	public void ShowHand(List<Card> hand);

	public void Clear();
	public void RotatePlayer(int player);
	public default void TurnCycle(List<Card> hand){
		Clear();
		ShowState();
		ShowHand(hand);
	}

	/**
	 * A method to prompt the player to take their turn.
	 * Method ensures the selection is valid.
	 * @param hand - A copy of the players hand (includes yield if an option and any jokers if single player).
	 * @return - The cards being played.
	 */
	public List<Card> TakeTurn(List<Card> hand);
	/**
	 * A method to prompt the user for damage discard.
	 * @param hand - A copy of the players hand.
	 * @param damage - The damage they need to discard.
	 * @return - The cards being discarded. Null if it's a loss.
	 */
	public List<Card> TakeDamage(List<Card> hand, int damage);

	public boolean EndGame(boolean Victory);
	public boolean SingleEndGame(boolean Victory, int Jokers);
}
