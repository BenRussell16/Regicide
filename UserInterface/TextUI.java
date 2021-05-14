package UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import CoreGame.Card;
import CoreGame.Game;

public class TextUI implements UserInterface{
	Scanner scan;
	public TextUI() {
		scan = new Scanner(System.in);
	}

	@Override
	public int GetNumPlayers() {
		Clear();
		System.out.println("How many players (1-4)?");
		int numPlayers = 0;
		while(numPlayers<1 || numPlayers>4) {
			if(scan.hasNext()) {
				if(scan.hasNextInt()) {
					numPlayers = scan.nextInt();
				}else {
					//Not an int.
					scan.next();
				}
			}
		}
		return numPlayers;
	}

	@Override
	public void ShowSingleState(int Tavern, int Discard, int Jokers, int Castle, Card Foe, int Damage, int Health, List<Card> Active) {
		Clear();
		printDecks(Tavern, Discard, Jokers);
		printBoard(Castle, Foe, Damage, Health, Active);
	}
	@Override
	public void ShowMultiState(int Tavern, int Discard, int Castle, Card Foe, int Damage, int Health, List<Card> Active,
			List<List<Card>> hands, int curPlayer) {
		Clear();
		printDecks(Tavern, Discard, 0);
		String otherHands = "Other hands:";
		for(int i=0; i<hands.size();i++) {
			if(i!=curPlayer) {
				otherHands+="\tPlayer "+(i+1)+" has "+hands.get(i).size()+" cards.";
			}
		}
		System.out.println(otherHands);
		printBoard(Castle, Foe, Damage, Health, Active);
	}
	private void printDecks(int Tavern, int Discard, int Jokers) {
		String DeckState = "Tavern deck: "+Tavern+"\t\tDiscard pile: "+Discard;
		if(Jokers != 0) {DeckState+="\t\tJokers: "+Jokers;}
		System.out.println(DeckState);
	}
	private void printBoard(int Castle, Card Foe, int Damage, int Health, List<Card> Active) {
		System.out.println(Castle+" foes remain.\t\t"+"Current foe: "+Foe.toString()+" (Damage: "+Damage+", Health: "+Health+")");
		String ActiveCards = "";
		for(Card c:Active) {ActiveCards+="\t"+c.toString();}
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
		System.out.println("Ready player "+(player+1));
		while(!scan.hasNext()) {//TODO - Pause to confirm player ready.
		}
		scan.next();
		
	}

	public List<Card> TakeTurn(List<Card> hand){
		printOptions(hand);
		List<Card> selection = new ArrayList<Card>();
		while(selection.isEmpty()) {
			if(scan.hasNext()) {
				if(scan.hasNextInt()) {
					int chosen = scan.nextInt();
					if(chosen>=0 && chosen<=hand.size()) {selection.add(hand.remove(chosen));}
				}else {
					//Not an int.
					scan.next();
				}
			}
		}
		if(!selection.get(0).isJoker()&&!selection.get(0).isYield()) {
			int number = selection.get(0).getNumber();
			int sum=0;
			if(number!=1) {sum=number;}
			List<Card> toRemove = new ArrayList<Card>();
			for(Card c:hand) {
				if(c.isJoker() || c.isYield()//Remove utility
						|| (c.getNumber()!=number && c.getNumber()!=1 && number!=1)//Remove incompatible
						|| (number!=1 && c.getNumber()+sum>10)//Remove too high to combo
						) {
					toRemove.add(c);
				}
				
			}
			hand.removeAll(toRemove);
			if(!hand.isEmpty()) {
				hand.add(new Card(-1, null) {public String toString() {return "Done";}});//Let them stop comboing.
				boolean finishedPlaying=false;
				System.out.print("Played so far:");//Print current selection.
				for(Card c:selection) {System.out.print("\t"+c.toString());}
				System.out.println();
				printOptions(hand);
				while(!finishedPlaying) {
					if(scan.hasNext()) {
						if(scan.hasNextInt()) {
							int chosen = scan.nextInt();
							if(chosen>=0 && chosen<=hand.size()) {
								Card selected = hand.remove(chosen);
								if(selected.isYield()) {
									finishedPlaying=true;
								}else {
									selection.add(selected);
									if(selected.getNumber()!=1) {number = selected.getNumber();}
									sum+=selected.getNumber();
									toRemove = new ArrayList<Card>();
									for(Card c:hand) {
										if(!c.isYield() && c.getNumber()!=1 && c.getNumber()+sum>10) {//Remove now invalid number cards
											toRemove.add(c);
										}
									}
									hand.removeAll(toRemove);
									if(hand.size()==1) {finishedPlaying=true;}
									else{
										System.out.print("Played so far:");//Print current selection.
										for(Card c:selection) {System.out.print("\t"+c.toString());}
										System.out.println();
										printOptions(hand);
									}
								}
							}
						}else {
							//Not an int.
							scan.next();
						}
					}
					
				}
			}
		}
		return selection;
	}
	public List<Card> TakeDamage(List<Card> hand, int damage){
		System.out.println("Damage: "+damage);
		printOptions(hand);
		int total = 0;
		for(Card c:hand) {if(!c.isJoker()&&!c.isYield()) {total+=c.getValue();}}
		if(total<damage) {return null;}//Loss condition check.
		total=0;
		List<Card> selection = new ArrayList<Card>();
		boolean finishedDiscarding=false;
		while(!finishedDiscarding) {
			if(scan.hasNext()) {
				if(scan.hasNextInt()) {
					int chosen = scan.nextInt();
					Card selected = hand.remove(chosen);
					if(selected.isYield()) {
						finishedDiscarding=true;
					}else {
						total+=selected.getValue();
						if(total<0) {total=0;}
						if(chosen>=0 && chosen<=hand.size()) {selection.add(selected);}
						if(total>=damage && total-selected.getValue()<total) {//Lets them discard extras.
							hand.add(new Card(-1, null) {
								public String toString() {return "Done";}//TODO - not get multiple dones
							});
						}
						int remaining = damage-total;
						if(remaining<0) {remaining=0;}
						System.out.println("Damage: "+damage+" ("+remaining+" remaining)\tCurrently discarding: "+selection);
						printOptions(hand);
					}
				}else {
					//Not an int.
					scan.next();
				}
			}
		}
		return selection;
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
			message+=" victory. Congradulations.";
		}else {message = "Defeat.";}
		System.out.println(message);
		return false;//TODO return true for new game.
	}
	
	private void printOptions(List<Card> hand) {
		String options = "";
		for(int i=0;i<hand.size();i++) {
			options+="\t["+i+"] "+hand.get(i);
			if((i+1)%5==0) {options+="\n";}
		}
		System.out.println("Available:\n"+options);
	}
}
