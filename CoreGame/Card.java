package CoreGame;

import java.util.Comparator;

public class Card {
	private int number;
	private Suit suit;
	private boolean isJoker;
	private boolean isYield;//Also used to skip.
	
	public Card(int number, Suit suit) {
		this.number = number;
		this.suit = suit;
		isJoker = number == 0;
		isYield = number == -1;
	}

	public int getNumber() {return number;}
	public Suit getSuit() {return suit;}
	public int getValue() {
		if(number==11) {return 10;}
		else if(number==12) {return 15;}
		else if(number==13) {return 20;}
		return number;
	}

	public boolean isFace() {return number>10;}
	public boolean isJoker() {return isJoker;}
	public boolean isYield() {return isYield;}
	
	public String toString() {
		if(isJoker) {return "Joker";}
		if(isYield) {return "Yield";}
		String ValueName = "";
		if(number==1) {ValueName = "Ace";}
		else if(number==11) {ValueName = "Jack";}
		else if(number==12) {ValueName = "Queen";}
		else if(number==13) {ValueName = "King";}
		else {ValueName += number;}
		return ValueName+" of "+suit.toNiceString();
	}
	
	public static Comparator<Card> getComparator(){
		return new Comparator<Card>() {
			@Override
			public int compare(Card o1, Card o2) {
				if(o1.isYield) {return -1;}
				else if(o2.isYield) {return 1;}
				else if(o1.isJoker) {return -1;}
				else if(o2.isJoker) {return 1;}
				if(o1.getNumber()!=o2.getNumber()) {return o1.getNumber() - o2.getNumber();}
				return o1.getSuit().ordinal()-o2.getSuit().ordinal();
			}
		};
	}
	
	public enum Suit{
		CLUBS,DIAMONDS,HEARTS,SPADES;
		public String toNiceString(){return name().toUpperCase().substring(0, 1)
				+ name().toLowerCase().substring(1);}
	}
}
