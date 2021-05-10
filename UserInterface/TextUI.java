package UserInterface;

import java.util.List;
import CoreGame.Card;
import CoreGame.Game;

public class TextUI implements UserInterface{
	Game game;
	public TextUI(Game game) {this.game = game;}

	@Override
	public int GetNumPlayers() {
		// TODO Actually ask for user input.
		return 1;
	}

	@Override
	public void ShowState() {
		String DeckState = "Tavern deck: "+game.GetTavernDeck().size()+"\t\tDiscard pile: "+game.GetDiscard().size();
		if(game.GetJokers() != 0) {DeckState+="\t\tJokers: "+game.GetJokers();}
		System.out.println(DeckState);
		System.out.println(game.GetCastleDeck().size()+" foes remain.\t\t"
				+"Current foe: "+game.GetFoe().toString()+" (Damage: "+game.getDamage()+", Health: "+game.getHealth()+")\n\n");
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
}
