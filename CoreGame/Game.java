package CoreGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import CoreGame.Card.Suit;
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
		UI = new TextUI();
		while(runGame()) {};//runGame returns if a new game is desired.
	}
	private boolean runGame() {
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
		int firstYield = -1;
		//Primary game loop
		while(!checkEndGameConditions()) {
			if(numPlayers!=1) {
				UI.RotatePlayer(curPlayer);
				UI.TurnCycle(TavernDeck.size(), Discard.size(), CastleDeck.size(), foe, damage, health, Active, Hands, curPlayer);
			}else {
				UI.TurnCycle(TavernDeck.size(), Discard.size(), Jokers.size(), CastleDeck.size(), foe, damage, health, Active,Hands.get(curPlayer));
			}
			//Take turn.
			List<Card> turn = new ArrayList<Card>();
			turn.addAll(Hands.get(curPlayer));
			if(numPlayers==1) {turn.addAll(Jokers);}//Let the Jokers be called in single player.
			else if(firstYield!=curPlayer+1 && !(curPlayer==numPlayers-1 && firstYield==0)){
				turn.add(new Card(-1, null));//Lets players return a yield if viable.
			}
			turn = UI.TakeTurn(turn);
			//Apply turn.
			if(turn.get(0).isJoker()) {//Jokers skip rest of turn and take special actions.
				firstYield=-1;
				if(numPlayers==1) {
					Jokers.remove(turn.get(0));
					while(!Hands.get(0).isEmpty()) {
						Discard.add(Hands.get(0).remove(0));//Discard current hand.
					}
					Deal();//Redraw to full.
				}else {
					Active.add(turn.get(0));
					Hands.get(curPlayer).remove(turn.get(0));
					//TODO - Multiplayer Joker actions
				}
			}else {
				if(turn.get(0).isYield()){//Yields get no benefit and go straight to taking damage.
					if(firstYield==-1) {firstYield = curPlayer;}//Set the flag for first yielder.
				}else{
					firstYield=-1;
					int total=0;//Normal selection.
					boolean[] suits = {false, false, false, false};//new boolean[4] filled with false
					for(Card c:turn) {
						total+=c.getValue();
						suits[c.getSuit().ordinal()]=true;
						Active.add(c);
						Hands.get(curPlayer).remove(c);
					}
					//Apply selection.
					if(suits[Suit.CLUBS.ordinal()] && ActiveSuit(Suit.CLUBS)) {//Apply damage to Foe.
						health-=total*2;
					}else {health-=total;}
					if(suits[Suit.DIAMONDS.ordinal()] && ActiveSuit(Suit.DIAMONDS)) {//Deal out needed cards.
						Draw(Hands, curPlayer, total);
					}
					if(suits[Suit.HEARTS.ordinal()] && ActiveSuit(Suit.HEARTS)) {//Refill Tavern deck.
						int toHeal = total;
						if(Discard.size()<toHeal) {toHeal=Discard.size();}
						Shuffle(Discard);
						for(int i=0;i<toHeal;i++) {
							TavernDeck.add(Discard.remove(0));
						}
					}
					if(suits[Suit.SPADES.ordinal()] && ActiveSuit(Suit.SPADES)) {//Apply damage reduction.
						damage-=total;
						if(damage<0) {damage=0;}
					}
				}
				if(health<=0) {//Foe defeated.
					CastleDeck.remove(foe);
					if(health==0) {
						TavernDeck.add(0, foe);
					}else {Discard.add(foe);}
					if(!CastleDeck.isEmpty()) {newFoe();}
					while(!Active.isEmpty()) {Discard.add(Active.remove(0));}//Empty the active list.
				}else {//Take damage
					List<Card> discarded = new ArrayList<Card>();
					discarded.addAll(Hands.get(curPlayer));
					if(damage==0) {//Lets them not discard if there's no damage.
						discarded.add(new Card(-1, null) {
							public String toString() {return "Done";}
						});
					}
					discarded = UI.TakeDamage(discarded, damage);
					if(discarded == null) {
						lossFlag = true;
					}else if(discarded.isEmpty() || !discarded.get(0).isYield()){
						for(Card c:discarded) {
							Discard.add(c);
							Hands.get(curPlayer).remove(c);
						}
					}
				}
			}
			if(numPlayers==1) {
				UI.ShowSingleState(TavernDeck.size(), Discard.size(), Jokers.size(), CastleDeck.size(), foe, damage, health, Active);
			}else {
				UI.ShowMultiState(TavernDeck.size(), Discard.size(), CastleDeck.size(), foe, damage, health, Active, Hands, curPlayer);
			}
			UI.ShowHand(Hands.get(curPlayer));
			if(numPlayers!=1) {
				curPlayer++;
				if(curPlayer==numPlayers) {curPlayer=0;}
			}
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
	
	
	/**
	 * Return is a given suit is being blocked by the current Foe.
	 * @param s - The suit being checked.
	 * @return - If the suit power can be used.
	 */
	private boolean ActiveSuit(Suit s) {
		if(foe.getSuit()!=s){return true;}
		for(Card c:Active){
			if(c.isJoker()) {return true;}
		}
		return false;
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
			while(hand.size()<handSize && !TavernDeck.isEmpty()) {
				hand.add(TavernDeck.remove(0));
			}
			Sort(hand);
		}
	}
	private void Draw(List<List<Card>> dealTo, int current, int numCards) {
		int fullHands=0;
		while(numCards>0 && fullHands!=numPlayers && !TavernDeck.isEmpty()) {
			if(dealTo.get(current).size()==handSize) {
				fullHands++;
			}else {
				fullHands=0;
				dealTo.get(current).add(TavernDeck.remove(0));
				numCards--;
			}
			current++;
			if(current==numPlayers) {current=0;}
		}
		for(List<Card> deals:dealTo) {Sort(deals);}//Sort the hands.
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
