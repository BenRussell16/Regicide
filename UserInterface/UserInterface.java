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

	public List<Card> TakeTurn(List<Card> hand);
	public List<Card> TakeDamage(List<Card> hand, int damage);

	public boolean EndGame(boolean Victory);
	public boolean SingleEndGame(boolean Victory, int Jokers);
}
