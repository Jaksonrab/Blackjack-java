import java.awt.*; //builds GUI (graphical user interface)
import java.awt.event.*;
import java.util.ArrayList; 
import java.util.Random;
import javax.swing.*; //more modern GUI components



public class BlackJack {
	private class Card {
		String value;
		String type;
	
	 Card(String value, String type) {
		this.value= value;
		this.type = type;
	}
	 
	 //display deck
	 public String toString() {
		 return this.value + "-" + this.type;
	 }
	 
	 //get value of card
	 public int getValue() {
         if (value.equals("J") || value.equals("Q") || value.equals("K")) {
             return 10;
         } 
         else if (value.equals("A")) {
             return 11;
         } 
         else {
             return Integer.parseInt(value);
         }
     }
	 
	 //if it is an ace
	 public boolean isAce() {
		 return value.equals("A");
	 }
	 
	 //to get the image of the card
	 public String getImagePath() {
		 return "./cards/" + toString() + ".png";
	 }
	 }

	//building the deck
	ArrayList<Card> deck;
	Random random = new Random();
    
	//Dealer values
	Card hiddenCard;
	ArrayList<Card> dealerHand;
	int dealerSum;
	int dealerAceCount; //track aces since they can be either 1 or 11
	
	//Player values
	Card playerCard;
	ArrayList<Card> playerHand;
	int playerSum;
	int playerAceCount; //same as dealer
	
	//window
	int boardWidth = 600;
	int boardHeight = 600;
	
	int cardWidth = 110;
	int cardHeight = 154;
	
	JFrame frame = new JFrame("Black Jack"); //making a frame with title "Black Jack"
	
	JPanel gamePanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			try {
			
			//draw hidden card
			Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
			
			if(!stayButton.isEnabled()) { //if stopped hitting, disable stay button 
				hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
			}
			g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight,null);
			
			//draw dealer hand
			for(int i = 0; i< dealerHand.size();i++) {
				Card card = dealerHand.get(i);
				Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
				g.drawImage(cardImg, cardWidth+25+(cardWidth+5)*i, 20, cardWidth, cardHeight,null);
			}
			
			//draw player hand 
			for(int i = 0; i< playerHand.size();i++) {
				Card card = playerHand.get(i);
				Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
				g.drawImage(cardImg, 20 +(cardWidth+5)*i, 360, cardWidth, cardHeight,null);
			
			}
			
