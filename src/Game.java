import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
	private static List<Card> TavernDeck;
	private static List<Card> CastleDeck;
	private static List<Card> Discard;
	private static List<List<Card>> Hands;

	public static void main(String[] args) {
		//Generate the tavern deck.
		//TODO Include Jokers as appropriate.
		TavernDeck = new ArrayList<Card>();
		for(Card.Suit s:Card.Suit.values()) {
			for(int i=1; i<=10; i++) {
				TavernDeck.add(new Card(i,s));
			}
		}
		Shuffle(TavernDeck);
		//Build the castle deck.
		CastleDeck = new ArrayList<Card>();
		for(int i=11; i<=13; i++) {
			List<Card> layer = new ArrayList<Card>();
			for(Card.Suit s:Card.Suit.values()) {
				layer.add(new Card(i,s));
			}
			Shuffle(layer);
			CastleDeck.addAll(layer);
		}
		//Ready the discard pile.
		Discard = new ArrayList<Card>();
		//Ready the hands list.
		Hands = new ArrayList<List<Card>>();
		Hands.add(new ArrayList<Card>());//Player 1.
		Deal();

		System.out.println("Tavern deck: "+TavernDeck.size()+"\t\tDiscard pile: "+Discard.size());
		System.out.println(CastleDeck.size()+" foes remain.\t\tCurrent foe: "+CastleDeck.get(0).toString());
		
		
		
	}

	public static void Deal() {
		int HandSize = 0;
		if(Hands.size()==1) {HandSize=7;}//Set hand sizes //TODO more players
		for(List<Card> hand:Hands) {
			for(int i=0; i<HandSize; i++) {
				hand.add(TavernDeck.remove(0));
			}
		}
	}
	
	public static void Shuffle(List<Card> deck) {
		Collections.shuffle(deck);
	}
}