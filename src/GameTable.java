import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import Cards.*;
import java.util.ArrayList;

public class GameTable extends JPanel {
    private DealerCardHand dealer;
    private ArrayList<PlayerCardHand> players;
    private boolean showAllDealerCards;
    private boolean gameOver;

    // drawing position vars
    private final int CARD_INCREMENT = 20;
    private final int INITIAL_CARD_POSITION = 100;
    private final int DEALER_POSITION = 50;

    private final int CARD_IMAGE_WIDTH = 71;
    private final int CARD_IMAGE_HEIGHT = 96;

    private final int NAME_SPACE = 10;

    private Font handTotalFont;
    private Font playerNameFont;

    private String dealerName;
    private ArrayList<String> playerNames;

    private Color playerNameColor = Color.WHITE;

    private Image[] cardImages = new Image[CardPack.CARDS_IN_PACK + 1];
    private Image backgroundImg;

    // Variables for animation
    private int cardX = INITIAL_CARD_POSITION;
    private Timer timer;
    private boolean animating = false;
    private int animationStep = 5;
    private int cardIndex = 0;

    // take game model as parameter so that it can get cards and draw them
    public GameTable() {
        super();
        this.setBackground(Color.GREEN);
        this.setOpaque(false);
        handTotalFont = new Font("Serif", Font.PLAIN, 96);
        playerNameFont = new Font("Serif", Font.ITALIC, 20);
        showAllDealerCards = true;
        gameOver = false;

        players = new ArrayList<>();
        playerNames = new ArrayList<>();

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

        /*
         * Load background image String backgroundName = "/images/background.png"; URL
         * backgroundURL = getClass().getResource(backgroundName); if (backgroundURL ==
         * null) { System.err.println("Imagem não encontrada: " + backgroundName); }
         * else { backgroundImg = Toolkit.getDefaultToolkit().getImage(backgroundURL);
         * imageTracker.addImage(backgroundImg, CardPack.CARDS_IN_PACK + 1); }
         */

        try {
            imageTracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Timer for animation
        timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardX += animationStep;
                if (cardX >= INITIAL_CARD_POSITION + CARD_INCREMENT * cardIndex) {
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
                if (!gameOver && y >= getPlayerPositionY(0) && y <= getPlayerPositionY(0) + CARD_IMAGE_HEIGHT) {
                    int index = (x - INITIAL_CARD_POSITION) / CARD_INCREMENT;
                    if (index >= 0 && index < players.get(0).size()) {
                        Card clickedCard = players.get(0).get(index);
                        JOptionPane.showMessageDialog(null, "You clicked on: " + clickedCard);
                    }
                }
            }
        });
    }

    private int getPlayerPositionY(int playerIndex) {
        return getHeight() - 150;
    }

    public void setHands(DealerCardHand dealer, ArrayList<PlayerCardHand> players) {
        this.dealer = dealer;
        this.players = players;
        this.cardIndex = 0;
        this.cardX = INITIAL_CARD_POSITION;
        animating = true;
        timer.start();
    }

    public void setShowAllDealerCards(boolean showAllDealerCards) {
        this.showAllDealerCards = showAllDealerCards;
    }

    public void setNames(String dealerName, ArrayList<String> playerNames) {
        this.dealerName = dealerName;
        this.playerNames = playerNames;
    }

    public void setPlayerNameColor(Color color) {
        this.playerNameColor = color;
        repaint();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        repaint();
    }

    public void startCardAnimation(int index) {
        cardIndex = index;
        cardX = INITIAL_CARD_POSITION;
        animating = true;
        timer.start();
    }

    public void update(DealerCardHand dealer, ArrayList<PlayerCardHand> players, boolean showAllDealerCards) {
        setHands(dealer, players);
        setShowAllDealerCards(showAllDealerCards);
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }

        g.setColor(playerNameColor);
        g.setFont(playerNameFont);

        g.drawString(dealerName, getWidth() / 2 - dealerName.length() * 5, DEALER_POSITION - NAME_SPACE);

        if (playerNames.size() > 0) {
            int leftPlayerX = 75;
            int rightPlayerX = getWidth() - 175;
            int playerY = getPlayerPositionY(0);

            // Essa parte mostra o nome do player 1

            String playerName = playerNames.get(0);
            String[] nameParts = playerName.split(" ");
            StringBuilder line = new StringBuilder();
            int lineHeight = g.getFontMetrics().getHeight();
            int linesCount = 0;
            int wordCount = 0;

            for (int i = 0; i < nameParts.length; i++) {
                if (wordCount < 2) {
                    wordCount++;
                } else {
                    linesCount++;
                    wordCount = 1;
                }
            }
            if (wordCount > 0) {
                linesCount++;
            }

            int yPosition = playerY - NAME_SPACE - (linesCount - 1) * lineHeight;

            wordCount = 0;

            for (int i = 0; i < nameParts.length; i++) {
                if (wordCount < 2) {
                    if (line.length() > 0) {
                        line.append(" ");
                    }
                    line.append(nameParts[i]);
                    wordCount++;
                } else {
                    g.drawString(line.toString(), leftPlayerX, yPosition);
                    yPosition += lineHeight;
                    line.setLength(0);
                    line.append(nameParts[i]);
                    wordCount = 1;
                }
            }

            if (line.length() > 0) {
                g.drawString(line.toString(), leftPlayerX, yPosition);
            }
            // Essa parte mostra o total de pontos do player 1
            g.drawString(Integer.toString(players.get(0).getTotal()), leftPlayerX + 20,
                    playerY + CARD_IMAGE_HEIGHT + 20);

            if (playerNames.size() > 1) {

                // Essa parte mostra o nome do player 2
                String playerName2 = playerNames.get(1);

                String[] nameParts2 = playerName2.split(" ");
                StringBuilder line2 = new StringBuilder();
                int linesCount2 = 0;
                int wordCount2 = 0;

                for (int i = 0; i < nameParts2.length; i++) {
                    if (wordCount2 < 2) {
                        wordCount2++;
                    } else {
                        linesCount2++;
                        wordCount2 = 1;
                    }
                }
                if (wordCount2 > 0) {
                    linesCount2++;
                }

                int yPosition2 = playerY - NAME_SPACE - (linesCount2 - 1) * lineHeight;

                wordCount2 = 0;

                for (int i = 0; i < nameParts2.length; i++) {
                    if (wordCount2 < 2) {
                        if (line2.length() > 0) {
                            line2.append(" ");
                        }
                        line2.append(nameParts2[i]);
                        wordCount2++;
                    } else {
                        g.drawString(line2.toString(), rightPlayerX, yPosition2);
                        yPosition2 += lineHeight;
                        line2.setLength(0);
                        line2.append(nameParts2[i]);
                        wordCount2 = 1;
                    }
                }

                if (line2.length() > 0) {
                    g.drawString(line2.toString(), rightPlayerX, yPosition);
                }

                // Essa parte mostra o total de pontos do player 2
                g.drawString(Integer.toString(players.get(1).getTotal()), rightPlayerX + 20,
                        playerY + CARD_IMAGE_HEIGHT + 20);

                if (gameOver) {
                    return;
                }

                g.setFont(handTotalFont);

                // draw dealer cards
                int dealerStartX = (getWidth() - (CARD_IMAGE_WIDTH + CARD_INCREMENT) * dealer.size()) / 2;
                if (showAllDealerCards) {
                    for (Card aCard : dealer) {
                        g.drawImage(cardImages[aCard.getCode() - 1], dealerStartX, DEALER_POSITION, this);
                        dealerStartX += CARD_INCREMENT;
                    }
                    g.drawString(Integer.toString(dealer.getTotal()), dealerStartX + CARD_IMAGE_WIDTH + CARD_INCREMENT,
                            DEALER_POSITION + CARD_IMAGE_HEIGHT);
                } else {
                    for (Card aCard : dealer) {
                        g.drawImage(cardImages[CardPack.CARDS_IN_PACK], dealerStartX, DEALER_POSITION, this);
                        dealerStartX += CARD_INCREMENT;
                    }
                    try {
                        Card topCard = dealer.lastElement();
                        dealerStartX -= CARD_INCREMENT;
                        g.drawImage(cardImages[topCard.getCode() - 1], dealerStartX, DEALER_POSITION, this);
                        g.drawString("?", dealerStartX + CARD_IMAGE_WIDTH + CARD_INCREMENT,
                                DEALER_POSITION + CARD_IMAGE_HEIGHT);
                    } catch (Exception e) {
                        System.out.println("No cards have been dealt yet.");
                    }
                }

                // draw player cards
                int playerStartX = leftPlayerX;
                for (Card aCard : players.get(0)) {
                    g.drawImage(cardImages[aCard.getCode() - 1], playerStartX, playerY, this);
                    playerStartX += CARD_INCREMENT;
                }

                playerStartX = rightPlayerX;
                if (players.size() > 1) {
                    for (Card aCard : players.get(1)) {
                        g.drawImage(cardImages[aCard.getCode() - 1], playerStartX, playerY, this);
                        playerStartX += CARD_INCREMENT;
                    }
                }
            }
        }
    }
}