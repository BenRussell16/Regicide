package UserInterface;

import java.util.List;
import CoreGame.Card;
import CoreGame.Game;

public class TextUI implements UserInterface{
	Game game;
	public TextUI(Game game) {this.game = game;}

	@Override
	public int GetNumPlayers() {
		Clear();
		// TODO Actually ask for user input.
		return 1;
	}

	@Override
	public void ShowState() {
		Clear();
		String DeckState = "Tavern deck: "+game.GetTavernDeck().size()+"\t\tDiscard pile: "+game.GetDiscard().size();
		if(game.GetJokers() != 0) {DeckState+="\t\tJokers: "+game.GetJokers();}
		System.out.println(DeckState);
		System.out.println(game.GetCastleDeck().size()+" foes remain.\t\t"
				+"Current foe: "+game.GetFoe().toString()+" (Damage: "+game.getDamage()+", Health: "+game.getHealth()+")");
		String ActiveCards = "";
		for(Card c:game.GetInPlay()) {ActiveCards+="\t"+c.toString();}
		System.out.println("Played so far:"+ActiveCards+"\n");
	}

	@Override
	public void ShowHand(List<Card> hand) {
		String line = "In hand:";
		for(Card c:hand) {line+="\t"+c.toString();}
		line+="\n";
		System.out.println(line);
	}

	@Override
	public void Clear() {//TODO Actual purge
		for(int i=0; i<10; i++){System.out.println();}
	}

	@Override
	public void RotatePlayer(int player) {
		Clear();
		System.out.println("Ready player "+player);
		//TODO - Pause to confirm player ready.
	}

	public List<Card> TakeTurn(List<Card> hand){//TODO - get selections
		while(hand.size()>2) {
			hand.remove(2);
		}
		return hand;
		//Remember yields and possible jokers
	}
	public List<Card> TakeDamage(List<Card> hand, int damage){//TODO - get selections
		return null;
		//Check if possible
	}

	@Override
	public boolean EndGame(boolean Victory) {
		Clear();
		if(Victory) {System.out.println("Congradulations");}
		else {System.out.println("Defeat");}
		return false;//TODO return true for new game.
	}
	@Override
	public boolean SingleEndGame(boolean Victory, int Jokers) {
		Clear();
		String message = "";
		if(Victory) {
			if(Jokers==0) {message+="Bronze";}
			else if(Jokers==1) {message+="Silver";}
			else if(Jokers==2) {message+="Gold";}
			message+="victory. Congradulations.";
		}else {message = "Defeat.";}
		System.out.println(message);
		return false;//TODO return true for new game.
	}
}
