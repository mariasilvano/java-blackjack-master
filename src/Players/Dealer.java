package Players;

import Cards.*;
import java.util.ArrayList;

/**
 * Class representing the Dealer of a Blackjack game. Dealer must stand on 17 or
 * over and can only accept an Ace as 11.
 *
 * @author David Winter
 */
public class Dealer extends BlackjackPlayer {
	/**
	 * The Deck of cards used for the game. The Dealer is in complete control of the
	 * Deck.
	 */
	private Deck deck;

	public DealerCardHand hand = new DealerCardHand();

	/**
	 * Whether or not the Dealer has dealt the initial two cards.
	 */
	private boolean firstDeal = true;

	/**
	 * The value the dealer must stand on.
	 */
	public static final int DEALER_STANDS_ON = 17;
	public static final int CARD_PACKS = 2;

	private boolean gameOver = true;
	private boolean cardsFaceUp = false;

	/**
	 * Whether the player is allowed to double at this stage in game.
	 */
	private boolean playerCanDouble = true;

	private String said = "Please place your wager.";

	/**
	 * Default constructor that creates a new dealer with a deck of 2 card packs.
	 */
	private PersonInfo personInfo;

    public Dealer() {
        super("Le Chiffre", 45, "male");
        this.personInfo = new PersonInfo("Le Chiffre", 45, "male");

		deck = new Deck(CARD_PACKS);
    }
	
	public void say(String announcement) {
		said = announcement;
		System.out.println(said);
	}

	public String says() {
		return said;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(Boolean b) {
		gameOver = b;
	}

	public boolean areCardsFaceUp() {
		return cardsFaceUp;
	}

	/**
	 * Acknowledge the bet from the player.
	 *
	 * @param player The player placing the bet.
	 * @param bet    The amount for the bet.
	 */
	public boolean acceptBetFrom(Player player, double bet) {
		boolean betSet = player.setBet(bet);

		if (player.betPlaced()) {
			say(player.getName() + ", thank you for your bet of $" + player.getBet() + ". Would you like me to deal?");
		} else {
			say("Please place your bet.");
		}

		return betSet;
	}

	public boolean reduceBetFrom(Player player, double amount) {
		boolean betReduced = player.reduceBet(amount);

		if (betReduced) {
			say(player.getName() + " reduced the bet by $" + amount + ". New bet is $" + player.getBet() + ".");
		} else {
			say(player.getName() + " tried to reduce the bet, but this failed. Amount $" + amount + " exceeds current bet of $" + player.getBet() + ".");
		}

		return betReduced;
	}

	public void allInMessage(Player player) {
		say(player.getName() + " has gone all in with $" + player.getBet() + ".");
	}

	public void playerStand(Player player) {
		say(player.getName() + " has chosen to stand.");
	}

	public void clearBetMessage(Player player) {
		say(player.getName() + " has cleared their bet.");
	}

	/**
	 * Deals initial two cards to each player and self.
	 *
	 * @param players The players to deal cards to.
	 *
	 * @return True if cards were dealt, otherwise false.
	 */
	public void deal(ArrayList<Player> players) {
		gameOver = false;
		cardsFaceUp = false;

		playerCanDouble = true;

		hand = new DealerCardHand();

		say("Initial deal made.");

		for (Player player : players) {
			player.hand = new PlayerCardHand();
			player.hand.add(deck.deal());
			player.hand.add(deck.deal());
		}

		this.hand.add(deck.deal());
		this.hand.add(deck.deal());

		firstDeal = false;

		for (Player player : players) {
			if (player.hand.hasBlackjack()) {
				say(player.getName() + " has Blackjack!");
				go(players);
				break;
			}
		}
	}

	/**
	 * Player requests another card.
	 *
	 * @param player The player requesting another card.
	 */
	public void hit(Player player) {
		Card newCard = deck.deal();
		player.hand.add(newCard);
		if (!player.betPlaced()) {
			deck.returnCard(newCard);
			say(player.getName() + ". First place your bet.");
			return;
		}
		say(player.getName() + " hits.");
		playerCanDouble = false;

		if (player.hand.isBust()) {
			say(player.getName() + " busts. Loses $" + player.getBet());
			player.loses();
		}
	}

	/**
	 * Player would like to place a bet up to double of his original e have the
	 * dealer give him one more card.
	 *
	 * @param player The player requesting to play double.
	 */
	public void playDouble(Player player, ArrayList<Player> players) {
		if (player.doubleBet() && playerCanDouble) {
			player.hand.add(deck.deal());
			say(player.getName() + " plays double.");
			if (player.hand.isBust()) {
				say(player.getName() + " busts. Loses $" + player.getBet());
				player.loses();
			}
		} else {
			say(player.getName() + ", you can't double. Not enough money.");
		}
	}

	

	/**
	 * The dealers turn.
	 *
	 * @param players The opposing players of the dealer.
	 */
	public void go(ArrayList<Player> players) {
		cardsFaceUp = true;

		if (!hand.hasBlackjack()) {
			keepPlaying();
		} else {
			say(this.getName() + " has BLACKJACK!");
		}

		for (Player player : players) {
			if (hand.hasBlackjack() && player.hand.hasBlackjack()) {
				say("Push");
				player.clearBet();
			} else if (player.hand.hasBlackjack()) {
				double winnings = (player.getBet() * 3) / 2;
				say(player.getName() + " wins with Blackjack $" + winnings);
				player.wins(player.getBet() + winnings);
			} else if (hand.hasBlackjack()) {
				say("Dealer has Blackjack. " + player.getName() + " loses $" + player.getBet());
				player.loses();
			} else if (hand.isBust()) {
				say("Dealer is bust. " + player.getName() + " wins $" + player.getBet());
				player.wins(player.getBet() * 2);
			} else if (player.hand.getTotal() == hand.getTotal()) {
				say("Push");
				player.clearBet();
			} else if (player.hand.getTotal() < hand.getTotal()) {
				say(player.getName() + " loses $" + player.getBet());
				player.loses();
			} else if (player.hand.getTotal() > hand.getTotal()) {
				say(player.getName() + " wins $" + player.getBet());
				player.wins(player.getBet() * 2);
			}
		}

		gameOver = true;
	}

	public void revealCards() {
		cardsFaceUp = true;
	}

	private void keepPlaying() {
		while (hand.getTotal() < DEALER_STANDS_ON) {
			hand.add(deck.deal());
			say(this.getName() + " hits.");
		}
		if (hand.isBust()) {
			say(this.getName() + " is BUST");
		} else {
			say(this.getName() + " stands on " + hand.getTotal());
		}
	}

	public int cardsLeftInPack() {
		return deck.size();
	}

	public void newDeck() {
		deck = new Deck(CARD_PACKS);
	}

	public boolean canPlayerDouble(Player player) {
		return playerCanDouble && player.canDouble();
	}

	public DealerCardHand getHand() {
		return hand;
	}

	public boolean isPlayerEligibleForDouble(Player player) {
		return !isGameOver() && canPlayerDouble(player);
	}
	
	public boolean isPlayerEligibleForAction(int playerIndex, int currentPlayerIndex) {
		return !isGameOver() && currentPlayerIndex == playerIndex;
	}
	
	public boolean isWalletSufficientForChip(Player player, int chipValue) {
		return isGameOver() && player.getWallet() >= chipValue;
  }
  
	public void setCanDouble(boolean playerCanDouble) {
		this.playerCanDouble = playerCanDouble;
	}
}
