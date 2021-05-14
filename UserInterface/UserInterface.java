package UserInterface;

import java.util.List;
import CoreGame.Card;

public interface UserInterface {
	public int GetNumPlayers();

	public void ShowSingleState(int Tavern, int Discard, int Jokers, int Castle, Card Foe, int Damage, int Health, List<Card> Active);
	public void ShowMultiState(int Tavern, int Discard, int Castle, Card Foe, int Damage, int Health, List<Card> Active,
			List<List<Card>> hands, int curPlayer);
	public void ShowHand(List<Card> hand);

	public void Clear();
	public void RotatePlayer(int player);
	
	public default void TurnCycle(int Tavern, int Discard, int Jokers, int Castle, Card Foe, int Damage, int Health, List<Card> Active,
			List<Card> hand){
		Clear();
		ShowSingleState(Tavern, Discard, Jokers,Castle, Foe, Damage, Health, Active);
		ShowHand(hand);
	}
	public default void TurnCycle(int Tavern, int Discard, int Castle, Card Foe, int Damage, int Health, List<Card> Active,
			List<List<Card>> hands, int curPlayer){
		Clear();
		ShowMultiState(Tavern, Discard, Castle, Foe, Damage, Health, Active, hands, curPlayer);
		ShowHand(hands.get(curPlayer));
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
