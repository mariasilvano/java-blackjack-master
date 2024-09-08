import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Application window. Holds the menu-bar etc.
 *
 * @autor David Winter
 */
public class AppWindow extends JFrame implements ActionListener, ComponentListener {
	private GamePanel gamePanel;
	private Color defaultTableColour = new Color(6, 120, 0);

	private JMenuItem savePlayer = new JMenuItem("Save Current Player");
	private JMenuItem openPlayer = new JMenuItem("Open Existing Player");

	private JMenuItem updatePlayerDetails;
	private JMenuItem dealAction;
	private JMenuItem hitAction;
	private JMenuItem doubleAction;
	private JMenuItem standAction;
	private JMenuItem oneChip;
	private JMenuItem fiveChip;
	private JMenuItem tenChip;
	private JMenuItem twentyFiveChip;
	private JMenuItem hundredChip;
	private JMenuItem windowTableColourMenu;
	private JMenuItem helpBlackjackRulesMenu;
	private JMenuItem helpAboutMenu;

	final int WIDTH = 1200;
	final int HEIGHT = 500;

	public AppWindow() {
		super("Blackjack");

		configureWindow();
		setupMenus();
		setupKeyboardShortcuts();
		setupActionListeners();
		initializeComponents();

		setVisible(true);
	}

