
public class Card {
	int value;
	Suit suit;
	boolean isJoker;
	public Card(int value, Suit suit) {
		this.value = value;
		this.suit = suit;
		isJoker = value == 0;
	}

	public int Value() {return value;}
	public Suit Suit() {return suit;}

	public boolean isFace() {return value>10;}
	public boolean isJoker() {return isJoker;}
	
	public String toString() {
		if(isJoker) {return "Joker";}
		String ValueName = "";
		if(value==1) {ValueName = "Ace";}
		else if(value==11) {ValueName = "Jack";}
		else if(value==12) {ValueName = "Queen";}
		else if(value==13) {ValueName = "King";}
		else {ValueName += value;}
		return ValueName+" of "+Suit().toNiceString();
	}
	
	public enum Suit{
		CLUBS,DIAMONDS,HEARTS,SPADES;
		public String toNiceString(){return name().toUpperCase().substring(0, 1)
				+ name().toLowerCase().substring(1);}
	}
}