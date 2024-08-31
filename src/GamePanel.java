import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import Players.*;
import Cards.*;

public class GamePanel extends JPanel implements ActionListener {
	private Dealer dealer;
	private ArrayList<Player> players;
	private int currentPlayerIndex;

	private GameTable table;

	private ArrayList<JButton> newGameButtons;
	private ArrayList<JButton> hitButtons;
	private ArrayList<JButton> doubleButtons;
	private ArrayList<JButton> standButtons;
	private ArrayList<JButton> add1ChipButtons;
	private ArrayList<JButton> add5ChipButtons;
	private ArrayList<JButton> add10ChipButtons;
	private ArrayList<JButton> add25ChipButtons;
	private ArrayList<JButton> add100ChipButtons;
	private ArrayList<JButton> reduce1BetButtons;
	private ArrayList<JButton> reduce10BetButtons;
	private ArrayList<JButton> allInButtons;
	private ArrayList<JButton> clearBetButtons;
	private JButton resetButton;

	private ArrayList<JLabel> currentBetLabels;
	private ArrayList<JLabel> playerWalletLabels;
	private JLabel cardsLeft = new JLabel("Cards left...");
	private JLabel dealerSays = new JLabel("Dealer says...");

	public GamePanel() {
		this.setLayout(new BorderLayout());
		this.setBackground(Color.BLACK);

		table = new GameTable();
		add(table, BorderLayout.CENTER);

		// Inicialização das listas de controles
		newGameButtons = new ArrayList<>();
		hitButtons = new ArrayList<>();
		doubleButtons = new ArrayList<>();
		standButtons = new ArrayList<>();
		add1ChipButtons = new ArrayList<>();
		add5ChipButtons = new ArrayList<>();
		add10ChipButtons = new ArrayList<>();
		add25ChipButtons = new ArrayList<>();
		add100ChipButtons = new ArrayList<>();
		reduce1BetButtons = new ArrayList<>();
		reduce10BetButtons = new ArrayList<>();
		allInButtons = new ArrayList<>();
		clearBetButtons = new ArrayList<>();
		currentBetLabels = new ArrayList<>();
		playerWalletLabels = new ArrayList<>();

		dealer = new Dealer();
		players = new ArrayList<>();
		players.add(new Player("James Bond", 32, "Male"));
		players.add(new Player("Ethan Hunt", 35, "Male"));
		for (Player player : players) {
			player.setWallet(100.00);
		}

		currentPlayerIndex = 0;

		// Painel de Dealer e cartas restantes
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(Color.BLACK);
		topPanel.add(dealerSays, BorderLayout.NORTH);
		topPanel.add(cardsLeft, BorderLayout.SOUTH);
		dealerSays.setForeground(Color.WHITE);
		cardsLeft.setForeground(Color.WHITE);
		resetButton = new JButton("Reset Game");
		resetButton.addActionListener(this);
		topPanel.add(resetButton, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		// Criar painéis de controle individual para cada jogador na parte inferior
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 2));
		bottomPanel.setBackground(Color.DARK_GRAY);

		JPanel leftPlayerPanel = new JPanel();
		leftPlayerPanel.setLayout(new BorderLayout());
		leftPlayerPanel.setBackground(Color.DARK_GRAY);

		JPanel rightPlayerPanel = new JPanel();
		rightPlayerPanel.setLayout(new BorderLayout());
		rightPlayerPanel.setBackground(Color.DARK_GRAY);

		for (int i = 0; i < players.size(); i++) {
			JPanel playerPanel = new JPanel();
			playerPanel.setLayout(new BorderLayout());
			playerPanel.setBackground(Color.DARK_GRAY);

			JPanel betPanel = new JPanel();
			betPanel.setBackground(Color.DARK_GRAY);
			JLabel currentBet = new JLabel("Please set your bet...");
			currentBet.setForeground(Color.WHITE);
			JLabel playerWallet = new JLabel("$999.99");
			playerWallet.setForeground(Color.WHITE);
			betPanel.add(currentBet);
			betPanel.add(playerWallet);
			betPanel.add(createChipButton("1", add1ChipButtons));
			betPanel.add(createChipButton("5", add5ChipButtons));
			betPanel.add(createChipButton("10", add10ChipButtons));
			betPanel.add(createChipButton("25", add25ChipButtons));
			betPanel.add(createChipButton("100", add100ChipButtons));
			betPanel.add(createButton("All In", allInButtons));
			betPanel.add(createButton("Clear", clearBetButtons));
			currentBetLabels.add(currentBet);
			playerWalletLabels.add(playerWallet);

			JPanel optionsPanel = new JPanel();
			optionsPanel.setBackground(Color.DARK_GRAY);
			optionsPanel.add(createButton("Deal", newGameButtons));
			optionsPanel.add(createButton("Hit", hitButtons));
			optionsPanel.add(createButton("Double", doubleButtons));
			optionsPanel.add(createButton("Stand", standButtons));
			optionsPanel.add(createButton("-1", reduce1BetButtons));
			optionsPanel.add(createButton("-10", reduce10BetButtons));

			playerPanel.add(betPanel, BorderLayout.NORTH);
			playerPanel.add(optionsPanel, BorderLayout.SOUTH);

			if (i == 0) {
				leftPlayerPanel.add(playerPanel, BorderLayout.SOUTH);
			} else {
				rightPlayerPanel.add(playerPanel, BorderLayout.SOUTH);
			}
		}

