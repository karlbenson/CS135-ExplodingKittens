import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class gameWindow {

	JFrame frame;
	JTextField status;
	JButton diffuseBtn;
	JButton dropBtn;
	JTextArea console;
	JPanel cardPane;
	JList playerList;
	JLabel drawPile;
	JTextField timer;
	JTextField diffuseCtr;
	JTextField explodesign;
	JTextArea chatsms;
	JLabel discard;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gameWindow window = new gameWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gameWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("CMSC 135: EXPLODING KITTENS");
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setResizable(false);
		frame.setBounds(100, 100, 1020, 720);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);
		
		playerList = new JList();
		playerList.setBounds(744, 57, 260, 217);
		frame.getContentPane().add(playerList);
		
		JScrollPane consoleHist = new JScrollPane();
		consoleHist.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consoleHist.setBounds(744, 306, 260, 306);
		frame.getContentPane().add(consoleHist);
		
		console = new JTextArea();
		console.setEditable(false);
		console.setLineWrap(true);
		consoleHist.setViewportView(console);
		
		status = new JTextField();
		status.setEditable(false);
		status.setBounds(18, 641, 539, 26);
		frame.getContentPane().add(status);
		status.setColumns(10);
		
		dropBtn = new JButton("DROP");
		dropBtn.setBackground(new Color(204, 153, 102));
		dropBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		dropBtn.setBounds(567, 624, 165, 60);
		frame.getContentPane().add(dropBtn);
		
		timer = new JTextField();
		timer.setEditable(false);
		timer.setForeground(Color.LIGHT_GRAY);
		timer.setFont(new Font("Impact", Font.PLAIN, 36));
		timer.setHorizontalAlignment(SwingConstants.CENTER);
		timer.setText("00:00:00");
		timer.setBackground(Color.GRAY);
		timer.setBounds(16, 426, 161, 74);
		frame.getContentPane().add(timer);
		timer.setColumns(10);
		
		JScrollPane playingCards = new JScrollPane();
		playingCards.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		playingCards.setBounds(191, 430, 543, 182);
		frame.getContentPane().add(playingCards);
		
		cardPane = new JPanel();
		playingCards.setViewportView(cardPane);
		cardPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		/*JLabel[] card = new JLabel[8];
		for(int i=0; i<btns.length; i++) {
			card[i]=new JLabel(Integer.toString(i));
			card[i].setPreferredSize(new Dimension(130,172));
			cardPane.add(card[i]);
		}*/
		
		diffuseCtr = new JTextField();
		diffuseCtr.setEditable(false);
		diffuseCtr.setFont(new Font("Impact", Font.PLAIN, 14));
		diffuseCtr.setHorizontalAlignment(SwingConstants.CENTER);
		diffuseCtr.setText("0");
		diffuseCtr.setBounds(151, 585, 26, 26);
		frame.getContentPane().add(diffuseCtr);
		diffuseCtr.setColumns(10);
		
		diffuseBtn = new JButton("DIFFUSE!");
		diffuseBtn.setBackground(Color.WHITE);
		diffuseBtn.setBounds(16, 504, 125, 107);
		frame.getContentPane().add(diffuseBtn);
		
		JPanel panel = new JPanel();
		panel.setBounds(16, 30, 716, 384);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		drawPile = new JLabel("Draw Card",JLabel.CENTER);
		drawPile.setBounds(229, 102, 117, 160);
		ImageIcon origimg=new ImageIcon(getClass().getResource("img/back.png"));
		Image scaled=origimg.getImage();
		scaled=scaled.getScaledInstance(drawPile.getWidth(), drawPile.getHeight(), Image.SCALE_SMOOTH);
		drawPile.setIcon(new ImageIcon(scaled));
		drawPile.setText("");
		panel.add(drawPile);
		drawPile.validate();
		
		discard = new JLabel("Discard Pile");
		discard.setHorizontalAlignment(SwingConstants.CENTER);
		discard.setBounds(358, 101, 117, 160);
		discard.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		panel.add(discard);
		
		
		JLabel lblNewLabel_1 = new JLabel("C O N S O L E ");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(744, 285, 260, 16);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel = new JLabel("P L A Y E R S");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(744, 30, 260, 16);
		frame.getContentPane().add(lblNewLabel);
		
		explodesign = new JTextField();
		explodesign.setBackground(Color.GREEN);
		explodesign.setEditable(false);
		explodesign.setHorizontalAlignment(SwingConstants.CENTER);
		explodesign.setFont(new Font("Impact", Font.PLAIN, 14));
		explodesign.setColumns(10);
		explodesign.setBounds(151, 504, 26, 26);
		frame.getContentPane().add(explodesign);
		
		JScrollPane chatPane = new JScrollPane();
		chatPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatPane.setBounds(744, 624, 260, 56);
		frame.getContentPane().add(chatPane);
		
		chatsms = new JTextArea();
		chatsms.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				chatsms.setForeground(Color.GRAY);
				chatsms.setText("enter text here...");
			}
		});
		chatsms.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(chatsms.getText().contentEquals("enter text here...")) {
					chatsms.setText("");
					chatsms.setForeground(Color.BLACK);
				}
			}
		});
		chatsms.setText("enter text here...");
		chatsms.setForeground(Color.GRAY);
		chatPane.setViewportView(chatsms);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 1014, 21);
		frame.getContentPane().add(menuBar);
		
		JMenu mnAbout = new JMenu("Help");
		menuBar.add(mnAbout);
		
		JMenuItem mntmGameRules = new JMenuItem("Game Rules");
		mntmGameRules.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "\nIf you explode, you lose. if you don't explode, you win.\n\n"
						+ "Each player is given 8 starting cards, these cards will\nlessen your chances of getting exploded by exploding\nkittens.\n\n"
						+ "In taking your turn, you can either drop a card on the\ndiscard pile, and following the instructions on the card\nor play no cards at all."
						+ "End your turn by drawing a card\nfrom the draw pile.");
			}
		});
		mnAbout.add(mntmGameRules);
		
		JMenuItem mntmCards = new JMenuItem("Card Guide");
		mntmCards.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "\nExploding - You must show this card immediately. Unless you\nhave a Defuse Card, you’re dead. Discard all of your cards,"
						+ "\nincluding the Exploding Kitten.\n\nDefuse - If you drew an Exploding Kitten, you can play this card\ninstead of dying. Place your Defuse Card in the Discard "
						+ "Pile.\nIf you drew an Exploding Kitten, you can play this card instead\nof dying. Place your Defuse Card in the Discard Pile.\n\nAttack Related - For Double "
						+ "Slap, end your turn without drawing\na card and force any other player to take 2 turns in a row.\nTriple Slap works the same way as Double Slap, but adds 3"
						+ "\nturns instead of 2.\n\nSkip - Immediately end your turn without drawing a card.\n\nShuffle - Shuffle the Draw Pile thoroughly.\n\nDraw From the Bottom - End your turn by drawing the bottom\ncard from the Draw Pile.\n\nFavor - Force any "
						+ "other player to give you 1 card from their\nhand.\n\nSee the Future - Privately view the top 3 cards from the Draw\nPile "
						+ "and put them back in the same order.\n\nAlter the Future - Privately view the top 3 cards from the Draw\nPile and rearrange them in any order you’d like.\n\n"
						+ "Cats - These cards have no instructions on them and are\npowerless.\n\n");
			}
		});
		mnAbout.add(mntmCards);
		
		JSeparator separator = new JSeparator();
		mnAbout.add(separator);
		
		JMenuItem mntmAboutTheGame = new JMenuItem("About");
		mntmAboutTheGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "The game is an adaptation of the card game Exploding Kittens for a CMSC 135 requirement by Mabutas and Floresca");
			}
		});
		mnAbout.add(mntmAboutTheGame);
	}
}
