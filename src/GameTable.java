import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import Cards.*;

public class GameTable extends JPanel {
	private DealerCardHand dealer;
	private PlayerCardHand player;
	private boolean showAllDealerCards;
	private boolean gameOver;

	// drawing position vars
	private final int CARD_INCREMENT = 20;
	private final int CARD_START = 100;
	private final int DEALER_POSITION = 50;
	private final int PLAYER_POSITION = 200;

	private final int CARD_IMAGE_WIDTH = 71;
	private final int CARD_IMAGE_HEIGHT = 96;

	private final int NAME_SPACE = 10;

	private Font handTotalFont;
	private Font playerNameFont;

	private String dealerName;
	private String playerName;

	private Color playerNameColor = Color.WHITE;

	private Image[] cardImages = new Image[CardPack.CARDS_IN_PACK + 1];
	private Image backgroundImg;

	// Variables for animation
	private int cardX = CARD_START;
	private Timer timer;
	private boolean animating = false;
	private int animationStep = 5;
	private int cardIndex = 0;

	// take game model as parameter so that it can get cards and draw them
	public GameTable() {
		super();
		this.setBackground(Color.BLUE);
		this.setOpaque(false);
		handTotalFont = new Font("Serif", Font.PLAIN, 96);
		playerNameFont = new Font("Serif", Font.ITALIC, 20);
		showAllDealerCards = true;
		gameOver = false;

		for (int i = 0; i < CardPack.CARDS_IN_PACK; i++) {
			String cardName = "/card_images/" + (i + 1) + ".png";
			URL urlImg = getClass().getResource(cardName);
			if (urlImg == null) {
				System.err.println("Imagem não encontrada: " + cardName);
				continue;
			}
			Image cardImage = Toolkit.getDefaultToolkit().getImage(urlImg);
			cardImages[i] = cardImage;
		}

		String backCard = "/card_images/red_back.png";
		URL backCardURL = getClass().getResource(backCard);
		if (backCardURL == null) {
			System.err.println("Imagem não encontrada: " + backCard);
		} else {
			Image backCardImage = Toolkit.getDefaultToolkit().getImage(backCardURL);
			cardImages[CardPack.CARDS_IN_PACK] = backCardImage;
		}

		MediaTracker imageTracker = new MediaTracker(this);
		for (int i = 0; i < CardPack.CARDS_IN_PACK + 1; i++) {
			if (cardImages[i] != null) {
				imageTracker.addImage(cardImages[i], i);
			}
		}

		// Load background image
		String backgroundName = "/images/background.png";
		URL backgroundURL = getClass().getResource(backgroundName);
		if (backgroundURL == null) {
			System.err.println("Imagem não encontrada: " + backgroundName);
		} else {
			backgroundImg = Toolkit.getDefaultToolkit().getImage(backgroundURL);
			imageTracker.addImage(backgroundImg, CardPack.CARDS_IN_PACK + 1);
		}

		try {
			imageTracker.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Timer for animation
		timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardX += animationStep;
				if (cardX >= CARD_START + CARD_INCREMENT * cardIndex) {
					animating = false;
					timer.stop();
				}
				repaint();
			}
		});

		// Adding MouseListener for interactivity
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();

				// Check if a card is clicked
				if (!gameOver && y >= PLAYER_POSITION && y <= PLAYER_POSITION + CARD_IMAGE_HEIGHT) {
					int index = (x - CARD_START) / CARD_INCREMENT;
					if (index >= 0 && index < player.size()) {
						Card clickedCard = player.get(index);
						JOptionPane.showMessageDialog(null, "You clicked on: " + clickedCard);
					}
				}
			}
		});
	}

	public void setHands(DealerCardHand dealer, PlayerCardHand player) {
		this.dealer = dealer;
		this.player = player;
		this.cardIndex = 0;
		this.cardX = CARD_START;
		animating = true;
		timer.start();
	}

	public void setShowAllDealerCards(boolean showAllDealerCards) {
		this.showAllDealerCards = showAllDealerCards;
	}

	public void setNames(String dealerName, String playerName) {
		this.dealerName = dealerName;
		this.playerName = playerName;
	}

	public void setPlayerNameColor(Color color) {
		this.playerNameColor = color;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		repaint();
	}

	public void update(DealerCardHand dealer, PlayerCardHand player, boolean showAllDealerCards) {
		setHands(dealer, player);
		setShowAllDealerCards(showAllDealerCards);
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (backgroundImg != null) {
			g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
		}

		if (gameOver) {
			return;
		}

		g.setColor(playerNameColor);
		g.setFont(playerNameFont);

		g.drawString(dealerName, CARD_START, DEALER_POSITION - NAME_SPACE);
		g.drawString(playerName, CARD_START, PLAYER_POSITION - NAME_SPACE);

		g.setFont(handTotalFont);

		// draw dealer cards
		int i = CARD_START;

		if (showAllDealerCards) {
			for (Card aCard : dealer) {
				g.drawImage(cardImages[aCard.getCode() - 1], i, DEALER_POSITION, this);
				i += CARD_INCREMENT;
			}
			g.drawString(Integer.toString(dealer.getTotal()), i + CARD_IMAGE_WIDTH + CARD_INCREMENT, DEALER_POSITION + CARD_IMAGE_HEIGHT);
		} else {
			for (Card aCard : dealer) {
				g.drawImage(cardImages[CardPack.CARDS_IN_PACK], i, DEALER_POSITION, this);
				i += CARD_INCREMENT;
			}
			try {
				Card topCard = dealer.lastElement();
				i -= CARD_INCREMENT;
				g.drawImage(cardImages[topCard.getCode() - 1], i, DEALER_POSITION, this);
				g.drawString("?", i + CARD_IMAGE_WIDTH + CARD_INCREMENT, DEALER_POSITION + CARD_IMAGE_HEIGHT);
			} catch (Exception e) {
				System.out.println("No cards have been dealt yet.");
			}
		}

		// draw player cards
		i = CARD_START;
		if (animating) {
			for (int j = 0; j < player.size(); j++) {
				if (i <= cardX) {
					g.drawImage(cardImages[player.get(j).getCode() - 1], i, PLAYER_POSITION, this);
					i += CARD_INCREMENT;
				}
			}
		} else {
			for (Card aCard : player) {
				g.drawImage(cardImages[aCard.getCode() - 1], i, PLAYER_POSITION, this);
				i += CARD_INCREMENT;
			}
			g.drawString(Integer.toString(player.getTotal()), i + CARD_IMAGE_WIDTH + CARD_INCREMENT, PLAYER_POSITION + CARD_IMAGE_HEIGHT);
		}
	}
}