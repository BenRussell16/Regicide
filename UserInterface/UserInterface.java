package UserInterface;

import java.util.List;
import CoreGame.Card;

public interface UserInterface {
	public int GetNumPlayers();
	
	public void ShowState();
	public void ShowHand(List<Card> hand);

	public void Clear();
	public default void TurnCycle(List<Card> hand){
		Clear();
		ShowState();
		ShowHand(hand);
	}
}
