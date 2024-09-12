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

	private ArrayList<String> dealerHistory;

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
	private JButton historyButton;
	private HistoryFrame historyFrame;

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
		dealerHistory = new ArrayList<>();
		historyFrame = null;

		dealer = new Dealer();
		players = new ArrayList<>();

		// Adicionando jogadores
		players.add(new Player(new PersonInfo("James Bond", 32, "Male")));
		players.add(new Player(new PersonInfo("Ethan Hunt", 35, "Male")));

		for (Player player : players) {
			player.setWallet(100.00);
		}

		currentPlayerIndex = 0;

		// Painel do Dealer e informações do topo
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(Color.BLACK);
		topPanel.add(dealerSays, BorderLayout.NORTH);
		topPanel.add(cardsLeft, BorderLayout.SOUTH);
		dealerSays.setForeground(Color.WHITE);
		cardsLeft.setForeground(Color.WHITE);

		resetButton = new JButton("Reset Game");
		resetButton.addActionListener(this);

		add(topPanel, BorderLayout.NORTH);

		historyButton = new JButton("History");
		historyButton.addActionListener(e -> showHistory());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setBackground(Color.BLACK);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 10); // Espaçamento à direita

		// Adicione o botão Reset
		resetButton.setPreferredSize(new Dimension(100, 30));
		gbc.gridx = 0;
		gbc.gridy = 0;
		buttonPanel.add(resetButton, gbc);

		// Adicione o botão Histórico
		historyButton.setPreferredSize(new Dimension(100, 30));
		gbc.gridx = 1;
		gbc.gridy = 0;
		buttonPanel.add(historyButton, gbc);

		topPanel.add(buttonPanel, BorderLayout.CENTER);

		// Painel inferior com os botões de ação e o botão Deal
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(Color.DARK_GRAY);

		// Painel para os botões do jogador 1 (lado esquerdo)
		JPanel leftPlayerPanel = new JPanel(new GridLayout(2, 1));
		leftPlayerPanel.setBackground(Color.DARK_GRAY);
		addPlayerControls(leftPlayerPanel, 0);

		// Painel para os botões do jogador 2 (lado direito)
		JPanel rightPlayerPanel = new JPanel(new GridLayout(2, 1));
		rightPlayerPanel.setBackground(Color.DARK_GRAY);
		addPlayerControls(rightPlayerPanel, 1);

		// Painel do botão Deal (centralizado)
		JPanel dealPanel = new JPanel();
		dealPanel.setBackground(Color.DARK_GRAY);
		JButton dealButton = createButton("Deal", newGameButtons);
		dealPanel.add(dealButton);
		dealButton.setEnabled(dealer.isGameOver());

		bottomPanel.add(leftPlayerPanel, BorderLayout.WEST);
		bottomPanel.add(dealPanel, BorderLayout.CENTER);
		bottomPanel.add(rightPlayerPanel, BorderLayout.EAST);

		add(bottomPanel, BorderLayout.SOUTH);

		updateValues();
	}

	private void addPlayerControls(JPanel panel, int playerIndex) {
		// Criar painel de controle de apostas e ações para o jogador
		JPanel betPanel = new JPanel();
		betPanel.setBackground(Color.DARK_GRAY);

		JLabel currentBet = new JLabel("Aposta: ");
		currentBet.setForeground(Color.WHITE);
		JLabel playerWallet = new JLabel("Saldo: ");
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

		JPanel actionPanel = new JPanel();
		actionPanel.setBackground(Color.DARK_GRAY);

		actionPanel.add(createButton("Hit", hitButtons));
		actionPanel.add(createButton("Double", doubleButtons));
		actionPanel.add(createButton("Stand", standButtons));
		actionPanel.add(createButton("-1", reduce1BetButtons));
		actionPanel.add(createButton("-10", reduce10BetButtons));

		panel.add(betPanel);
		panel.add(actionPanel);
	}

	private void resetGame() {
		dealer.setGameOver(true);
		table.setGameOver(true);
		for (int i = 0; i < players.size(); i++) {
			players.get(i).clearHand();
			clearBet(i);
		}
		dealerHistory.clear();
		updateValues();

		if (historyFrame != null) {
			historyFrame.dispose(); // Fechar a janela do histórico
			historyFrame = null; // Definir a instância como null
		}
	}

	private void showHistory() {
		if (historyFrame == null || !historyFrame.isVisible()) {
			historyFrame = new HistoryFrame(dealerHistory);
			historyFrame.setVisible(true);
		} else {
			historyFrame.updateHistory();
		}
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

		int playerIndex = getPlayerIndex(source); // Obtenha o índice do jogador

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
			if (hitButtons.get(i) == source || doubleButtons.get(i) == source || standButtons.get(i) == source
					|| clearBetButtons.get(i) == source || allInButtons.get(i) == source
					|| add1ChipButtons.get(i) == source || add5ChipButtons.get(i) == source
					|| add10ChipButtons.get(i) == source || add25ChipButtons.get(i) == source
					|| add100ChipButtons.get(i) == source || reduce1BetButtons.get(i) == source
					|| reduce10BetButtons.get(i) == source) {
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
		Player player = players.get(playerIndex);
		dealer.playerStand(player);
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
		dealer.clearBetMessage(player);
		updateValues();
	}

	public void allInBet(int playerIndex) {
		Player player = players.get(playerIndex);
		player.allIn();
		dealer.allInMessage(player);
		updateValues();
	}

	private String lastDealerMessage = "";

	public void updateValues() {
		Color colorText = Color.WHITE;

		String dealerMessage = dealer.says();

		if (!dealerMessage.equals(lastDealerMessage)) {
			dealerHistory.add(dealerMessage);
			lastDealerMessage = dealerMessage; // Atualizando com a última mensagem adicionada ao histórico
		}

		dealerSays.setText("<html><p align=\"center\"><font face=\"Serif\" color=\"white\" style=\"font-size: 20pt\">"
				+ dealerMessage + "</font></p></html>");

		// Atualiza o histórico na janela de histórico se ela estiver aberta
		if (historyFrame != null) {
			historyFrame.updateHistory();
		}

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);

			doubleButtons.get(i).setEnabled(dealer.isPlayerEligibleForDouble(player)
					&& dealer.isPlayerEligibleForAction(i, currentPlayerIndex));
			hitButtons.get(i).setEnabled(dealer.isPlayerEligibleForAction(i, currentPlayerIndex));
			standButtons.get(i).setEnabled(dealer.isPlayerEligibleForAction(i, currentPlayerIndex));
			clearBetButtons.get(i).setEnabled(dealer.isGameOver() && player.betPlaced());
			allInButtons.get(i).setEnabled(dealer.isGameOver() && player.getWallet() >= 1.0);
			add1ChipButtons.get(i).setEnabled(dealer.isWalletSufficientForChip(player, 1));
			add5ChipButtons.get(i).setEnabled(dealer.isWalletSufficientForChip(player, 5));
			add10ChipButtons.get(i).setEnabled(dealer.isWalletSufficientForChip(player, 10));
			add25ChipButtons.get(i).setEnabled(dealer.isWalletSufficientForChip(player, 25));
			add100ChipButtons.get(i).setEnabled(dealer.isWalletSufficientForChip(player, 100));
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
		JOptionPane.showMessageDialog(null, player.getName() + " went bankrupt. To receive a new balance amount, click ok to continue", "Out of funds", JOptionPane.INFORMATION_MESSAGE);

		player.setWallet(100.00);
		updateValues();
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