		bottomPanel.add(leftPlayerPanel);
		bottomPanel.add(rightPlayerPanel);

		add(bottomPanel, BorderLayout.SOUTH);

		updateValues();
	}

	private void resetGame() {
		dealer.setGameOver(true);
		table.setGameOver(true);
		for (int i = 0; i < players.size(); i++) {
			players.get(i).clearHand();
			clearBet(i);
		}
		updateValues();
	}

	private JButton createButton(String text, ArrayList<JButton> buttonList) {
		JButton button = new JButton(text);
		button.addActionListener(this);

		// Definindo cores diferentes para cada função
		if (text.equals("Deal")) {
			button.setBackground(Color.GREEN);
		} else if (text.equals("Hit")) {
			button.setBackground(Color.YELLOW);
		} else if (text.equals("Double")) {
			button.setBackground(Color.ORANGE);
		} else if (text.equals("Stand")) {
			button.setBackground(Color.RED);
		} else {
			button.setBackground(Color.GRAY);
		}

		button.setForeground(Color.BLACK);
		buttonList.add(button);
		return button;
	}

	private JButton createChipButton(String text, ArrayList<JButton> buttonList) {
		JButton button = new JButton(text);
		button.addActionListener(this);
		button.setBackground(Color.GRAY);
		button.setForeground(Color.BLACK);
		button.setToolTipText("Add a $" + text + " chip to your current bet.");
		buttonList.add(button);
		return button;
	}

	public void actionPerformed(ActionEvent evt) {
		String act = evt.getActionCommand();
		Object source = evt.getSource();
		int playerIndex = getPlayerIndex(source);

		if (act.equals("Deal")) {
			if (!allPlayersHaveBet()) {
				JOptionPane.showMessageDialog(this,
						"Todos os jogadores devem fazer uma aposta antes de come\u00E7ar o jogo.",
						"Aposta insuficiente", JOptionPane.WARNING_MESSAGE);
				return;
			}
			newGame();
		} else if (act.equals("Hit")) {
			hit(playerIndex);
		} else if (act.equals("Double")) {
			playDouble(playerIndex);
		} else if (act.equals("Stand")) {
			stand(playerIndex);
		} else if (isBetEvent(act)) {
			increaseBet(playerIndex, Integer.parseInt(act));
		} else if (isReduceBetEvent(act)) {
			int reduction = act.equals("-1") ? 1 : 10;
			reduceBetBy(playerIndex, reduction);
		} else if (act.equals("Clear")) {
			clearBet(playerIndex);
		} else if (act.equals("All In")) {
			allInBet(playerIndex);
		} else if (act.equals("Reset Game")) {
			resetGame();
		}

		updateValues();
	}

	private int getPlayerIndex(Object source) {
		for (int i = 0; i < players.size(); i++) {
			if (newGameButtons.get(i) == source || hitButtons.get(i) == source || doubleButtons.get(i) == source
					|| standButtons.get(i) == source || clearBetButtons.get(i) == source
					|| allInButtons.get(i) == source ||
					add1ChipButtons.get(i) == source || add5ChipButtons.get(i) == source
					|| add10ChipButtons.get(i) == source || add25ChipButtons.get(i) == source
					|| add100ChipButtons.get(i) == source ||
					reduce1BetButtons.get(i) == source || reduce10BetButtons.get(i) == source) {
				return i;
			}
		}
		return -1;
	}

	public boolean isBetEvent(String act) {
		return act.equals("1") || act.equals("5") || act.equals("10") || act.equals("25") || act.equals("100");
	}

	public boolean isReduceBetEvent(String act) {
		return act.equals("-1") || act.equals("-10");
	}

	public void newGame() {
		dealer.deal(players);
		ArrayList<PlayerCardHand> playerHands = new ArrayList<>();
		for (Player player : players) {
			playerHands.add(player.getHand());
		}
		table.setHands(dealer.getHand(), playerHands);
		ArrayList<String> playerNames = new ArrayList<>();
		for (Player player : players) {
			playerNames.add(player.getName());
		}
		table.setNames(dealer.getName(), playerNames);
		table.setGameOver(false);
		currentPlayerIndex = 0;
		updateValues();
		updateTurn();
	}

	private boolean allPlayersHaveBet() {
		for (Player player : players) {
			if (player.getBet() <= 0) {
				return false;
			}
		}
		return true;
	}

	public void hit(int playerIndex) {
		dealer.hit(players.get(playerIndex));
		updateValues();
		if (players.get(playerIndex).hand.isBust()) {
			JOptionPane.showMessageDialog(this, players.get(playerIndex).getName() + " estourou!");
			nextTurn();
		}
	}

	public void playDouble(int playerIndex) {
		dealer.playDouble(players.get(playerIndex), players);
		updateValues();
		if (players.get(playerIndex).hand.isBust()) {
			JOptionPane.showMessageDialog(this, players.get(playerIndex).getName() + " estourou!");
		}
		nextTurn();
	}

	public void stand(int playerIndex) {
		nextTurn();
	}

	public void increaseBet(int playerIndex, int amount) {
		Player player = players.get(playerIndex);
		dealer.acceptBetFrom(player, amount + player.getBet());
	}

	public void reduceBetBy(int playerIndex, int amount) {
		Player player = players.get(playerIndex);
		dealer.reduceBetFrom(player, amount);
	}

	public void clearBet(int playerIndex) {
		Player player = players.get(playerIndex);
		player.clearBet();
	}

	public void allInBet(int playerIndex) {
		Player player = players.get(playerIndex);
		player.allIn();
	}

	public void updateValues() {
		Color colorText = Color.WHITE;

		dealerSays.setText("<html><p align=\"center\"><font face=\"Serif\" color=\"white\" style=\"font-size: 20pt\">"
				+ dealer.says() + "</font></p></html>");

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			doubleButtons.get(i)
					.setEnabled(!dealer.isGameOver() && dealer.canPlayerDouble(player) && currentPlayerIndex == i);
			newGameButtons.get(i).setEnabled(dealer.isGameOver() && player.betPlaced() && !player.isBankrupt());
			hitButtons.get(i).setEnabled(!dealer.isGameOver() && currentPlayerIndex == i);
			standButtons.get(i).setEnabled(!dealer.isGameOver() && currentPlayerIndex == i);
			clearBetButtons.get(i).setEnabled(dealer.isGameOver() && player.betPlaced());
			allInButtons.get(i).setEnabled(dealer.isGameOver() && player.getWallet() >= 1.0);
			add1ChipButtons.get(i).setEnabled(dealer.isGameOver() && player.getWallet() >= 1.0);
			add5ChipButtons.get(i).setEnabled(dealer.isGameOver() && player.getWallet() >= 5);
			add10ChipButtons.get(i).setEnabled(dealer.isGameOver() && player.getWallet() >= 10);
			add25ChipButtons.get(i).setEnabled(dealer.isGameOver() && player.getWallet() >= 25);
			add100ChipButtons.get(i).setEnabled(dealer.isGameOver() && player.getWallet() >= 100);
			reduce1BetButtons.get(i).setEnabled(dealer.isGameOver() && player.getBet() >= 1);
			reduce10BetButtons.get(i).setEnabled(dealer.isGameOver() && player.getBet() >= 10);

			// redraw bet
			currentBetLabels.get(i).setText(Double.toString(player.getBet()));
			playerWalletLabels.get(i).setText(Double.toString(player.getWallet()));
			currentBetLabels.get(i).setForeground(colorText);
			playerWalletLabels.get(i).setForeground(colorText);
		}

		// redraw cards and totals
		ArrayList<PlayerCardHand> playerHands = new ArrayList<>();
		for (Player player : players) {
			playerHands.add(player.getHand());
		}
		table.update(dealer.getHand(), playerHands, dealer.areCardsFaceUp());
		ArrayList<String> playerNames = new ArrayList<>();
		for (Player player : players) {
			playerNames.add(player.getName());
		}
		table.setNames(dealer.getName(), playerNames);
		table.setPlayerNameColor(colorText);
		table.repaint();

		cardsLeft.setText(
				"Deck: " + dealer.cardsLeftInPack() + "/" + (dealer.CARD_PACKS * Cards.CardPack.CARDS_IN_PACK));
		cardsLeft.setForeground(Color.WHITE);

		for (Player player : players) {
			if (player.isBankrupt()) {
				moreFunds(player);
			}
		}
	}

	private void moreFunds(Player player) {
		int response = JOptionPane.showConfirmDialog(null,
				"Marshall Aid. One Hundred dollars. With the compliments of the USA.", "Out of funds",
				JOptionPane.YES_NO_OPTION);

		if (response == JOptionPane.YES_OPTION) {
			player.setWallet(100.00);
			updateValues();
		}
	}

	public void savePlayer(int playerIndex) {
		Player player = players.get(playerIndex);
		if (dealer.isGameOver()) {
			JFileChooser playerSaveDialog = new JFileChooser("~");
			SimpleFileFilter fileFilter = new SimpleFileFilter(".ser", "(.ser) Serialised Files");
			playerSaveDialog.addChoosableFileFilter(fileFilter);
			int playerSaveResponse = playerSaveDialog.showSaveDialog(this);

			if (playerSaveResponse == playerSaveDialog.APPROVE_OPTION) {
				String filePath = playerSaveDialog.getSelectedFile().getAbsolutePath();

				try {
					ObjectOutputStream playerOut = new ObjectOutputStream(new FileOutputStream(filePath));
					playerOut.writeObject(player);
					playerOut.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Can't save a player while a game is in progress.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public void openPlayer(int playerIndex) {
		Player player = players.get(playerIndex);
		if (dealer.isGameOver()) {
			JFileChooser playerOpenDialog = new JFileChooser("~");
			SimpleFileFilter fileFilter = new SimpleFileFilter(".ser", "(.ser) Serialised Files");
			playerOpenDialog.addChoosableFileFilter(fileFilter);
			int playerOpenResponse = playerOpenDialog.showOpenDialog(this);

			if (playerOpenResponse == playerOpenDialog.APPROVE_OPTION) {
				String filePath = playerOpenDialog.getSelectedFile().getAbsolutePath();

				try {
					ObjectInputStream playerIn = new ObjectInputStream(new FileInputStream(filePath));
					Player openedPlayer = (Player) playerIn.readObject();
					openedPlayer.hand = new PlayerCardHand();
					players.set(playerIndex, openedPlayer);
					playerIn.close();
					System.out.println(openedPlayer.getName());
				} catch (ClassNotFoundException e) {
					System.err.println(e);
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Can't open an existing player while a game is in progress.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updatePlayer(int playerIndex) {
		Player player = players.get(playerIndex);
		PlayerDialog playerDetails = new PlayerDialog(null, "Player Details", true, player);
		playerDetails.setVisible(true);

		players.set(playerIndex, playerDetails.getPlayer());
		updateValues(); // Atualiza a interface gráfica após a alteração dos jogadores
	}

	//////////////////////////////////////////////////////
	private void nextTurn() {
		currentPlayerIndex++;
		if (currentPlayerIndex >= players.size()) {
			dealerPlay();
		} else {
			dealer.setCanDouble(true);
			updateTurn();
		}
	}

	private void dealerPlay() {
		dealer.revealCards();
		updateValues();
		dealer.go(players);

		for (Player player : players) {
			String message;
			if (player.hand.getTotal() > 21
					|| (dealer.getHand().getTotal() <= 21 && dealer.getHand().getTotal() > player.hand.getTotal())) {
				message = player.getName() + " perdeu!";
			} else if (dealer.getHand().getTotal() > 21 || player.hand.getTotal() > dealer.getHand().getTotal()) {
				message = player.getName() + " ganhou!";
				player.setWallet(player.getWallet() + player.getBet() * 2); // Adiciona o valor ganho ao saldo do
																			// jogador
			} else {
				message = player.getName() + " empatou!";
				player.setWallet(player.getWallet() + player.getBet()); // Retorna a aposta ao saldo do jogador
			}
			JOptionPane.showMessageDialog(this, message);
			player.hand.clear();
			player.hand.getTotal();
			message = player.getName() + player.hand.getTotal();;
		}
		table.setGameOver(true);
		updateValues();
	}

	private void updateTurn() {
		for (int i = 0; i < players.size(); i++) {
			boolean isCurrentPlayer = (i == currentPlayerIndex);
			hitButtons.get(i).setEnabled(isCurrentPlayer);
			standButtons.get(i).setEnabled(isCurrentPlayer);
			doubleButtons.get(i).setEnabled(isCurrentPlayer && dealer.canPlayerDouble(players.get(i)));
		}
	}

	public int getCurrentPlayerID() {
		return currentPlayerIndex;
	}

	public void updatePlayerNameColor(Color color) {
		table.setPlayerNameColor(color);
		table.repaint();
	}
}
