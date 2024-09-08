package Players;

import java.io.*;

import Cards.*;

/**
 * Class that represents a normal Blackjack player.
 *
 * @author David Winter
 */
public class Player extends BlackjackPlayer implements Serializable {
	/**
	 * The card hand the player holds.
	 */
	public transient PlayerCardHand hand = new PlayerCardHand();

	/**
	 * The players wallet size...
	 */
	private double wallet = 100.00;

	/**
	 * The players current bet.
	 */
	private double bet = 0.0;

	public Player() {
		super();
	}

	private PersonInfo personInfo;

    public Player(PersonInfo info) {
        super(info.getName(), info.getAge(), info.getGender());
        this.personInfo = info;
    }

	/**
	 * Sets the players wallet size.
	 *
	 * @param amount The amount in the players wallet.
	 */
	public void setWallet(double amount) {
		this.wallet = amount;
	}

	/**
	 * Returns the size of the players wallet.
	 *
	 * @return The size of the players wallet.
	 */
	public double getWallet() {
		return this.wallet;
	}

	/**
	 * Set the players bet.
	 * 
	 * @param bet The total size of bet the player wishes to place.
	 *
	 * @return Whether or not the bet was valid.
	 */
	public boolean setBet(double bet) {
		if (bet <= (getWallet() + getBet())) {
			this.wallet += this.bet; // reset old bet
			this.bet = bet; // set new bet
			this.wallet -= bet; // update wallet
			return true;
		}

		return false;
	}

	public boolean reduceBet(double amount) {
		if (amount > 0 && amount <= this.bet) {
			this.bet -= amount;
			this.wallet += amount;
			return true;
		}
		return false;
	}

	/**
	 * Get the players current bet.
	 *
	 * @return The players current bet.
	 */
	public double getBet() {
		return this.bet;
	}

	public void clearBet() {
		this.wallet += this.bet;
		this.bet = 0.0;
	}

	public void allIn() {
		this.bet += this.wallet;
		this.wallet = 0.0;
	}

	/**
	 * The amount the player wishes to double.
	 *
	 * @return Whether the bet was valid or not.
	 */
	public boolean doubleBet() {
		return setBet(getBet() * 2);
	}

	// insurance

	public void loses() {
		this.bet = 0.0;
	}

	public void wins(double amount) {
		this.wallet += amount;
		this.bet = 0.0;
	}

	/**
	 * Has the player placed a bet yet?
	 *
	 * @return True if the player has placed a bet, otherwise false.
	 */
	public boolean betPlaced() {
		return getBet() > 0.0;
	}

	// wallet less than 1 in case .5 is in there from a blackjack
	public boolean isBankrupt() {
		return getWallet() < 1 && getBet() <= 0;
	}

	public boolean canDouble() {
		return getBet() <= getWallet();
	}

	public boolean canPlaceBet() {
		return !isBankrupt() && betPlaced();
	}

	/**
	 * Shows (returns) the players hand.
	 *
	 * @return The players card hand.
	 */
	public PlayerCardHand getHand() {
		return this.hand;
	}

	public void clearHand() {
		hand.clear();
	}
}
