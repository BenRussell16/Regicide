package CoreGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import UserInterface.TextUI;
import UserInterface.UserInterface;

public class Game {
	private UserInterface UI;
	private List<Card> TavernDeck;
	private List<Card> CastleDeck;
	private List<Card> Discard;
	private List<Card> Active;
	private List<Card> Jokers;
	
	private Card foe;
	private int health;
	private int damage;
	
	private List<List<Card>> Hands;
	private int numPlayers;//Should be 1 to 4
	private int handSize;//8 to 5 depending on players.
	private int curPlayer;
	
	private boolean lossFlag;
	

	public Game() {
		UI = new TextUI(this);
		while(runGame()) {};//runGame returns if a new game is desired.
	}
	private boolean runGame() {
		UI.Clear();
		numPlayers = UI.GetNumPlayers();
		curPlayer = 0;

		//Generate the tavern deck.
		TavernDeck = new ArrayList<Card>();
		Jokers = new ArrayList<Card>();
		for(Card.Suit s:Card.Suit.values()) {
			for(int i=1; i<=10; i++) {
				TavernDeck.add(new Card(i,s));
			}
		}
		if(numPlayers == 4) {TavernDeck.add(new Card(0,null));}//Add Jokers
		if(numPlayers == 3 || numPlayers == 4) {TavernDeck.add(new Card(0,null));}
		else if(numPlayers == 1) {Jokers.add(new Card(0,null));Jokers.add(new Card(0,null));}
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
		newFoe();
		//Ready the discard and active piles.
		Discard = new ArrayList<Card>();
		Active = new ArrayList<Card>();
		
		//Ready the hands lists.
		Hands = new ArrayList<List<Card>>();
		for(int i=0; i<numPlayers; i++) {
			Hands.add(new ArrayList<Card>());
		}
		Deal();

		lossFlag = false;
		//Primary game loop
		while(!checkEndGameConditions()) {
			if(numPlayers!=1) {UI.RotatePlayer(curPlayer+1);}
			UI.TurnCycle(Hands.get(curPlayer));
			
			//TODO - Game loop.
			//Take turn.
			//Apply turn.
			//Take damage.
			
			curPlayer++;
			if(curPlayer==numPlayers) {curPlayer=0;}
			break;//TODO - Remove once I have pauses.
		}
		
		//Launch new game if desired.
		if(numPlayers==1) {
			return UI.SingleEndGame(!lossFlag, Jokers.size());
		}else {
			return UI.EndGame(!lossFlag);
		}
	}
	
	private boolean checkEndGameConditions() {//Returns true if the game is over
		return CastleDeck.isEmpty() || lossFlag;
	}
	
	
	
	private void newFoe() {
		foe=CastleDeck.get(0);
		health=foe.getValue()*2;
		damage=foe.getValue();
	}

	
	
	
	private void Deal() {
		if(numPlayers==1) {handSize=8;}
		else if(numPlayers==2) {handSize=7;}
		else if(numPlayers==3) {handSize=6;}
		else if(numPlayers==4) {handSize=5;}
		for(List<Card> hand:Hands) {
			for(int i=0; i<handSize; i++) {
				hand.add(TavernDeck.remove(0));
			}
			Sort(hand);
		}
	}
	
	private void Shuffle(List<Card> deck) {
		Collections.shuffle(deck);
	}
	private void Sort(List<Card> sorting) {
		sorting.sort(Card.getComparator());
	}
	
	

	public List<Card> GetTavernDeck(){return TavernDeck;}
	public List<Card> GetCastleDeck(){return CastleDeck;}
	public List<Card> GetDiscard(){return Discard;}
	public List<Card> GetInPlay(){return Active;}
	public List<List<Card>> GetHands(){return Hands;}
	public int GetJokers(){return Jokers.size();}

	public Card GetFoe(){return foe;}
	public int getHealth() {return health;}
	public int getDamage() {return damage;}
}