			if(!stayButton.isEnabled()) {
				dealerSum = reducedDealerAce();
				playerSum = reducedPlayerAce();
				
				 
				String message= "";
				
				if(playerSum> 21) {
					message = "You Lose!";
				}
				
				else if (dealerSum>21) {
					message = "You Win!";
				}
				
				else if (dealerSum == playerSum) {
					message = "You Tie!";
				}
				
				else if(dealerSum>playerSum) {
					message = "You Lose!";
				}
				else if(dealerSum < playerSum){
					message = "You Win";
				}
				
				//print out the message
				g.setFont(new Font("Arial", Font.PLAIN,30));
				g.setColor(Color.white);
				g.drawString(message, 220, 250);
				}
			
			}
			catch (Exception e) {
				e.printStackTrace();
				
			}
			}
	};
	JPanel buttonPanel = new JPanel();
	JButton hitButton = new JButton("Hit");
	JButton stayButton = new JButton("Stay"); 
	JButton replayButton = new JButton("Restart");
	

	BlackJack(){
		startGame();
		
		frame.setVisible(true); //make window visible
		frame.setSize(boardWidth, boardHeight); //dimensions
		frame.setLocationRelativeTo(null); //opens window in center of screen
		frame.setResizable(true); //can resize
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closing window terminates program
		
		gamePanel.setLayout(new BorderLayout()); //divides the panel into 5 regions
		gamePanel.setBackground(new Color(0,128,0)); 
		frame.add(gamePanel); //adds the game panel to the frame
		
		//adding the buttons to the layout
		hitButton.setFocusable(false);
		stayButton.setFocusable(false);
		replayButton.setFocusable(false);
		
		replayButton.setEnabled(false);
		
		buttonPanel.add(hitButton);
		buttonPanel.add(stayButton);
		buttonPanel.add(replayButton);
	
		frame.add(buttonPanel,BorderLayout.SOUTH); //adds the button panel to the bottom
		
		//hit button
		hitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Card card = deck.remove(deck.size()-1);
				playerSum += card.getValue();
				playerAceCount += card.isAce()? 1:0;
				playerHand.add(card);
				
				//if card value greater than or equal to 21 with aces reduced, disable hitting
				if (reducedPlayerAce() >= 21) { 
					hitButton.setEnabled(false);
					stayButton.doClick();
				}
			gamePanel.repaint(); //updates the graphics
			}
		});
		
		//stay button 
		stayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hitButton.setEnabled(false); //can't hit after clicked stay
				stayButton.setEnabled(false);
				
				//dealer hits when under 17
				while(dealerSum < 17) {
					Card card = deck.remove(deck.size()-1);
					dealerSum += card.getValue();
					dealerAceCount += card.isAce()? 1:0;
					dealerHand.add(card);
				}
				
				replayButton.setEnabled(true);
				
				gamePanel.repaint();
				
			}
			
		});
		
		//replay button
		replayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(!stayButton.isEnabled() && !hitButton.isEnabled()){
					startGame();
					replayButton.setEnabled(false); 
					stayButton.setEnabled(true); 
					hitButton.setEnabled(true); 
					gamePanel.repaint(); 
				}}
		});
		gamePanel.repaint(); 
	}
	
	//starting the game by dealing out two cards to both players and building deck
	public void startGame() {
		
		//making the deck and shuffling it
		buildDeck();
		shuffleDeck();
		
//		System.out.println(deck);
		
		//default dealer hand
		dealerHand = new ArrayList<Card>();
		dealerSum = 0;
		dealerAceCount =0;
		
		//dealers hidden card
		hiddenCard = deck.remove(deck.size()-1);
		dealerSum += hiddenCard.getValue(); 
		dealerAceCount += hiddenCard.isAce()? 1:0;
		
		//dealers up card
		Card upCard = deck.remove(deck.size()-1);
		dealerHand.add(upCard);
		dealerSum += upCard.getValue(); 
		dealerAceCount += upCard.isAce() ? 1:0;
		

		//player
		playerHand = new ArrayList<Card>();
		playerSum = 0;
		playerAceCount = 0;
		
		for(int i =0; i<2; i++) {
			Card playerCard = deck.remove(deck.size()-1);
			playerSum += playerCard.getValue();
			playerAceCount += playerCard.isAce() ? 1:0;
			playerHand.add(playerCard);
		}
		

		}
	
	public void buildDeck() {
		deck = new ArrayList<Card>();
		String[] values = {"A", "2","3","4","5","6","7","8","9","10","J","Q","K"};
		String[] types = {"C","D","H","S"};
		
		for(int i = 0; i<types.length; i++) {
			for(int j =0; j< values.length; j++) {
				Card curr = new Card(values[j], types[i]);
				deck.add(curr);
			}
		}
		
	}
	
	public void shuffleDeck() {
		for(int i = 0; i<deck.size(); i++) { //swaps two cards
			int j = random.nextInt(deck.size()); //get a random int from 0 to deck size
			
			//getting the two cards
			Card currCard = deck.get(i);
			Card randomCard = deck.get(j);
			
			//swapping the two cards
			deck.set(i, randomCard);
			deck.set(j, currCard);
			

		}

	}
	
	public int reducedPlayerAce() {
		while(playerSum> 21 && playerAceCount>0) {
			playerSum -= 10;
			playerAceCount -=1;
		}
		return playerSum;
	}
	
	public int reducedDealerAce() {
		while(dealerSum> 21 && dealerAceCount>0) {
			dealerSum -= 10;
			dealerAceCount -=1;
		}
		return dealerSum;
	}
	
	
}