	private void configureWindow() {
		addComponentListener(this);

		Dimension windowSize = new Dimension(WIDTH, HEIGHT);
		setSize(windowSize);
		setMinimumSize(windowSize); // Define o tamanho mínimo
		setLocationRelativeTo(null); // Coloca o jogo no centro da tela

		getContentPane().setBackground(defaultTableColour);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setupMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu playerMenu = new JMenu("Player");
		updatePlayerDetails = new JMenuItem("Update Player Details");
		playerMenu.add(updatePlayerDetails);
		playerMenu.addSeparator();
		playerMenu.add(savePlayer);
		playerMenu.add(openPlayer);
		menuBar.add(playerMenu);

		JMenu actionMenu = new JMenu("Actions");
		dealAction = new JMenuItem("Deal");
		hitAction = new JMenuItem("Hit");
		doubleAction = new JMenuItem("Double");
		standAction = new JMenuItem("Stand");
		actionMenu.add(dealAction);
		actionMenu.add(hitAction);
		actionMenu.add(doubleAction);
		actionMenu.add(standAction);
		menuBar.add(actionMenu);

		JMenu betMenu = new JMenu("Bet");
		oneChip = new JMenuItem("Add 1 Chip");
		fiveChip = new JMenuItem("Add 5 Chips");
		tenChip = new JMenuItem("Add 10 Chips");
		twentyFiveChip = new JMenuItem("Add 25 Chips");
		hundredChip = new JMenuItem("Add 100 Chips");
		betMenu.add(oneChip);
		betMenu.add(fiveChip);
		betMenu.add(tenChip);
		betMenu.add(twentyFiveChip);
		betMenu.add(hundredChip);
		menuBar.add(betMenu);

		JMenu windowMenu = new JMenu("Window");
		windowTableColourMenu = new JMenuItem("Change Table Colour");
		windowMenu.add(windowTableColourMenu);
		menuBar.add(windowMenu);

		JMenu helpMenu = new JMenu("Help");
		helpBlackjackRulesMenu = new JMenuItem("Blackjack Rules");
		helpAboutMenu = new JMenuItem("About");
		helpMenu.add(helpBlackjackRulesMenu);
		helpMenu.add(helpAboutMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		hitAction.setEnabled(false);
		doubleAction.setEnabled(false);
		standAction.setEnabled(false);
	}

	private void setupKeyboardShortcuts() {
		// Setup keyboard shortcuts
	}

	private void setupActionListeners() {
		savePlayer.addActionListener(this);
		openPlayer.addActionListener(this);
		updatePlayerDetails.addActionListener(this);
		dealAction.addActionListener(this);
		hitAction.addActionListener(this);
		doubleAction.addActionListener(this);
		standAction.addActionListener(this);
		oneChip.addActionListener(this);
		fiveChip.addActionListener(this);
		tenChip.addActionListener(this);
		twentyFiveChip.addActionListener(this);
		hundredChip.addActionListener(this);
		windowTableColourMenu.addActionListener(this);
		helpBlackjackRulesMenu.addActionListener(this);
		helpAboutMenu.addActionListener(this);
	}

	private void initializeComponents() {
		gamePanel = new GamePanel();
		gamePanel.setBackground(defaultTableColour);
		updatePlayerNameColor(); // Update player name color based on the initial background color
		add(gamePanel, BorderLayout.CENTER);
	}

	private void enableButton() {
		hitAction.setEnabled(true);
		doubleAction.setEnabled(true);
		standAction.setEnabled(true);
	}

	private void disableButton() {
		hitAction.setEnabled(false);
		doubleAction.setEnabled(false);
		standAction.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int playerID = gamePanel.getCurrentPlayerID();

		if (e.getSource() == oneChip) {
			gamePanel.increaseBet(playerID, Chip.one().getValue());
			enableButton();
		} else if (e.getSource() == fiveChip) {
			gamePanel.increaseBet(playerID, Chip.five().getValue());
			enableButton();
		} else if (e.getSource() == tenChip) {
			gamePanel.increaseBet(playerID, Chip.ten().getValue());
			enableButton();
		} else if (e.getSource() == twentyFiveChip) {
			gamePanel.increaseBet(playerID, Chip.twentyFive().getValue());
			enableButton();
		} else if (e.getSource() == hundredChip) {
			gamePanel.increaseBet(playerID, Chip.hundred().getValue());
			enableButton();
		} else if (e.getSource() == dealAction) {
			gamePanel.newGame();
			enableButton();
		} else if (e.getSource() == hitAction) {
			gamePanel.hit(playerID);
			enableButton();
		} else if (e.getSource() == doubleAction) {
			gamePanel.playDouble(playerID);
			enableButton();
		} else if (e.getSource() == standAction) {
			gamePanel.stand(playerID);
			enableButton();
		} else if (e.getSource() == updatePlayerDetails) {
			updatePlayerDetailsAction();
			enableButton();
		} else if (e.getSource() == savePlayer) {
			gamePanel.savePlayer(playerID);
			enableButton();
		} else if (e.getSource() == openPlayer) {
			gamePanel.openPlayer(playerID);
			enableButton();
		} else if (e.getSource() == windowTableColourMenu) {
			changeTableColour();
			enableButton();
		} else if (e.getSource() == helpBlackjackRulesMenu) {
			showBlackjackRules();
			enableButton();
		} else if (e.getSource() == helpAboutMenu) {
			showAboutInfo();
			enableButton();
		}
	}

	private void updatePlayerDetailsAction() {
		String[] playerOptions = { "Player 1", "Player 2" };
		int playerIndex = JOptionPane.showOptionDialog(this, "Select the player to update", "Update Player",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, playerOptions, playerOptions[0]);

		if (playerIndex >= 0) {
			gamePanel.updatePlayer(playerIndex);
		}
	}

	private void changeTableColour() {
		Color newColor = JColorChooser.showDialog(this, "Choose Table Colour", defaultTableColour);
		if (newColor != null) {
			defaultTableColour = newColor;
			getContentPane().setBackground(defaultTableColour);
			gamePanel.setBackground(defaultTableColour);
			updatePlayerNameColor(); // Update player name color based on the new background color
		}
	}

	private void updatePlayerNameColor() {
		Color textColor = getContrastingColor(defaultTableColour);
		gamePanel.updatePlayerNameColor(textColor); // Assuming GamePanel has a method to update player name color
	}

	private Color getContrastingColor(Color color) {
		int d = 0;
		double a = 1 - (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
		if (a < 0.5) {
			d = 0; // Bright colors - use black
		} else {
			d = 255; // Dark colors - use white
		}
		return new Color(d, d, d);
	}

	private void showBlackjackRules() {
		JOptionPane.showMessageDialog(this,
				"Blackjack Rules: \n1. The goal is to beat the dealer's hand without going over 21.\n2. Face cards are worth 10. Aces are worth 1 or 11, whichever makes a better hand.\n3. Each player starts with two cards, one of the dealer's cards is hidden until the end.\n4. To 'Hit' is to ask for another card. To 'Stand' is to hold your total and end your turn.\n5. If you go over 21 you bust, and the dealer wins regardless of the dealer's hand.\n6. If you are dealt 21 from the start (Ace & 10), you got a blackjack.",
				"Blackjack Rules", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showAboutInfo() {
		JOptionPane.showMessageDialog(this, "Blackjack Game\nVersion 1.0\nAuthor: David Winter", "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void componentResized(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}
}
